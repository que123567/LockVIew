package com.example.smaug.lockview;

/**
 * Created by smaug on 2017/5/11.
 */

public class Point {
    public float x;
    public float y;
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public static final int STATU_NORNAL = 0;
    public static final int STATU_PRESSED = 1;
    public static final int STATU_ERROR = 2;
    public int state;


    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public Point() {super();}

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
