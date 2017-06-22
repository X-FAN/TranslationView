package com.xf.tranlationview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.ColorInt;
import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class TranslationView extends FrameLayout {
    public static final String START = "start";
    public static final String END = "end";
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    private static final String TAG = "TranslationView";
    private static final int DEFAULT_COLOR = 0x50000000;
    private String mDirection = TOP;
    private int mShadowColor = DEFAULT_COLOR;
    private int mW;
    private int mH;
    private boolean mIsShow = false;


    private View mTranslationView;
    private ObjectAnimator mShowAni;
    private ObjectAnimator mHideAni;


    public TranslationView(Context context) {
        this(context, null);
    }

    public TranslationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TranslationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TranslationView);
        mDirection = a.getString(R.styleable.TranslationView_direction);
        if (TextUtils.isEmpty(mDirection)) {
            mDirection = TOP;
        }
        a.recycle();
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mW = w;
        mH = h;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        switch (mDirection) {
            case TOP:
                mTranslationView.layout(0, -mTranslationView.getHeight(), mTranslationView.getWidth(), 0);
                break;
            case BOTTOM:
                mTranslationView.layout(0, mH, mTranslationView.getWidth(), mH + mTranslationView.getHeight());
                break;
            case START:
                mTranslationView.layout(-mTranslationView.getWidth(), 0, 0, mTranslationView.getHeight());
                break;
            case END:
                mTranslationView.layout(mW, 0, mW + mTranslationView.getWidth(), mTranslationView.getHeight());
                break;
            default:
                break;
        }
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
                if (mIsShow && !inTranslationView(ev)) {
                    hide();
                    return true;
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 在内容区域中
     *
     * @param ev
     * @return
     */
    private boolean inTranslationView(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        final float leftEdge = mTranslationView.getX();
        final float rightEdge = leftEdge + mTranslationView.getWidth();
        final float topEdge = mTranslationView.getY();
        final float bottomEdge = mTranslationView.getHeight() + topEdge;
        return x > leftEdge && x < rightEdge && y > topEdge && y < bottomEdge;
    }


    public void show() {
        if (!mIsShow) {
            mIsShow = true;
            if (mShowAni == null) {
                switch (mDirection) {
                    case TOP:
                        mShowAni = ObjectAnimator.ofFloat(mTranslationView, "translationY", 0, mTranslationView.getHeight());
                        break;
                    case BOTTOM:
                        mShowAni = ObjectAnimator.ofFloat(mTranslationView, "translationY", 0, -mTranslationView.getHeight());
                        break;
                    case START:
                        mShowAni = ObjectAnimator.ofFloat(mTranslationView, "translationX", 0, mTranslationView.getWidth());
                        break;
                    case END:
                        mShowAni = ObjectAnimator.ofFloat(mTranslationView, "translationX", 0, -mTranslationView.getWidth());
                        break;
                    default:
                        break;
                }
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
                switch (mDirection) {
                    case TOP:
                    case BOTTOM:
                        mHideAni = ObjectAnimator.ofFloat(mTranslationView, "translationY", mTranslationView.getTranslationY(), 0);
                        break;
                    case START:
                    case END:
                        mHideAni = ObjectAnimator.ofFloat(mTranslationView, "translationX", mTranslationView.getTranslationX(), 0);
                        break;
                }
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