package com.androtech.spinnertest.custom.spinner;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.List;
import java.util.Random;

import androidx.annotation.IntDef;
import androidx.core.graphics.ColorUtils;

import com.androtech.spinnertest.R;
import com.androtech.spinnertest.custom.spinner.model.SpinWheelItemSectionModel;

/**
 * Created by mohammed shahbaz on 17/04/24.
 */

public class SkinView extends View {
    private RectF mRange = new RectF();
    private int mRadius;
    private Paint mArcPaint;
    private TextPaint mTextPaint;
    private int mCenter;
    private int mPadding;
    private int mTopTextPadding;
    private int mTopTextSize;
    private int mSecondaryTextSize;
    private int mRoundOfNumber = 4;
    private int mEdgeWidth = -1;
    private boolean isRunning = false;
    private int borderColor = 0;
    private int defaultBackgroundColor = 0;
    private int textColor = 0;
    private int predeterminedNumber = -1;
    float viewRotation;
    double fingerRotation;
    long downPressTime, upPressTime;
    double[] newRotationStore = new double[3];

    private Boolean playSound;
    private List<SpinWheelItemSectionModel> mSpinWheelItemSectionModelList;
    private SkinRotateListener mSkinRotateListener;
    private MediaPlayer mediaPlayer;

    public interface SkinRotateListener {
        void rotateDone(int index);
    }

    public SkinView(Context context) {
        super(context);
    }

    public SkinView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSkinRotateListener(SkinRotateListener listener) {
        this.mSkinRotateListener = listener;
    }

    private void init() {
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);


