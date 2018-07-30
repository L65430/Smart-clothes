package com.example.l.l_clothers.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import com.example.l.l_clothers.R;

/**
 * Created by L on 2017/6/4.
 */

public class SwitchButton extends android.support.v7.widget.AppCompatCheckBox {

    private int ANIMATION_FRAME_DURATION=1000/60;
    private Paint mPaint;

    private ViewParent mParent;

    private Bitmap mBottom;

    private Bitmap mCurBtnPic;

    private Bitmap mBtnPressed;

    private Bitmap mBtnNormal;

    private Bitmap mFrame;

    private Bitmap mMask;

    private RectF mSaveLayerRectF;

    private PorterDuffXfermode mXfermode;

    private float mFirstDownY;

    private float mFirstDownX;

    private float mRealPos;

    private float mBtnPos;

    private float mBtnOnPos;

    private float mBtnOffPos;

    private float mMaskWidth;

    private float mMaskHeight;

    private float mBtnWidth;

    private float mBtnInitPos;

    private int mClickTimeout;

    private int mTouchSlop;

    private final int MAX_ALPHA = 255;

    private int mAlpha = MAX_ALPHA;

    private boolean mChecked = false;

    private boolean mBroadcasting;

    private boolean mTurningOn;

    private PerformClick mPerformClick;

    private OnCheckedChangeListener mOnCheckedChangeListener;

    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;

    private boolean mAnimating;

    private final float VELOCITY = 350;

    private float mVelocity;

    private final float EXTENDED_OFFSET_Y = 15;

    private float mExtendOffsetY;

    private float mAnimationPosition;

