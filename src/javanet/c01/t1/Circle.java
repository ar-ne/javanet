/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */


package javanet.c01.t1;

class Circle extends Shape {
    private double x;

    Circle(double x) {
        this.x = x;
    }

    @Override
    double getArea() {
        return x;
    }
}
