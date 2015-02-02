package puji.wheelmenu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Administrator on 15/2/2.
 */
public class WheelMenuView extends ViewGroup {

    private int mRadius;
    private Paint mPaint;
    private float mDegrees;
    private float mLastX;
    private float mLastY;
    private float degrees;


    public WheelMenuView(Context context) {
        super(context);
        init();
    }

    public WheelMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WheelMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setFocusableInTouchMode(true);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);


    }


    private int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.setClickable(true);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"hello",Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private double getAngle(double x, double y) {

        x = x - getWidth() / 2;
        y = getHeight() / 2 - y;

        switch (getQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
                return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 3:
                return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                return 0;
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        float x = ev.getX();
        float y = ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                return true|super.dispatchTouchEvent(ev);
            case MotionEvent.ACTION_MOVE:

                double start = getAngle(mLastX, mLastY);
                double end = getAngle(ev.getX(), ev.getY());
                degrees = ((float) (start - end) + degrees) % 360;
                requestLayout();
                mLastY = ev.getY();
                mLastX = ev.getX();
                break;

            case MotionEvent.ACTION_UP:

                break;
        }
        return super.dispatchTouchEvent(ev);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int w = 0;
        int h = 0;

        if (widthMode == MeasureSpec.EXACTLY) {
            w = width;
        } else {
            w = getSuggestedMinimumWidth();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            h = height;
        } else {
            h = getSuggestedMinimumHeight();
        }
        mRadius = w < h ? w / 2 : h / 2;
        setMeasuredDimension(mRadius*2, mRadius*2);

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);

            int w1 = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int h1 = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);

            view.measure(w1, h1);
        }

        mDegrees = 360 / getChildCount();
    }

    /**
     * {@inheritDoc}
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int left = (int) (getWidth() / 2 + mRadius * 3 / 4.0f * Math.cos(Math.toRadians(degrees))) - view.getMeasuredWidth() / 2;
            int top = (int) (getHeight() / 2 + mRadius * 3 / 4.0f * Math.sin(Math.toRadians(degrees))) - view.getMeasuredHeight() / 2;
            view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
            //view.setRotation(degrees+90);
            degrees = degrees + mDegrees;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mPaint);
    }
}