    private float mAnimatedVelocity;

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkboxStyle);
    }

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        Resources resources = context.getResources();
        System.out.println("---");
        // get viewConfiguration
        mClickTimeout = ViewConfiguration.getPressedStateDuration()
                + ViewConfiguration.getTapTimeout();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        // get Bitmap
        mBottom = BitmapFactory.decodeResource(resources, R.drawable.switch_bottom);
        mBtnPressed = BitmapFactory.decodeResource(resources, R.drawable.switch_btn_pressed);
        mBtnNormal = BitmapFactory.decodeResource(resources, R.drawable.switch_btn_unpressed);
        mFrame = BitmapFactory.decodeResource(resources, R.drawable.switch_frame);
        mMask = BitmapFactory.decodeResource(resources, R.drawable.switch_mask);
        mCurBtnPic = mBtnNormal;

        mBtnWidth = mBtnPressed.getWidth();
        mMaskWidth = mMask.getWidth();
        mMaskHeight = mMask.getHeight();

        mBtnOffPos = mBtnWidth / 2;
        mBtnOnPos = mMaskWidth - mBtnWidth / 2;

        mBtnPos = mChecked ? mBtnOnPos : mBtnOffPos;
        mRealPos = getRealPos(mBtnPos);
        //获取屏幕密度
        final float density = getResources().getDisplayMetrics().density;
        mVelocity = (int) (VELOCITY * density + 0.5f);
        mExtendOffsetY = (int) (EXTENDED_OFFSET_Y * density + 0.5f);
        //构�?�矩�?
        mSaveLayerRectF = new RectF(0, mExtendOffsetY, mMask.getWidth(), mMask.getHeight()
                + mExtendOffsetY);
        //两张图片相交时的模式
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    @Override
    //表是否启�?
    public void setEnabled(boolean enabled) {
        mAlpha = enabled ? MAX_ALPHA : MAX_ALPHA / 2;
        super.setEnabled(enabled);
    }
    @Override

    public boolean isChecked() {
        return mChecked;
    }
    @Override
    //切换可见状态
    public void toggle() {
        setChecked(!mChecked);
    }

    /**
     * 内部调用此方法设置checked状态，此方法会延迟执行各种回调函数，保证动画的流畅
     *
     * @param checked
     */
    private void setCheckedDelayed(final boolean checked) {
        this.postDelayed(new Runnable() {

            @Override
            public void run() {
                setChecked(checked);
            }
        }, 10);
    }

    /**
     * <p>
     * Changes the checked state of this button.
     * </p>
     *
     * @param checked true to check the button, false to uncheck it
     */
    @Override

    public void setChecked(boolean checked) {

        if (mChecked != checked) {
            mChecked = checked;

            mBtnPos = checked ? mBtnOnPos : mBtnOffPos;
            mRealPos = getRealPos(mBtnPos);
            //自动清屏
            invalidate();

            // Avoid infinite recursions if setChecked() is called from a
            // listener
            if (mBroadcasting) {
                return;
            }

            mBroadcasting = true;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(SwitchButton.this, mChecked);
            }
            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener.onCheckedChanged(SwitchButton.this, mChecked);
            }

            mBroadcasting = false;
        }
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes.
     *
     *
     */
    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes. This callback is used for internal purpose only.
     *
     * @param listener the callback to call on checked state change
     * @hide
     */

    void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeWidgetListener = listener;
    }
    /**手机屏幕触屏事件*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        float deltaX = Math.abs(x - mFirstDownX);
        float deltaY = Math.abs(y - mFirstDownY);
        switch (action) {
            /**屏幕长按事件/
             case MotionEvent.ACTION_DOWN:
             attemptClaimDrag();
             mFirstDownX = x;
             mFirstDownY = y;
             mCurBtnPic = mBtnPressed;
             mBtnInitPos = mChecked ? mBtnOnPos : mBtnOffPos;
             break;
             /**手机屏幕滑动事件*/
            case MotionEvent.ACTION_MOVE:
                /**计算滑动时间*/
                float time = event.getEventTime() - event.getDownTime();
                mBtnPos = mBtnInitPos + event.getX() - mFirstDownX;
                if (mBtnPos >= mBtnOffPos) {
                    mBtnPos = mBtnOffPos;
                }
                if (mBtnPos <= mBtnOnPos) {
                    mBtnPos = mBtnOnPos;
                }
                mTurningOn = mBtnPos > (mBtnOffPos - mBtnOnPos) / 2 + mBtnOnPos;

                mRealPos = getRealPos(mBtnPos);
                break;
            /**手机屏幕按下抬起*/
            case MotionEvent.ACTION_UP:
                mCurBtnPic = mBtnNormal;
                time = event.getEventTime() - event.getDownTime();
                if (deltaY < mTouchSlop && deltaX < mTouchSlop && time < mClickTimeout) {
                    if (mPerformClick == null) {
                        mPerformClick = new PerformClick();
                    }
                    if (!post(mPerformClick)) {
                        performClick();
                    }
                } else {
                    startAnimation(!mTurningOn);
                }
                break;
        }

        invalidate();
        return isEnabled();
    }

    private final class PerformClick implements Runnable {
        public void run() {
            performClick();
        }
    }
    /**重写perforClick函数，载入动画效果*/
    @Override
    public boolean performClick() {
        startAnimation(!mChecked);
        return true;
    }

    /**
     * Tries to claim the user's drag motion, and requests disallowing any
     * ancestors from stealing events in the drag.
     */
    /**不可拦截控件上的触摸事件*/
    private void attemptClaimDrag() {
        mParent = getParent();
        if (mParent != null) {
            mParent.requestDisallowInterceptTouchEvent(true);
        }
    }

    /**
     * 将btnPos转换成RealPos
     *
     * @param btnPos
     * @return
     */
    private float getRealPos(float btnPos) {
        return btnPos - mBtnWidth / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.saveLayerAlpha(mSaveLayerRectF, mAlpha, Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        // 绘制蒙板
        canvas.drawBitmap(mMask, 0, mExtendOffsetY, mPaint);
        mPaint.setXfermode(mXfermode);

        // 绘制底部图片
        canvas.drawBitmap(mBottom, mRealPos, mExtendOffsetY, mPaint);
        mPaint.setXfermode(null);
        // 绘制边框
        canvas.drawBitmap(mFrame, 0, mExtendOffsetY, mPaint);

        // 绘制按钮
        canvas.drawBitmap(mCurBtnPic, mRealPos, mExtendOffsetY, mPaint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) mMaskWidth, (int) (mMaskHeight + 2 * mExtendOffsetY));
    }

    private void startAnimation(boolean turnOn) {
        mAnimating = true;
        mAnimatedVelocity = turnOn ? -mVelocity : mVelocity;
        mAnimationPosition = mBtnPos;

        new SwitchAnimation().run();
    }

    private void stopAnimation() {
        mAnimating = false;
    }

    private final class SwitchAnimation implements Runnable {

        @Override
        public void run() {
            if (!mAnimating) {
                return;
            }
            doAnimation();
            FrameAnimationController.requestAnimationFrame(this);
        }
    }

    private void doAnimation() {
        mAnimationPosition += mAnimatedVelocity * ANIMATION_FRAME_DURATION/1000;
        if (mAnimationPosition <= mBtnOnPos) {
            stopAnimation();
            mAnimationPosition = mBtnOnPos;
            setCheckedDelayed(true);
        } else if (mAnimationPosition >= mBtnOffPos) {
            stopAnimation();
            mAnimationPosition = mBtnOffPos;
            setCheckedDelayed(false);
        }
        moveView(mAnimationPosition);
    }

    private void moveView(float position) {
        mBtnPos = position;
        mRealPos = getRealPos(mBtnPos);
        invalidate();
    }
}
