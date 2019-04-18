/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c01.t1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

interface Arraylizeable<T> {
    byte[] toByteArray();

    T toInstance(byte[] bytes);
}

public class t13 {
    public static void main(String[] args) {
        Student s = new Student("12345678", "12345677", 123);
        byte[] b = s.toByteArray();
        Student x = new Student(b);
        System.out.println(s);
        System.out.println(x);
        fclass<Student> fc = new fclass<Student>(Student.byteSize, s);
        System.out.println(fc.size());
        fc.add(s);
        fc.write();
        System.out.println(fc.get(0));
    }
}

class Student implements Arraylizeable {
    public static final int byteSize = 29;
    String id;
    String name;
    int score;

    Student(String id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    Student(byte[] dat) {
        ByteBuffer buffer = ByteBuffer.wrap(dat);
        byte[] tmp = new byte[8];
        buffer.get(tmp, 0, 8);
        id = new String(tmp);
        tmp = new byte[20];
        buffer.get(tmp, 0, 20);
        name = new String(tmp);
        score = buffer.get(28) & (0xff);
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(29);
        buffer.put(ByteBuffer.allocate(8).put(id.getBytes()).array());
        buffer.put(ByteBuffer.allocate(20).put(name.getBytes()).array());
        buffer.put(28, (byte) score);
        return buffer.array();
    }

    @Override
    public Student toInstance(byte[] bytes) {
        return new Student(bytes);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}

class fclass<T extends Arraylizeable> {
    public boolean autoCommit = false;
    String fn = "TestData/score.data";
    File file;
    int blockSize;
    List<T> items;
    T deArraylizer;

    fclass(int blockSize, T deArraylizer) {
        this.blockSize = blockSize;
        this.deArraylizer = deArraylizer;
        file = new File(fn);
        if (file.exists()) {
            read();
        } else {
            try {
                file.createNewFile();
                items = new ArrayList<>();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    T[] getAll() {
        return (T[]) items.toArray();
    }

    int size() {
        return items.size();
    }

    void add(T o) {
        items.add(o);
        if (autoCommit) write();
    }

    T get(int index) {
        return items.get(index);
    }

    void replace(int index, T o) {
        items.set(index, o);
        if (autoCommit) write();
    }

    void del(int index) {
        items.remove(index);
        if (autoCommit) write();
    }

    void write() {
        if (items.size() == 0) {
            try {
                file.delete();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        ByteBuffer buffer = ByteBuffer.allocate(blockSize * items.size());
        for (T item : items) {
            buffer.put(item.toByteArray());
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(buffer.array());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    void read() {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            int b;
            byte[] buf = new byte[blockSize];
            items = new ArrayList<>();
            while ((b = fileInputStream.read()) != -1) {
                buf[0] = (byte) b;
                for (int i = 1; i < buf.length; i++) {
                    buf[i] = (byte) fileInputStream.read();
                }
                items.add((T) deArraylizer.toInstance(buf));
            }
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}