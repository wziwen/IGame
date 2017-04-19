package com.wenziwen.igame.bean;

import android.graphics.Rect;

import java.io.Serializable;

/**
 * Created by wen on 2016/11/10.
 * 动作描述类
 */
public class ActionBean implements Serializable{

    private String photoPath;
    private Rectangle targetRectangle;
    private String targetImgPath;
    private Point clickPoint;
    private long waitTime;
    private int clickWaitTime;

    public int getClickWaitTime() {
        return clickWaitTime;
    }

    public void setClickWaitTime(int clickWaitTime) {
        this.clickWaitTime = clickWaitTime;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Rectangle getTargetRectangle() {
        return targetRectangle;
    }

    public void setTargetRectangle(Rectangle targetRectangle) {
        this.targetRectangle = targetRectangle;
    }

    public String getTargetImgPath() {
        return targetImgPath;
    }

    public void setTargetImgPath(String targetImgPath) {
        this.targetImgPath = targetImgPath;
    }

    public Point getClickPoint() {
        return clickPoint;
    }

    public void setClickPoint(Point clickPoint) {
        this.clickPoint = clickPoint;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public static class Point implements Serializable{
        public double x;
        public double y;

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    public static class Rectangle implements Serializable{
        public Rectangle() {
        }

        public Rectangle(Rect rect) {
            this.left = rect.left;
            this.top = rect.top;
            this.right = rect.right;
            this.bottom = rect.bottom;
        }

        @Override
        public String toString() {
            return "Rectangle{" +
                    "left=" + left +
                    ", top=" + top +
                    ", right=" + right +
                    ", bottom=" + bottom +
                    '}';
        }

        public double left;
        public double top;
        public double right;
        public double bottom;
    }


}
