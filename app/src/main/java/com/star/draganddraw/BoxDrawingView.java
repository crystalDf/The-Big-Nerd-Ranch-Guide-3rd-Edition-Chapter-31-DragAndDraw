package com.star.draganddraw;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class BoxDrawingView extends View {

    private static final String TAG = "BoxDrawingView";

    private static final String PARCEL_BUNDLE_KEY = "parcelBundleKey";
    private static final String SERIAL_BUNDLE_KEY = "serialBundleKey";

    private Box mCurrentBox;
    private ArrayList<Box> mBoxes = new ArrayList<>();

    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    public BoxDrawingView(Context context) {
        this(context, null);
    }

    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22FF0000);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xFFF8EFE0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);

        for (Box box : mBoxes) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);

            canvas.save();

            canvas.rotate(box.getAngle(), box.getOrigin().x, box.getOrigin().y);

            canvas.drawRect(left, top, right, bottom, mBoxPaint);

            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        PointF current = new PointF(event.getX(), event.getY());

        String action = "";

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";

                mCurrentBox = new Box(current);
                mBoxes.add(mCurrentBox);

                break;

            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";

                if (mCurrentBox != null) {
                    mCurrentBox.setCurrent(current);

                    if (event.getPointerCount() > 1) {
                        float angle = (float) Math.toDegrees(Math.atan(
                                (event.getY(1) - mCurrentBox.getOrigin().y) /
                                        (event.getX(1) - mCurrentBox.getOrigin().x)
                            )
                        );
                        mCurrentBox.setAngle(angle);
                    }

                    invalidate();
                }

                break;

            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";

                mCurrentBox = null;

                break;

            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                break;
        }

        Log.i(TAG, action + " at x = " + current.x + ", y = " + current.y);

        return true;
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable(PARCEL_BUNDLE_KEY, super.onSaveInstanceState());
        bundle.putSerializable(SERIAL_BUNDLE_KEY, mBoxes);

        return bundle;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onRestoreInstanceState(Parcelable state) {

        if (state.getClass() == Bundle.class) {
            Bundle bundle = (Bundle) state;

            super.onRestoreInstanceState(bundle.getParcelable(PARCEL_BUNDLE_KEY));
            mBoxes = (ArrayList<Box>) bundle.getSerializable(SERIAL_BUNDLE_KEY);
        }
    }
}
