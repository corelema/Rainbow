package com.cocorporation.rainbow.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.cocorporation.rainbow.R;
import com.cocorporation.rainbow.utils.Point;
import com.cocorporation.rainbow.utils.Util;

/**
 * Created by Corentin on 6/21/2016.
 */
public class Rainbow extends View {
    public static final String TAG = "Rainbow";


    private static final int DEFAULT_LONG_POINTER_SIZE = 50;

    private boolean mMultiColor;
    private int mSelectedColor;
    private Integer mCursor;
    private float mArcWidth = 0.0f;
    private float mArcHeight = 0.0f;
    private float mArcStrokeWidth;

    int[] mRainbowColors;

    private Paint mArcPaint;
    private Paint mSelectedColorIndicatorPaint;

    private RectF mRect;
    private float mRainbowRadius;

    private float circleX = 0;
    private float circleY = 0;

    private GestureDetector mDetector;

    public Rainbow(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Rainbow,
                0, 0);

        try {
            mMultiColor = a.getBoolean(R.styleable.Rainbow_multicolor, false);
            mSelectedColor = a.getColor(R.styleable.Rainbow_selectedColor, Color.RED);
            mCursor = a.getInteger(R.styleable.Rainbow_cursor, 0);
            mArcWidth = a.getDimension(R.styleable.Rainbow_arcWidth, 0.0f);
            mArcHeight = a.getDimension(R.styleable.Rainbow_arcHeight, 0.0f);
            mArcStrokeWidth = a.getDimension(R.styleable.Rainbow_arcStrokeWidth, 10);
        } finally {
            a.recycle();
        }

        init();
    }

    /* Getters and Setters */

    public boolean ismMultiColor() {
        return mMultiColor;
    }

    public void setMultiColor(boolean multiColor) {
        this.mMultiColor = multiColor;
        invalidate();
        //requestLayout();
    }

    public int getmSelectedColor() {
        return mSelectedColor;
    }

    public void setSelectedColor(int color) {
        this.mSelectedColor = color;
        invalidate();
        //requestLayout();
    }

    public Integer getmCursor() {
        return mCursor;
    }

    public void setCursor(Integer cursor) {
        this.mCursor = cursor;
        invalidate();
        //requestLayout();
    }

    public float getArcStrokeWidth() {
        return mArcStrokeWidth;
    }

    public void setArcStrokeWidth(float arcStrokeWidth) {
        this.mArcStrokeWidth = arcStrokeWidth;
    }

    /*Custom view methods*/

    private void init() {
        initArcPaint();
        initSelectedColorIndicatorPaint();
        initRainbowColors();

        mRect = new RectF();
        mDetector = new GestureDetector(Rainbow.this.getContext(), new GestureListener());
    }

    private void initArcPaint(){
        mArcPaint = new Paint();
        mArcPaint.setColor(mSelectedColor);
        mArcPaint.setStrokeWidth(mArcStrokeWidth);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStrokeCap(Paint.Cap.BUTT);
        mArcPaint.setStyle(Paint.Style.STROKE);
    }

    private void initSelectedColorIndicatorPaint() {
        mSelectedColorIndicatorPaint = new Paint();
        mSelectedColorIndicatorPaint.setStrokeWidth(mArcStrokeWidth);
        mSelectedColorIndicatorPaint.setAntiAlias(true);
        mSelectedColorIndicatorPaint.setStyle(Paint.Style.STROKE);
    }

    private void initRainbowColors() {
        mRainbowColors = new int[]{
                Color.RED,
                Color.rgb(255, 127, 0),
                Color.YELLOW,
                Color.GREEN,
                Color.BLUE,
                Color.rgb(75, 0, 130),
                Color.rgb(148, 0, 211)
        };
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) mArcWidth * 2;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return (int) mArcHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        int h = resolveSizeAndState(MeasureSpec.getSize(w)/2, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        // Try for a width based on our minimum
//        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
//
//        int w = Math.max(minw, MeasureSpec.getSize(widthMeasureSpec));
//
//        // Whatever the width ends up being, ask for a height that would let the arc
//        // get as big as it can
//        int minh = (w - (int) mArcWidth) + getPaddingBottom() + getPaddingTop();
//        int h = Math.min(MeasureSpec.getSize(heightMeasureSpec), minh);
//
//        setMeasuredDimension(w, h);
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mRect.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), getHeight() * 2 - getPaddingTop());

        mRainbowRadius = mRect.centerX() - mRect.left;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mArcPaint.setColor(mSelectedColor);
        mArcPaint.setShader(null);
        canvas.drawArc(mRect, 180, 180, false, mArcPaint);

        float center = getWidth()/2;
        float bottom = getHeight();

        //mArcPaint.setShader(new LinearGradient(getWidth(), 0, 0, 0, colors, null, Shader.TileMode.CLAMP));

        SweepGradient sweepGradient = new SweepGradient(center, bottom, mRainbowColors, new float[] {0.5f, 0.583f, 0.667f, 0.75f, 0.833f, 0.917f, 1f});
        mArcPaint.setShader(sweepGradient);

        canvas.drawArc(mRect, 170, 200, false, mArcPaint);
        //canvas.drawCircle(center, bottom, 50, mArcPaint);

        float x1 = center;
        float y1 = bottom;
        float x2 = circleX;
        float y2 = circleY;
        float distanceToSelectedColorIndicator = mRainbowRadius + mArcStrokeWidth + mArcStrokeWidth / 4;

        Point selectedColorIndicatorCenter = Util.extendSegmentToDistance(x1, y1, x2, y2, distanceToSelectedColorIndicator);

        float angle = Util.angleBetweenSegments(x1, y1, selectedColorIndicatorCenter.x, selectedColorIndicatorCenter.y);
        int selectedColorIndicatorColor = Util.colorFromAngle(angle, mRainbowColors);

        mSelectedColorIndicatorPaint.setColor(selectedColorIndicatorColor);
        //For motion event debugging
        canvas.drawCircle(selectedColorIndicatorCenter.x, selectedColorIndicatorCenter.y, mArcStrokeWidth / 4, mSelectedColorIndicatorPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "Action = " + event.getAction());
        boolean result = mDetector.onTouchEvent(event);
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                circleX = event.getAxisValue(MotionEvent.AXIS_X);
                circleY = event.getAxisValue(MotionEvent.AXIS_Y);

                invalidate();
                requestLayout();

                //Log.d(TAG, "RawX = " + event.getRawX());
                //Log.d(TAG, "RawY = " + event.getRawY());
                //Log.d(TAG, "x = " + event.getAxisValue(MotionEvent.AXIS_X));
                //Log.d(TAG, "y = " + event.getAxisValue(MotionEvent.AXIS_Y));
                result = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                result = true;
            }
        }
        return result;
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }
    }
}
