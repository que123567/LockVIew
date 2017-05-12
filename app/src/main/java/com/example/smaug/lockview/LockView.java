package com.example.smaug.lockview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by smaug on 2017/5/11.
 */

public class LockView extends View {

    private final static int row = 3;
    private final static int colun = 3;
    private boolean hasInit = false;
    private float width;
    private float height;
    private float offsetY;
    private float offsetX;

    private Bitmap point_normal;
    private Bitmap point_error;
    private Bitmap point_pressed;
    private Point[][] points;

    private float br;
    private float eventY;
    private float eventX;
    private boolean isSelecte;
    private boolean moveOnPoint = true;//默认按在点上
    private boolean isFinish;

    private Paint paint = new Paint();
    private ArrayList<Point> pointList = new ArrayList<>();
    private Point lastPoint;
    private Point middlePoint;
    private OnLockChangeListener onLockChangeListener;

    public LockView(Context context) {
        super(context);
    }

    public LockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!hasInit) {
            initPoints();
            initPaint();
            hasInit = true;
        }
        drawPoints(canvas);
        drawLine(canvas);
    }


    private void initPaint() {
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(12);
    }


    private void drawLine(Canvas canvas) {
        if (pointList.size() > 0) {
            Point a = pointList.get(0);
            for (int i = 1; i < pointList.size(); i++) {
                Point b = pointList.get(i);
                canvas.drawLine(a.x, a.y, b.x, b.y, paint);
                a = b;
            }
            if (!moveOnPoint) { //
                canvas.drawLine(a.x, a.y, eventX, eventY, paint);
            }
        }
    }

    private void drawPoints(Canvas canvas) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
                if (point.getState() == Point.STATU_NORNAL) {
                    //获取当前点的坐标,减去点图片的半径得到left和top的坐标点
                    canvas.drawBitmap(point_normal, point.x - br, point.y - br, null);
                } else if (point.getState() == Point.STATU_PRESSED) {
                    canvas.drawBitmap(point_pressed, point.x - br, point.y - br, null);

                } else if (point.getState() == Point.STATU_ERROR) {
                    canvas.drawBitmap(point_error, point.x - br, point.y - br, null);
                }
            }
        }
    }

    private void initPoints() {
        width = getWidth();
        height = getHeight();
        if (height > width) {//竖屏
            offsetY = (height - width) / 2;
            height = width;
        } else { //横屏
            offsetX = (height - width) / 2;
            width = height;
        }
        //定义9个点的坐标
        points = new Point[row][colun];
        int index = 1;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < colun; j++) {
                points[i][j] = new Point(offsetX + (width / (row + 1)) * (i + 1), offsetY +
                        (width / (colun + 1)) * (j + 1));
                points[i][j].setIndex(index);
                index++;
            }
        }
        point_normal = BitmapFactory.decodeResource(getResources(), R.drawable.point_normal);
        point_pressed = BitmapFactory.decodeResource(getResources(), R.drawable.point_pressed);
        point_error = BitmapFactory.decodeResource(getResources(), R.drawable.point_error);

        br = point_normal.getWidth() / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //没有结束
        isFinish = false;
        //默认用户滑动到了点上
        moveOnPoint = true;
        eventX = event.getX();
        eventY = event.getY();
        Point CheckPoint = null;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                reset();
                CheckPoint = checkPoint(eventX, eventY, br);
                if (CheckPoint != null) {
                    isSelecte = true;
                    CheckPoint.setState(Point.STATU_PRESSED);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //手指滑动
                if (isSelecte) {
                    CheckPoint = checkPoint(eventX, eventY, br);
                    if (CheckPoint != null) {
                        if (!pointList.contains(CheckPoint)) {
                            middlePoint = checkPoint((CheckPoint.x + lastPoint.x) / 2,
                                    (CheckPoint.y + lastPoint.y) / 2, br);
                            if (middlePoint != null) {
                                middlePoint.setState(Point.STATU_PRESSED);
                            }
                        }
                        CheckPoint.setState(Point.STATU_PRESSED);
                        moveOnPoint = true;
                    } else { //画线
                        moveOnPoint = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isFinish = true;
                isSelecte = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                isFinish = true;
                isSelecte = false;
                break;
            default:
                break;
        }
        if (isSelecte && !isFinish && CheckPoint != null) {
            //添加中心点
            if (middlePoint != null && !pointList.contains(middlePoint)) {
                pointList.add(middlePoint);
            }
            //判断当前添加的点是否已经添加过
            if (!pointList.contains(CheckPoint)) {
                lastPoint = CheckPoint; //记录最后一个点
                pointList.add(CheckPoint);
            } else {
                moveOnPoint = false;
            }
        } else if (isFinish) { //结束
            if (pointList != null) {
                if (pointList.size() == 1) {
                    reset();//重置
                } else if (pointList.size() < 5) {//个数不足
                    errorPoint();
                    if (onLockChangeListener != null) {
                        onLockChangeListener.setOnLockError();
                    }
                } else if (pointList.size() >= 5) { //九宫格连接数大于等于5
                    StringBuilder passward = new StringBuilder();
                    for (int i = 0; i < pointList.size(); i++) {
                        int tmpIndex = pointList.get(i).getIndex();
                        passward.append(tmpIndex + "");
                    }
                    if (onLockChangeListener != null)
                        onLockChangeListener.setOnLockSuccessed(passward);
                }
            }
        }

        postInvalidate();// 刷新界面
        return true;
    }

    private void errorPoint() {
        for (int i = 0; i < pointList.size(); i++) {
            pointList.get(i).setState(Point.STATU_ERROR);
        }
    }

    //重置九宫格的点
    private void reset() {
        //将状态置为正常状态
        for (int i = 0; i < pointList.size(); i++) {
            pointList.get(i).setState(Point.STATU_NORNAL);
        }
        pointList.clear();
        middlePoint = null;
    }

    /**
     * 计算按下或者移动的位置是否在九宫格上
     *
     * @param eventX
     * @param eventY
     * @param br
     * @return
     */
    private Point checkPoint(float eventX, float eventY, float br) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
                double distance = getDistance(point.x, point.y, eventX, eventY);
                if (distance < br) {
                    return point;
                }
            }
        }
        return null;
    }

    private double getDistance(float x, float y, float eventX, float eventY) {
        return Math.sqrt(Math.abs(x - eventX) * Math.abs(x - eventX) + Math.abs(y - eventY) *
                Math.abs(y - eventY));
    }

    public interface OnLockChangeListener {
        public void setOnLockSuccessed(StringBuilder passward);

        public void setOnLockError();
    }

    public void setOnLockChangeListeners(OnLockChangeListener onLockChangeListener) {
        this.onLockChangeListener = onLockChangeListener;
    }

}
