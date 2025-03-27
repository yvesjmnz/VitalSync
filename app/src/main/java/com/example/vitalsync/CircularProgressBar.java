package com.example.vitalsync;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressBar extends View {

    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private int progress = 0;
    private int maxProgress = 1000; // Default max progress (can be updated dynamically)

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xFFE0E0E0);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(30);
        backgroundPaint.setAntiAlias(true);

        foregroundPaint = new Paint();
        foregroundPaint.setColor(0xFF00C853);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(30);
        foregroundPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 15;


        canvas.drawCircle(width / 2, height / 2, radius, backgroundPaint);


        float sweepAngle = 360f * progress / maxProgress;


        canvas.drawArc(
                width / 2 - radius,
                height / 2 - radius,
                width / 2 + radius,
                height / 2 + radius,
                -90,
                sweepAngle,
                false,
                foregroundPaint
        );
    }


    public void setProgress(int progress) {
        this.progress = Math.min(progress, maxProgress);
        invalidate();
    }


    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }
}
