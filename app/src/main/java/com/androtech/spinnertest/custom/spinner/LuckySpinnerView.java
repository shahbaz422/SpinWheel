package com.androtech.spinnertest.custom.spinner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androtech.spinnertest.R;
import com.androtech.spinnertest.custom.spinner.model.SpinWheelItemSectionModel;

import java.util.List;
import java.util.Random;

/**
 * Created by mohammed shahbaz on 17/04/24.
 */

public class LuckySpinnerView extends RelativeLayout implements SkinView.SkinRotateListener {
    private int mBackgroundColor;
    private int mTextColor;
    private int mTopTextSize;
    private int mSecondaryTextSize;
    private int mBorderColor;
    private int mTopTextPadding;
    private int mEdgeWidth;
    private Drawable mCenterImage;
    private Drawable mCursorImage;

    private SkinView skinView;
    private ImageView ivCursorView;

    private Boolean playSound;

    private LuckyRoundItemSelectedListener mLuckyRoundItemSelectedListener;

    @Override
    public void rotateDone(int index) {
        if (mLuckyRoundItemSelectedListener != null) {
            mLuckyRoundItemSelectedListener.LuckyRoundItemSelected(index);
        }
    }

    public interface LuckyRoundItemSelectedListener {
        void LuckyRoundItemSelected(int index);
    }

    public void setLuckyRoundItemSelectedListener(LuckyRoundItemSelectedListener listener) {
        this.mLuckyRoundItemSelectedListener = listener;
    }

    public LuckySpinnerView(Context context) {
        super(context);
        init(context, null);
    }

    public LuckySpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * @param ctx
     * @param attrs
     */
    private void init(Context ctx, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.LuckySpinnerView);
            mBackgroundColor = typedArray.getColor(R.styleable.LuckySpinnerView_lswBackgroundColor, 0xffcc0000);
            mTopTextSize = typedArray.getDimensionPixelSize(R.styleable.LuckySpinnerView_lswTopTextSize, (int) LuckyWheelUtils.convertDpToPixel(10f, getContext()));
            mSecondaryTextSize = typedArray.getDimensionPixelSize(R.styleable.LuckySpinnerView_lswSecondaryTextSize, (int) LuckyWheelUtils.convertDpToPixel(20f, getContext()));
            mTextColor = typedArray.getColor(R.styleable.LuckySpinnerView_lswTopTextColor, 0);
            mTopTextPadding = typedArray.getDimensionPixelSize(R.styleable.LuckySpinnerView_lswTopTextPadding, (int) LuckyWheelUtils.convertDpToPixel(10f, getContext())) + (int) LuckyWheelUtils.convertDpToPixel(10f, getContext());
            mCursorImage = typedArray.getDrawable(R.styleable.LuckySpinnerView_lswCursor);
            mCenterImage = typedArray.getDrawable(R.styleable.LuckySpinnerView_lswCenterImage);
            mEdgeWidth = typedArray.getInt(R.styleable.LuckySpinnerView_lswEdgeWidth, 10);
            mBorderColor = typedArray.getColor(R.styleable.LuckySpinnerView_lswEdgeColor, 0);
            playSound = typedArray.getBoolean(R.styleable.LuckySpinnerView_lswPlaySound, false);
            typedArray.recycle();
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.lucky_wheel_layout, this, false);

        skinView = frameLayout.findViewById(R.id.pieView);
        ivCursorView = frameLayout.findViewById(R.id.cursorView);

        skinView.setSkinRotateListener(this);
        skinView.setSkinBackgroundColor(mBackgroundColor);
        skinView.setTopTextPadding(mTopTextPadding);
        skinView.setTopTextSize(mTopTextSize);
        skinView.setPlaySound(playSound);
        skinView.setSecondaryTextSizeSize(mSecondaryTextSize);
        skinView.setSkinCenterImage(mCenterImage);
        skinView.setBorderColor(mBorderColor);
        skinView.setBorderWidth(mEdgeWidth);


        if (mTextColor != 0)
            skinView.setSkinTextColor(mTextColor);

        ivCursorView.setImageDrawable(mCursorImage);

        addView(frameLayout);
    }


    public boolean isTouchEnabled() {
        return skinView.isTouchEnabled();
    }

    public void setTouchEnabled(boolean touchEnabled) {
        skinView.setTouchEnabled(touchEnabled);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //This is to control that the touch events triggered are only going to the SkinView
        for (int i = 0; i < getChildCount(); i++) {
            if (isSkinView(getChildAt(i))) {
                return super.dispatchTouchEvent(ev);
            }
        }
        return false;
    }

    private boolean isSkinView(View view) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < getChildCount(); i++) {
                if (isSkinView(((ViewGroup) view).getChildAt(i))) {
                    return true;
                }
            }
        }
        return view instanceof SkinView;
    }

    public void setLuckyWheelBackgroundColor(int color) {
        skinView.setSkinBackgroundColor(color);
    }

    public void setLuckyWheelCursorImage(int drawable) {
        ivCursorView.setBackgroundResource(drawable);
    }

    public void setLuckyWheelCenterImage(Drawable drawable) {
        skinView.setSkinCenterImage(drawable);
    }

    public void setBorderColor(int color) {
        skinView.setBorderColor(color);
    }

    public void setLuckyWheelTextColor(int color) {
        skinView.setSkinTextColor(color);
    }

    /**
     * @param data
     */
    public void setData(List<SpinWheelItemSectionModel> data) {
        skinView.setData(data);
    }

    /**
     * @param numberOfRound
     */
    public void setRound(int numberOfRound) {
        skinView.setRound(numberOfRound);
    }

    /**
     * @param fixedNumber
     */
    public void setPredeterminedNumber(int fixedNumber) {
        skinView.setPredeterminedNumber(fixedNumber);
    }

    public void startLuckyWheelWithTargetIndex(int index) {
        skinView.rotateTo(index);
    }

    public void startLuckyWheelWithRandomTarget() {
        Random r = new Random();
        skinView.rotateTo(r.nextInt(skinView.getLuckyItemListSize() - 1));
    }
}
