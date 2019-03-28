/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c01.t1;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

abstract class Shape implements Serializable {
    abstract double getArea();

    void printInfo() {
        System.out.println(getClass().getSimpleName() +
                " has area " + getArea());
    }

}

class me {
    public static void main(String[] args) {
        List<Shape> l = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            if (Math.random() > 0.5)
                l.add(new Circle(Math.random() * 15));
            else l.add(new Rectangle(Math.random() * 85));
        }
        try {
            OutputStream stream = new FileOutputStream("shapes.data");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
            for (Shape shape : l) {
                shape.printInfo();
                objectOutputStream.writeObject(shape);
            }
            objectOutputStream.flush();
            objectOutputStream.close();
            stream.flush();
            stream.close();
        } catch (Exception ignored) {
        }
    }
}

class meme {
    public static void main(String[] args) {
        List<Shape> l = new ArrayList<>();
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("shapes.data"))) {
            Object o;
            while ((o = objectInputStream.readObject()) != null) {
                l.add((Shape) o);
            }
        } catch (EOFException e) {
            for (Shape shape : l) {
                shape.printInfo();
            }
        } catch (IOException | ClassNotFoundException ignored) {
        }
    }
}