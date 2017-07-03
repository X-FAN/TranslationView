package com.xf.tranlationview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.List;

public class TranslationView extends FrameLayout {
    public static final String START = "start";
    public static final String END = "end";
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    private static final String TAG = "TranslationView";
    private static final int DEFAULT_COLOR = 0x50000000;
    private String mDirection = TOP;
    private final int NONE = -1;
    private int mAniTime = 300;
    private int mShadowColor = DEFAULT_COLOR;
    private int mW;
    private int mH;
    private int mShowPosition = NONE;
    private boolean mIsShow = false;

    private ObjectAnimator mShowAni;
    private ObjectAnimator mHideAni;
    private List<ObjectAnimator> mShowAnis = new ArrayList<>();
    private List<ObjectAnimator> mHideAnis = new ArrayList<>();
    private List<View> mMenus = new ArrayList<>();


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
        mAniTime = a.getInteger(R.styleable.TranslationView_duration, 300);
        if (TextUtils.isEmpty(mDirection)) {
            mDirection = TOP;
        }
        a.recycle();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        if (count < 2) {
            throw new IllegalStateException(" contain two child views at least");
        }
        for (int i = 1; i < count; i++) {
            mMenus.add(getChildAt(i));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mW = w;
        mH = h;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int size = mMenus.size();
        int time = mAniTime;
        int height = 0, width = 0;
        for (int i = 0; i < size; i++) {
            View view = mMenus.get(i);
            if (i == 0) {
                height = view.getHeight();
                width = view.getWidth();
            }
            ObjectAnimator showAni = null;
            ObjectAnimator hideAni = null;
            switch (mDirection) {
                case TOP:
                    time = mAniTime * view.getHeight() / height;
                    view.layout(0, -view.getHeight(), view.getWidth(), 0);
                    showAni = ObjectAnimator.ofFloat(view, "translationY", 0, view.getHeight());
                    hideAni = ObjectAnimator.ofFloat(view, "translationY", view.getHeight(), 0);
                    break;
                case BOTTOM:
                    time = mAniTime * view.getHeight() / height;
                    view.layout(0, mH, view.getWidth(), mH + view.getHeight());
                    showAni = ObjectAnimator.ofFloat(view, "translationY", 0, -view.getHeight());
                    hideAni = ObjectAnimator.ofFloat(view, "translationY", -view.getHeight(), 0);
                    break;
                case START:
                    time = mAniTime * view.getWidth() / width;
                    view.layout(-view.getWidth(), 0, 0, view.getHeight());
                    showAni = ObjectAnimator.ofFloat(view, "translationX", 0, view.getWidth());
                    hideAni = ObjectAnimator.ofFloat(view, "translationX", view.getWidth(), 0);
                    break;
                case END:
                    time = mAniTime * view.getWidth() / width;
                    view.layout(mW, 0, mW + view.getWidth(), view.getHeight());
                    showAni = ObjectAnimator.ofFloat(view, "translationX", 0, -view.getWidth());
                    hideAni = ObjectAnimator.ofFloat(view, "translationX", -view.getWidth(), 0);
                    break;
                default:
                    break;
            }
            if (showAni != null) {
                showAni.setDuration(time);
                showAni.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        invalidate();
                    }
                });
                mShowAnis.add(showAni);
            }
            if (hideAni != null) {
                hideAni.setDuration(time);
                hideAni.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        invalidate();
                    }
                });
                mHideAnis.add(hideAni);
            }
        }


    }


    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (mShowPosition != NONE && mMenus.get(mShowPosition) == child) {
            canvas.drawColor(mShadowColor);
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (mShowPosition != NONE && !inTranslationView(ev)) {
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
        View view = mMenus.get(mShowPosition);
        float x = ev.getX();
        float y = ev.getY();
        final float leftEdge = view.getX();
        final float rightEdge = leftEdge + view.getWidth();
        final float topEdge = view.getY();
        final float bottomEdge = view.getHeight() + topEdge;
        return x > leftEdge && x < rightEdge && y > topEdge && y < bottomEdge;
    }


    public void show(int position) {
        if (mShowPosition == NONE) {
            mShowPosition = position;
            mShowAnis.get(position).start();
        } else {
            hide();
        }
    }

    public void hide() {
        if (mShowPosition != NONE) {
            int tempPosition = mShowPosition;
            mShowPosition = NONE;
            mHideAnis.get(tempPosition).start();
        }
    }

    public void setShadowColor(@ColorInt int color) {
        mShadowColor = color;
    }
}