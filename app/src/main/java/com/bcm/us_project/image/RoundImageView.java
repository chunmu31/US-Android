package com.bcm.us_project.image;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

// Profile RoundImageView
public class RoundImageView extends AppCompatImageView {

    public static float radius = 36.0f;

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path clipPath = new Path();
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}
