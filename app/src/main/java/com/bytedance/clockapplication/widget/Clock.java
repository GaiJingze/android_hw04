package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private int mWidth, mCenterX, mCenterY, mRadius;

    /**
     * properties
     */
    private int centerInnerColor;
    private int centerOuterColor;

    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;

    private int degreesColor;

    private int hoursValuesColor;

    private int numbersColor;

    private boolean mShowAnalog = true;

    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        size = Math.min(widthWithoutPadding, heightWithoutPadding);
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {

        this.centerInnerColor = Color.LTGRAY;
        this.centerOuterColor = DEFAULT_PRIMARY_COLOR;

        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;

        numbersColor = Color.WHITE;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = Math.min(getWidth(), getHeight());

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;

        if (mShowAnalog) {
            drawDegrees(canvas);
            drawHoursValues(canvas);
            drawNeedles(canvas);
            drawCenter(canvas);
        } else {
            drawNumbers(canvas);
        }
        postInvalidateDelayed(1000);

    }

    private void drawDegrees(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6 /* Step */) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(mCenterX - layout.getWidth() / 2f, mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private void drawHoursValues(Canvas canvas) {
        // Default Color:
        // - hoursValuesColor
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(hoursValuesColor);
        paint.setTextSize(mWidth*0.05f);
        for(int i=0; i<12; i++)
        {
            String text="";
             switch (i)
             {
                 case 0:text="XII";
                     break;
                 case 1:text="I";
                     break;
                 case 2:text="II";
                     break;
                 case 3:text="III";
                     break;
                 case 4:text="IV";
                     break;
                 case 5:text="V";
                     break;
                 case 6:text="VI";
                     break;
                 case 7:text="VII";
                     break;
                 case 8:text="VIII";
                     break;
                 case 9:text="IX";
                     break;
                 case 10:text="X";
                     break;
                 case 11:text="XI";
                     break;

             }
            Rect textBound = new Rect();
            paint.getTextBounds(text, 0, text.length(), textBound);
            float px = (float)(mCenterX - mCenterX * 0.8f * Math.sin(Math.toRadians(i*30))-textBound.width() / 2);
            float py = (float)(mCenterX - mCenterX * 0.8f * Math.cos(Math.toRadians(i*30)));
            canvas.drawText(text, px, py, paint);
        }
    }

    private void drawNeedles(final Canvas canvas) {
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        float hourAngle = (hour + (float) minute / 60) * 360 / 12;
        float minuteAngle = (minute + (float) second / 60) * 360 / 60;
        int secondAngle = second * 6;

        float px, py;
        canvas.save();
        Paint paint= new Paint();
        paint.setColor(hoursNeedleColor);
        paint.setStrokeWidth(mWidth*0.015f);
        px = (float)(mCenterX + mCenterX * 0.6f * Math.sin(Math.toRadians(hourAngle)));
        py = (float)(mCenterX - mCenterX * 0.6f* Math.cos(Math.toRadians(hourAngle)));
        canvas.drawLine(mCenterX, mCenterY, px, py, paint);
        canvas.restore();

        canvas.save();
        paint.setColor(minutesNeedleColor);
        paint.setStrokeWidth(mWidth*0.01f);
        px = (float)(mCenterX + mCenterX * 0.8f  * Math.sin(Math.toRadians(minuteAngle)));
        py = (float)(mCenterX - mCenterX * 0.8f * Math.cos(Math.toRadians(minuteAngle)));
        canvas.drawLine(mCenterX, mCenterY, px, py, paint);
        canvas.restore();

        canvas.save();
        paint.setColor(secondsNeedleColor);
        paint.setStrokeWidth(mWidth*0.006f);
        px = (float) (mCenterX + mCenterX * 0.9f  * Math.sin(Math.toRadians(secondAngle)));
        py = (float) (mCenterX - mCenterX * 0.9f * Math.cos(Math.toRadians(secondAngle)));
        canvas.drawLine(mCenterX, mCenterY, px, py, paint);
        canvas.restore();
    }

    /**
     * Draw Center Dot
     *
     * @param canvas
     */
    private void drawCenter(Canvas canvas) {
        Paint paint= new Paint();
        paint.setColor(centerOuterColor);
        canvas.drawCircle(mCenterX,mCenterY,mWidth*0.02f,paint);
        paint.setColor(centerInnerColor);
        canvas.drawCircle(mCenterX,mCenterY,mWidth*0.016f,paint);
    }

    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        invalidate();
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }

}