        if (textColor != 0) mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14,
                getResources().getDisplayMetrics()));

        mRange = new RectF(mPadding, mPadding, mPadding + mRadius, mPadding + mRadius);
        prepareMediaPlayer();
    }

    private void prepareMediaPlayer() {
        int soundResourceId = R.raw.taktak;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = MediaPlayer.create(getContext(), soundResourceId);
        mediaPlayer.setVolume(0.5f, 0.5f);
    }

    private void playSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public int getLuckyItemListSize() {
        return mSpinWheelItemSectionModelList.size();
    }
    
    public void setData(List<SpinWheelItemSectionModel> spinWheelItemSectionModelList) {
        this.mSpinWheelItemSectionModelList = spinWheelItemSectionModelList;
        invalidate();
    }

    public void setPlaySound(Boolean play){
        playSound = play;
    }

    public void setSkinBackgroundColor(int color) {
        defaultBackgroundColor = color;
        invalidate();
    }

    public void setBorderColor(int color) {
        borderColor = color;
        invalidate();
    }

    public void setTopTextPadding(int padding) {
        mTopTextPadding = padding;
        invalidate();
    }


    public void setSkinCenterImage(Drawable drawable) {
        invalidate();
    }

    public void setTopTextSize(int size) {
        mTopTextSize = size;
        invalidate();
    }

    public void setSecondaryTextSizeSize(int size) {
        mSecondaryTextSize = size;
        invalidate();
    }


    public void setBorderWidth(int width) {
        mEdgeWidth = width;
        invalidate();
    }

    public void setSkinTextColor(int color) {
        textColor = color;
        invalidate();
    }

    private void drawSkinBackgroundWithBitmap(Canvas canvas, Bitmap bitmap) {
        canvas.drawBitmap(bitmap, null, new Rect(mPadding / 2, mPadding / 2,
                getMeasuredWidth() - mPadding / 2,
                getMeasuredHeight() - mPadding / 2), null);
    }

    /**
     * @param canvas
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mSpinWheelItemSectionModelList == null) {
            return;
        }

        drawBackgroundColor(canvas, defaultBackgroundColor);

        init();

        float mStartAngle = 0;
        float tmpAngle = mStartAngle;
        float sweepAngle = 360f / mSpinWheelItemSectionModelList.size();

        for (int i = 0; i < mSpinWheelItemSectionModelList.size(); i++) {

            if (mSpinWheelItemSectionModelList.get(i).color != 0) {
                mArcPaint.setStyle(Paint.Style.FILL);
                mArcPaint.setColor(mSpinWheelItemSectionModelList.get(i).color);
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
            }

            if (borderColor != 0 && mEdgeWidth > 0) {
                mArcPaint.setStyle(Paint.Style.STROKE);
                mArcPaint.setColor(borderColor);
                mArcPaint.setStrokeWidth(mEdgeWidth);
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
            }

            int sliceColor = mSpinWheelItemSectionModelList.get(i).color != 0 ? mSpinWheelItemSectionModelList.get(i).color : defaultBackgroundColor;

            if (!TextUtils.isEmpty(mSpinWheelItemSectionModelList.get(i).topText))
                drawTopText(canvas, tmpAngle, sweepAngle, mSpinWheelItemSectionModelList.get(i).topText, sliceColor);
            if (!TextUtils.isEmpty(mSpinWheelItemSectionModelList.get(i).secondaryText))
                drawSecondaryText(canvas, tmpAngle, mSpinWheelItemSectionModelList.get(i).secondaryText, sliceColor);

            if (mSpinWheelItemSectionModelList.get(i).icon != 0)
                drawImage(canvas, tmpAngle, BitmapFactory.decodeResource(getResources(),
                        mSpinWheelItemSectionModelList.get(i).icon));
            tmpAngle += sweepAngle;
        }

      //  drawCenterImage(canvas, drawableCenterImage);
    }

    private void drawBackgroundColor(Canvas canvas, int color) {
        if (color == 0)
            return;
        Paint mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(color);
        canvas.drawCircle(mCenter, mCenter, mCenter - 5, mBackgroundPaint);
    }

    /**
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());

        mPadding = getPaddingLeft() == 0 ? 10 : getPaddingLeft();
        mRadius = width - mPadding * 2;

        mCenter = width / 2;

        setMeasuredDimension(width, width);
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param bitmap
     */
    private void drawImage(Canvas canvas, float tmpAngle, Bitmap bitmap) {
        int imgWidth = mRadius / mSpinWheelItemSectionModelList.size();

        float angle = (float) ((tmpAngle + 360f / mSpinWheelItemSectionModelList.size() / 2) * Math.PI / 180);

        int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));

        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2,
                x + imgWidth / 2, y + imgWidth / 2);
        canvas.drawBitmap(bitmap, null, rect, null);
    }

    private void drawCenterImage(Canvas canvas, Drawable drawable) {
        ;
        Bitmap bitmap = LuckyWheelUtils.drawableToBitmap(drawable);
        bitmap = Bitmap.createScaledBitmap(bitmap, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), false);
        canvas.drawBitmap(bitmap, getMeasuredWidth() / 2 - bitmap.getWidth() / 2,
                getMeasuredHeight() / 2 - bitmap.getHeight() / 2, null);
    }

    private boolean isColorDark(int color) {
        double colorValue = ColorUtils.calculateLuminance(color);
        double compareValue = 0.30d;
        return colorValue <= compareValue;
    }


    /**
     * @param canvas
     * @param tmpAngle
     * @param sweepAngle
     * @param mStr
     */
    private void drawTopText(Canvas canvas, float tmpAngle, float sweepAngle, String mStr, int backgroundColor) {
        Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);

        if (textColor == 0)
            mTextPaint.setColor(isColorDark(backgroundColor) ? 0xffffffff : 0xff000000);

        Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
        mTextPaint.setTypeface(typeface);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(mTopTextSize);
        float textWidth = mTextPaint.measureText(mStr);
        int hOffset = (int) (mRadius * Math.PI / mSpinWheelItemSectionModelList.size() / 2 - textWidth / 2);

        int vOffset = mTopTextPadding;

        canvas.drawTextOnPath(mStr, path, hOffset, vOffset, mTextPaint);
    }


    /**
     * @param canvas
     * @param tmpAngle
     * @param mStr
     * @param backgroundColor
     */
    private void drawSecondaryText(Canvas canvas, float tmpAngle, String mStr, int backgroundColor) {
        canvas.save();
        int arraySize = mSpinWheelItemSectionModelList.size();

        if (textColor == 0)
            mTextPaint.setColor(isColorDark(backgroundColor) ? 0xffffffff : 0xff000000);

        Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        mTextPaint.setTypeface(typeface);
        mTextPaint.setTextSize(mSecondaryTextSize);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        float textWidth = mTextPaint.measureText(mStr);

        float initFloat = (tmpAngle + 360f / arraySize / 2);
        float angle = (float) (initFloat * Math.PI / 180);

        int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));

        RectF rect = new RectF(x + textWidth, y,
                x - textWidth, y);

        Path path = new Path();
        path.addRect(rect, Path.Direction.CW);
        path.close();
        canvas.rotate(initFloat + (arraySize / 18f), x, y);
        canvas.drawTextOnPath(mStr, path, mTopTextPadding / 7f, mTextPaint.getTextSize() / 2.75f, mTextPaint);
        canvas.restore();
    }

    /**
     * @return
     */
    private float getAngleOfIndexTarget(int index) {
        return (360f / mSpinWheelItemSectionModelList.size()) * index;
    }

    /**
     * @param numberOfRound
     */
    public void setRound(int numberOfRound) {
        mRoundOfNumber = numberOfRound;
    }


    public void setPredeterminedNumber(int predeterminedNumber) {
        this.predeterminedNumber = predeterminedNumber;
    }

    public void rotateTo(final int index) {
        Random rand = new Random();
        rotateTo(index, (rand.nextInt() * 3) % 2, true);
    }

    /**
     * @param index
     * @param rotation,  spin orientation of the wheel if clockwise or counterclockwise
     * @param startSlow, either animates a slow start or an immediate turn based on the trigger
     */
    public void rotateTo(final int index, @SpinRotation final int rotation, boolean startSlow) {
        if (isRunning) {
            return;
        }

        int rotationAssess = rotation <= 0 ? 1 : -1;

        //If the staring position is already off 0 degrees, make an illusion that the rotation has smoothly been triggered.
        // But this initial animation will just reset the position of the circle to 0 degreees.
        if (getRotation() != 0.0f) {
            setRotation(getRotation() % 360f);
            TimeInterpolator animationStart = startSlow ? new AccelerateInterpolator() : new LinearInterpolator();
            //The multiplier is to do a big rotation again if the position is already near 360.
            float multiplier = getRotation() > 200f ? 2 : 1;
            animate()
                    .setInterpolator(animationStart)
                    .setDuration(500L)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            isRunning = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isRunning = false;
                            setRotation(0);
                            rotateTo(index, rotation, false);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    })
                    .rotation(360f * multiplier * rotationAssess)
                    .start();
            return;
        }

        // This addition of another round count for counterclockwise is to simulate the perception of the same number of spin
        // if you still need to reach the same outcome of a positive degrees rotation with the number of rounds reversed.
        if (rotationAssess < 0) mRoundOfNumber++;

        float targetAngle = ((360f * mRoundOfNumber * rotationAssess) + 270f - getAngleOfIndexTarget(index) - (360f / mSpinWheelItemSectionModelList.size()) / 2);
        ValueAnimator animator = ObjectAnimator.ofFloat(0, targetAngle);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(mRoundOfNumber * 1000 + 2200L); //900
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int previousSection = -1;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                int newSection = (int) (animatedValue / (360f / mSpinWheelItemSectionModelList.size()));

                if(playSound){
                    if (newSection != previousSection) {
                        playSound();
                        previousSection = newSection;
                    }
                }

                setRotation(animatedValue % 360f);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isRunning = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isRunning = false;
                setRotation(getRotation() % 360f);
                if (mSkinRotateListener != null) {
                    mSkinRotateListener.rotateDone(index);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
    }

    public boolean touchEnabled = true;

    public boolean isTouchEnabled() {
        return touchEnabled;
    }

    public void setTouchEnabled(boolean touchEnabled) {
        this.touchEnabled = touchEnabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRunning || !touchEnabled) {
            return false;
        }

        final float x = event.getX();
        final float y = event.getY();

        final float xc = getWidth() / 2.0f;
        final float yc = getHeight() / 2.0f;

        double newFingerRotation;


        double degrees = Math.toDegrees(Math.atan2(x - xc, yc - y));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                viewRotation = (getRotation() + 360f) % 360f;
                fingerRotation = degrees;
                downPressTime = event.getEventTime();
                return true;
            case MotionEvent.ACTION_MOVE:
                newFingerRotation = degrees;

                if (isRotationConsistent(newFingerRotation)) {
                    setRotation(newRotationValue(viewRotation, fingerRotation, newFingerRotation));
                }
                return true;
            case MotionEvent.ACTION_UP:
                newFingerRotation = degrees;
                float computedRotation = newRotationValue(viewRotation, fingerRotation, newFingerRotation);

                fingerRotation = newFingerRotation;

                // This computes if you're holding the tap for too long
                upPressTime = event.getEventTime();
                if (upPressTime - downPressTime > 700L) {
                    // Disregarding the touch since the tap is too slow
                    return true;
                }

                // These operators are added so that fling difference can be evaluated
                // with usually numbers that are only around more or less 100 / -100.
                if (computedRotation <= -250f) {
                    computedRotation += 360f;
                } else if (computedRotation >= 250f) {
                    computedRotation -= 360f;
                }

                double flingDiff = computedRotation - viewRotation;
                if (flingDiff >= 200 || flingDiff <= -200) {
                    if (viewRotation <= -50f) {
                        viewRotation += 360f;
                    } else if (viewRotation >= 50f) {
                        viewRotation -= 360f;
                    }
                }

                flingDiff = computedRotation - viewRotation;

                if (flingDiff <= -60 ||
                        //If you have a very fast flick / swipe, you an disregard the touch difference
                        (flingDiff < 0 && flingDiff >= -59 && upPressTime - downPressTime <= 200L)) {
                    if (predeterminedNumber > -1) {
                        rotateTo(predeterminedNumber, SpinRotation.COUNTERCLOCKWISE, false);
                    } else {
                        rotateTo(getFallBackRandomIndex(), SpinRotation.COUNTERCLOCKWISE, false);
                    }
                }

                if (flingDiff >= 60 ||
                        //If you have a very fast flick / swipe, you an disregard the touch difference
                        (flingDiff > 0 && flingDiff <= 59 && upPressTime - downPressTime <= 200L)) {
                    if (predeterminedNumber > -1) {
                        rotateTo(predeterminedNumber, SpinRotation.CLOCKWISE, false);
                    } else {
                        rotateTo(getFallBackRandomIndex(), SpinRotation.CLOCKWISE, false);
                    }
                }

                return true;
        }
        return super.onTouchEvent(event);
    }

    private float newRotationValue(final float originalWheenRotation, final double originalFingerRotation, final double newFingerRotation) {
        double computationalRotation = newFingerRotation - originalFingerRotation;
        return (originalWheenRotation + (float) computationalRotation + 360f) % 360f;
    }

    private int getFallBackRandomIndex() {
        Random rand = new Random();
        return rand.nextInt(mSpinWheelItemSectionModelList.size() - 1) + 0;
    }

    /**
     * This detects if your finger movement is a result of an actual raw touch event of if it's from a view jitter.
     * This uses 3 events of rotation temporary storage so that differentiation between swapping touch events can be determined.
     *
     * @param newRotValue
     */
    private boolean isRotationConsistent(final double newRotValue) {
        double evalValue = newRotValue;

        if (Double.compare(newRotationStore[2], newRotationStore[1]) != 0) {
            newRotationStore[2] = newRotationStore[1];
        }
        if (Double.compare(newRotationStore[1], newRotationStore[0]) != 0) {
            newRotationStore[1] = newRotationStore[0];
        }

        newRotationStore[0] = evalValue;

        if (Double.compare(newRotationStore[2], newRotationStore[0]) == 0
                || Double.compare(newRotationStore[1], newRotationStore[0]) == 0
                || Double.compare(newRotationStore[2], newRotationStore[1]) == 0

                //Is the middle event the odd one out
                || (newRotationStore[0] > newRotationStore[1] && newRotationStore[1] < newRotationStore[2])
                || (newRotationStore[0] < newRotationStore[1] && newRotationStore[1] > newRotationStore[2])
        ) {
            return false;
        }
        return true;
    }


    @IntDef({
            SpinRotation.CLOCKWISE,
            SpinRotation.COUNTERCLOCKWISE
    })
    @interface SpinRotation {
        int CLOCKWISE = 0;
        int COUNTERCLOCKWISE = 1;
    }
}
