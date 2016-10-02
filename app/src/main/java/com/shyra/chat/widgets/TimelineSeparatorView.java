package com.shyra.chat.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.shyra.chat.R;

/**
 * Custom View for connector between Timeline Events
 * Created by Rachit Goyal for ShyRa on 10/2/16.
 */

public class TimelineSeparatorView extends View {

    private static final String TAG = TimelineSeparatorView.class.getSimpleName();

    private Context mContext;

    private int topCircleFillColor, topCircleBorderColor, topCircleCenterDotColor,
            bottomCircleFillColor, bottomCircleBorderColor, bottomCircleCenterDotColor,
            connectorLineColor;

    private float topCircleWidth, topCircleBorderWidth, topCircleCenterDotWidth,
            bottomCircleWidth, bottomCircleBorderWidth, bottomCircleCenterDotWidth,
            connectorLineWidth;

    private boolean direction; // True = left, False = right

    private Paint mViewPaint;

    public TimelineSeparatorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        mViewPaint = new Paint();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.TimelineSeparatorView, 0, 0);

        try {
            topCircleFillColor = typedArray.getColor(R.styleable.TimelineSeparatorView_topCircleFillColor, ContextCompat.getColor(mContext, R.color.default_circle_fill));
            topCircleBorderColor = typedArray.getColor(R.styleable.TimelineSeparatorView_topCircleBorderColor, ContextCompat.getColor(mContext, R.color.default_circle_border));
            topCircleCenterDotColor = typedArray.getColor(R.styleable.TimelineSeparatorView_topCircleCenterDotColor, ContextCompat.getColor(mContext, R.color.default_center_dot));
            bottomCircleFillColor = typedArray.getColor(R.styleable.TimelineSeparatorView_bottomCircleFillColor, ContextCompat.getColor(mContext, R.color.default_circle_fill));
            bottomCircleBorderColor = typedArray.getColor(R.styleable.TimelineSeparatorView_bottomCircleBorderColor, ContextCompat.getColor(mContext, R.color.default_circle_border));
            bottomCircleCenterDotColor = typedArray.getColor(R.styleable.TimelineSeparatorView_bottomCircleCenterDotColor, ContextCompat.getColor(mContext, R.color.default_center_dot));
            connectorLineColor = typedArray.getColor(R.styleable.TimelineSeparatorView_connectorLineColor, ContextCompat.getColor(mContext, R.color.default_connector_line));

            topCircleWidth = typedArray.getDimension(R.styleable.TimelineSeparatorView_topCircleWidth, 10);
            topCircleBorderWidth = typedArray.getDimension(R.styleable.TimelineSeparatorView_topCircleBorderWidth, 2);
            topCircleCenterDotWidth = typedArray.getDimension(R.styleable.TimelineSeparatorView_topCircleCenterDotWidth, 2);
            bottomCircleWidth = typedArray.getDimension(R.styleable.TimelineSeparatorView_bottomCircleWidth, 10);
            bottomCircleBorderWidth = typedArray.getDimension(R.styleable.TimelineSeparatorView_bottomCircleBorderWidth, 2);
            bottomCircleCenterDotWidth = typedArray.getDimension(R.styleable.TimelineSeparatorView_bottomCircleCenterDotWidth, 2);
            connectorLineWidth = typedArray.getDimension(R.styleable.TimelineSeparatorView_connectorLineWidth, 6);

            direction = typedArray.getBoolean(R.styleable.TimelineSeparatorView_direction, true);
        } finally {
            typedArray.recycle();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the View

        // Get half of the width and height to display text at the center
        int width = this.getMeasuredWidth();
        int height = this.getMeasuredHeight();
        int viewWidthHalf = this.getMeasuredWidth() / 2;

        // Drawing Connecting Line
        mViewPaint.setColor(connectorLineColor);
        mViewPaint.setStyle(Paint.Style.STROKE);

        float startX, startY, stopX, stopY;
        if (direction) {
            startX = viewWidthHalf - topCircleWidth / 2 + topCircleBorderWidth;
            stopX = bottomCircleWidth;
        } else {
            startX = viewWidthHalf + topCircleWidth / 2 - topCircleBorderWidth;
            stopX = width - bottomCircleWidth;
        }
        startY = topCircleWidth - topCircleBorderWidth;
        stopY = height - bottomCircleWidth + (bottomCircleBorderWidth / 2);

        /*mViewPaint.setShader(new LinearGradient(startX, startY, stopX, stopY, topCircleFillColor, connectorLineColor, Shader.TileMode.MIRROR));*/
        mViewPaint.setStrokeWidth(connectorLineWidth);
        canvas.drawLine(startX, startY, stopX, stopY, mViewPaint);
        Log.d(TAG, "onDraw: Connector Drawn");


        // Drawing Top Circle
        float topCircleCenterY = (topCircleWidth / 2) + (topCircleBorderWidth / 2);
        // Drawing Top Circle Border
        mViewPaint.setAntiAlias(true);
        mViewPaint.setStyle(Paint.Style.STROKE);
        mViewPaint.setStrokeWidth(topCircleBorderWidth);
        mViewPaint.setColor(topCircleBorderColor);
        float radius = topCircleWidth / 2;
        canvas.drawCircle(viewWidthHalf, topCircleCenterY, radius, mViewPaint);
        Log.d(TAG, "onDraw: Top Circle Border Drawn");

        // Drawing Top Circle Fill
        mViewPaint.setStyle(Paint.Style.FILL);
        mViewPaint.setStrokeWidth(0);
        mViewPaint.setColor(topCircleFillColor);
        radius = topCircleWidth / 2 - (topCircleBorderWidth / 2);
        canvas.drawCircle(viewWidthHalf, topCircleCenterY, radius, mViewPaint);
        Log.d(TAG, "onDraw: Top Circle Fill Drawn");

        // Drawing Top Circle Center Dot
        mViewPaint.setColor(topCircleCenterDotColor);
        radius = topCircleCenterDotWidth / 2;
        canvas.drawCircle(viewWidthHalf, topCircleCenterY, radius, mViewPaint);
        Log.d(TAG, "onDraw: Top Circle Center Dot Drawn");
        // End - Drawing Top Circle

        // Drawing Bottom Circle
        float bottomCircleCenterX, bottomCircleCenterY;
        if (direction) {
            bottomCircleCenterX = bottomCircleWidth / 2 + bottomCircleBorderWidth;
            bottomCircleCenterY = (height - (bottomCircleWidth / 2)) - bottomCircleBorderWidth;
        } else {
            bottomCircleCenterX = (width - (bottomCircleWidth / 2)) - bottomCircleBorderWidth;
            bottomCircleCenterY = (height - (bottomCircleWidth / 2)) - bottomCircleBorderWidth;
        }
        // Drawing Bottom Circle Border
        mViewPaint.setStyle(Paint.Style.STROKE);
        mViewPaint.setStrokeWidth(bottomCircleBorderWidth);
        mViewPaint.setColor(bottomCircleBorderColor);
        radius = bottomCircleWidth / 2;
        canvas.drawCircle(bottomCircleCenterX, bottomCircleCenterY, radius, mViewPaint);
        Log.d(TAG, "onDraw: Bottom Circle Border Drawn");

        // Drawing Bottom Circle Fill
        mViewPaint.setStyle(Paint.Style.FILL);
        mViewPaint.setStrokeWidth(0);
        mViewPaint.setColor(bottomCircleFillColor);
        radius = bottomCircleWidth / 2 - (bottomCircleBorderWidth / 2);
        canvas.drawCircle(bottomCircleCenterX, bottomCircleCenterY, radius, mViewPaint);
        Log.d(TAG, "onDraw: Bottom Circle Fill Drawn");

        // Drawing Bottom Circle Center Dot
        mViewPaint.setColor(bottomCircleCenterDotColor);
        radius = bottomCircleCenterDotWidth / 2;
        canvas.drawCircle(bottomCircleCenterX, bottomCircleCenterY, radius, mViewPaint);
        Log.d(TAG, "onDraw: Bottom Circle Center Dot Drawn");
        // End - Drawing Bottom Circle
    }

    public int getTopCircleFillColor() {
        return topCircleFillColor;
    }

    public void setTopCircleFillColor(int topCircleFillColor) {
        this.topCircleFillColor = topCircleFillColor;
        invalidate();
        requestLayout();
    }

    public int getTopCircleBorderColor() {
        return topCircleBorderColor;
    }

    public void setTopCircleBorderColor(int topCircleBorderColor) {
        this.topCircleBorderColor = topCircleBorderColor;
        invalidate();
        requestLayout();
    }

    public int getTopCircleCenterDotColor() {
        return topCircleCenterDotColor;
    }

    public void setTopCircleCenterDotColor(int topCircleCenterDotColor) {
        this.topCircleCenterDotColor = topCircleCenterDotColor;
        invalidate();
        requestLayout();
    }

    public int getBottomCircleFillColor() {
        return bottomCircleFillColor;
    }

    public void setBottomCircleFillColor(int bottomCircleFillColor) {
        this.bottomCircleFillColor = bottomCircleFillColor;
        invalidate();
        requestLayout();
    }

    public int getBottomCircleBorderColor() {
        return bottomCircleBorderColor;
    }

    public void setBottomCircleBorderColor(int bottomCircleBorderColor) {
        this.bottomCircleBorderColor = bottomCircleBorderColor;
        invalidate();
        requestLayout();
    }

    public int getBottomCircleCenterDotColor() {
        return bottomCircleCenterDotColor;
    }

    public void setBottomCircleCenterDotColor(int bottomCircleCenterDotColor) {
        this.bottomCircleCenterDotColor = bottomCircleCenterDotColor;
        invalidate();
        requestLayout();
    }

    public int getConnectorLineColor() {
        return connectorLineColor;
    }

    public void setConnectorLineColor(int connectorLineColor) {
        this.connectorLineColor = connectorLineColor;
        invalidate();
        requestLayout();
    }

    public float getTopCircleWidth() {
        return topCircleWidth;
    }

    public void setTopCircleWidth(float topCircleWidth) {
        this.topCircleWidth = topCircleWidth;
        invalidate();
        requestLayout();
    }

    public float getTopCircleBorderWidth() {
        return topCircleBorderWidth;
    }

    public void setTopCircleBorderWidth(float topCircleBorderWidth) {
        this.topCircleBorderWidth = topCircleBorderWidth;
        invalidate();
        requestLayout();
    }

    public float getTopCircleCenterDotWidth() {
        return topCircleCenterDotWidth;
    }

    public void setTopCircleCenterDotWidth(float topCircleCenterDotWidth) {
        this.topCircleCenterDotWidth = topCircleCenterDotWidth;
        invalidate();
        requestLayout();
    }

    public float getBottomCircleWidth() {
        return bottomCircleWidth;
    }

    public void setBottomCircleWidth(float bottomCircleWidth) {
        this.bottomCircleWidth = bottomCircleWidth;
        invalidate();
        requestLayout();
    }

    public float getBottomCircleBorderWidth() {
        return bottomCircleBorderWidth;
    }

    public void setBottomCircleBorderWidth(float bottomCircleBorderWidth) {
        this.bottomCircleBorderWidth = bottomCircleBorderWidth;
        invalidate();
        requestLayout();
    }

    public float getBottomCircleCenterDotWidth() {
        return bottomCircleCenterDotWidth;
    }

    public void setBottomCircleCenterDotWidth(float bottomCircleCenterDotWidth) {
        this.bottomCircleCenterDotWidth = bottomCircleCenterDotWidth;
        invalidate();
        requestLayout();
    }

    public float getConnectorLineWidth() {
        return connectorLineWidth;
    }

    public void setConnectorLineWidth(float connectorLineWidth) {
        this.connectorLineWidth = connectorLineWidth;
        invalidate();
        requestLayout();
    }

    public boolean isDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
        invalidate();
        requestLayout();
    }
}
