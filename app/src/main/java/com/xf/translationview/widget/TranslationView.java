package com.xf.translationview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.ColorInt;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class TranslationView extends FrameLayout {

    private static final String TAG = "TranslationView";
    private static final int DEFAULT_COLOR = 0x50000000;
    private int mShadowColor = DEFAULT_COLOR;
    private boolean mIsShow = false;

    private View mTranslationView;
    private ObjectAnimator mShowAni;
    private ObjectAnimator mHideAni;


    public TranslationView(Context context) {
        super(context, null);
    }

    public TranslationView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public TranslationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalStateException("only and should contain two child view");
        }
        mTranslationView = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mTranslationView.layout(0, -mTranslationView.getHeight(), mTranslationView.getWidth(), 0);
    }


    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (mIsShow && child == mTranslationView) {
            canvas.drawColor(mShadowColor);
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (mIsShow && inShadow(ev)) {
                    hide();
                    return true;
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean inShadow(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        final float leftEdge = mTranslationView.getX();
        final float rightEdge = leftEdge + mTranslationView.getWidth();
        final float topEdge = mTranslationView.getHeight();
        final float bottomEdge = getHeight() + topEdge;
        return x > leftEdge && x < rightEdge && y > topEdge && y < bottomEdge;
    }


    public void show() {
        if (!mIsShow) {
            mIsShow = true;
            if (mShowAni == null) {
                mShowAni = ObjectAnimator.ofFloat(mTranslationView, "translationY", mTranslationView.getTranslationY(), mTranslationView.getHeight());
                mShowAni.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        invalidate();
                    }
                });
            }
            mShowAni.start();
        }
    }

    public void hide() {
        if (mIsShow) {
            mIsShow = false;
            if (mHideAni == null) {
                mHideAni = ObjectAnimator.ofFloat(mTranslationView, "translationY", mTranslationView.getTranslationY(), -mTranslationView.getHeight());
                mHideAni.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        invalidate();
                    }
                });
            }
            mHideAni.start();
        }
    }

    public void setShadowColor(@ColorInt int color) {
        mShadowColor = color;
    }
}