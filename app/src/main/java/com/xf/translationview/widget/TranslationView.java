package com.xf.translationview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class TranslationView extends FrameLayout {

    private static final int DEFAULT_COLOR = 0x50000000;
    private int mShadowColor = DEFAULT_COLOR;
    private boolean mIsShow = false;

    private View mTranslationView;


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
        if (getChildCount() != 2) {
            throw new IllegalStateException("only and should contain two child view");
        }
        mTranslationView = getChildAt(1);
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

    public void show() {
        if (!mIsShow) {
            mIsShow = true;
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mTranslationView, "translationY", mTranslationView.getTranslationY(), mTranslationView.getHeight());
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    invalidate();
                }
            });
            objectAnimator.start();
        }
    }

    public void hide() {
        if (mIsShow) {
            mIsShow = false;
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mTranslationView, "translationY", mTranslationView.getTranslationY(), -mTranslationView.getHeight());
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    invalidate();
                }
            });
            objectAnimator.start();
        }
    }

    public void setShadowColor(@ColorInt int color) {
        mShadowColor = color;
    }
}