package com.shyra.chat.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.shyra.chat.R;

import static android.R.attr.radius;

/**
 * Created by Rachit Goyal for ShyRa on 10/2/16.
 */

public class TimelineSeparatorView extends View {

    // Circle and text colors
    private int viewColor, labelColor;
    // Label text
    private String viewText;

    // Paint for drawing custom view
    private Paint viewPaint;

    public TimelineSeparatorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        viewPaint = new Paint();
        // Get the attributes specified in attrs.xml using the name we included
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.TimelineSeparatorView, 0, 0);

        try {
            // Get the text and colors specified using the names in attrs.xml
            viewText = typedArray.getString(R.styleable.TimelineSeparatorView_viewLabel);
            viewColor = typedArray.getColor(R.styleable.TimelineSeparatorView_viewColor, 0); // 0 is default
            labelColor = typedArray.getInteger(R.styleable.TimelineSeparatorView_labelColor, 0);
        } finally {
            typedArray.recycle();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the View

        // Get half of the width and height to display text at the center
        int viewWidthHalf = this.getMeasuredWidth() / 2;
        int viewHeightHalf = this.getMeasuredHeight() / 2;

        // Drawing the view
        viewPaint.setStyle(Paint.Style.FILL);
        viewPaint.setAntiAlias(true);

        viewPaint.setColor(viewColor);
        canvas.drawCircle(viewWidthHalf - 10, viewHeightHalf - 10, radius, viewPaint);

        // Drawing the text on the view
        viewPaint.setColor(labelColor);
        viewPaint.setTextAlign(Paint.Align.CENTER);
        viewPaint.setTextSize(50);
        canvas.drawText(viewText, viewWidthHalf, viewHeightHalf, viewPaint);

    }

    public int getViewColor() {
        return viewColor;
    }

    public void setViewColor(int viewColor) {
        this.viewColor = viewColor;
        invalidate();
        requestLayout();
    }

    public int getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
        invalidate();
        requestLayout();
    }

    public String getViewText() {
        return viewText;
    }

    public void setViewText(String viewText) {
        this.viewText = viewText;
        invalidate();
        requestLayout();
    }
}
