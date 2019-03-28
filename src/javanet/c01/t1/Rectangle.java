/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c01.t1;

class Rectangle extends Shape {
    private double x;

    Rectangle(double x) {
        this.x = x;
    }

    @Override
    double getArea() {
        return x;
    }
}
