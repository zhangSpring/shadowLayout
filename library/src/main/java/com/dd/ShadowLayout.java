package com.dd;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import com.dd.shadow.layout.R;

public class ShadowLayout extends FrameLayout {

    private int mShadowColor;
    private float mShadowRadius;
    private float mCornerRadius;
    private float mDx;
    private float mDy;

    private boolean mInvalidateShadowOnSizeChanged = true;
    private boolean mForceInvalidateShadow = false;

    public ShadowLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(w > 0 && h > 0 && (getBackground() == null || mInvalidateShadowOnSizeChanged || mForceInvalidateShadow)) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(w, h);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mForceInvalidateShadow) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(right - left, bottom - top);
        }
    }

    public void setInvalidateShadowOnSizeChanged(boolean invalidateShadowOnSizeChanged) {
        mInvalidateShadowOnSizeChanged = invalidateShadowOnSizeChanged;
    }

    public void invalidateShadow() {
        mForceInvalidateShadow = true;
        requestLayout();
        invalidate();
    }
    int xPadding;
    int yPadding;
    private void initView(Context context, AttributeSet attrs) {
        initAttributes(context, attrs);

        xPadding = (int) (mShadowRadius + Math.abs(mDx));
        yPadding = (int) (mShadowRadius + Math.abs(mDy));
        setPadding(xPadding, yPadding, xPadding, yPadding);
    }

    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(int w, int h) {
        Bitmap bitmap = createShadowBitmap(w, h, mCornerRadius, mShadowRadius, mDx, mDy, mShadowColor, Color.TRANSPARENT);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
    }


    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray attr = getTypedArray(context, attrs, R.styleable.ShadowLayout);
        if (attr == null) {
            return;
        }

        try {
            mCornerRadius = attr.getDimension(R.styleable.ShadowLayout_sl_cornerRadius, getResources().getDimension(R.dimen.default_corner_radius));
            mShadowRadius = attr.getDimension(R.styleable.ShadowLayout_sl_shadowRadius, getResources().getDimension(R.dimen.default_shadow_radius));
            mDx = attr.getDimension(R.styleable.ShadowLayout_sl_dx, 0);
            mDy = attr.getDimension(R.styleable.ShadowLayout_sl_dy, 0);
            mShadowColor = attr.getColor(R.styleable.ShadowLayout_sl_shadowColor, getResources().getColor(R.color.default_shadow_color));
        } finally {
            attr.recycle();
        }
    }

    private TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight, float cornerRadius, float shadowRadius,
                                      float dx, float dy, int shadowColor, int fillColor) {

        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        RectF shadowRectFirst = new RectF(
                shadowRadius-xPadding,
                shadowRadius-xPadding,
                shadowWidth - shadowRadius+xPadding,
                shadowHeight - shadowRadius+xPadding);

        Paint shadowPaintFirst = new Paint();
        shadowPaintFirst.setAntiAlias(true);
        shadowPaintFirst.setColor(Color.WHITE);
        canvas.drawRect(shadowRectFirst,shadowPaintFirst);

        /**
         *
         * 其实就是讲背景的绘制区域缩小
         */
        RectF shadowRect = new RectF(
                shadowRadius,
                shadowRadius,
                shadowWidth - shadowRadius,
                shadowHeight - shadowRadius);
        /*if (dy > 0) {
            shadowRect.top += dy+dy/4;
            shadowRect.bottom -= dy;
        } else if (dy < 0) {
            shadowRect.top += Math.abs(dy);
            shadowRect.bottom -= Math.abs(dy);
        }

        if (dx > 0) {
            shadowRect.left += dx+dx/4;
            shadowRect.right -= dx;
        } else if (dx < 0) {
            shadowRect.left += Math.abs(dx);
            shadowRect.right -= Math.abs(dx);
        }*/

        RectF shadowRectDown = new RectF();
        shadowRectDown.left = shadowRect.left;
        shadowRectDown.top = shadowRect.bottom;
        shadowRectDown.right = shadowRect.right;
        shadowRectDown.bottom = shadowRect.bottom+20;

        Paint shadowPaint = new Paint();
        //shadowPaint.setAntiAlias(true);
        shadowPaint.setDither(true);
        //shadowPaint.setColor(Color.TRANSPARENT);
        shadowPaint.setColor(0xFFf0002f);
        shadowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        Log.i("显示",shadowRectDown.left+"");
        Log.i("显示",shadowRectDown.top+"");
        Log.i("显示",shadowRectDown.right+"");
        Log.i("显示",shadowRectDown.bottom+"");
      /*  LinearGradient linearGradient = new LinearGradient( (shadowRectDown.right-shadowRectDown.left)/2,shadowRectDown.top,(shadowRectDown.right-shadowRectDown.left)/2, shadowRectDown.bottom, new int[] {
                Color.RED,Color.WHITE},new float[]{0.0f,1.0f},
                Shader.TileMode.CLAMP);*/
        LinearGradient linearGradient = new LinearGradient( (shadowRectDown.right-shadowRectDown.left)/2,shadowRectDown.top,(shadowRectDown.right-shadowRectDown.left)/2, shadowRectDown.bottom, new int[] {
               0xFFf8859b,0xFFf995a9,0xFFfaa7b7 ,0xFFfbbeca ,0xFFfcd0d9,0xFFfde0e6,0xFFfeecf0,0xFFfef5f7,0xFFfffcfd,0xFF000000},null,
                Shader.TileMode.CLAMP);
        shadowPaint.setShader(linearGradient);
        //shadowPaint.setMaskFilter(new BlurMaskFilter(15,BlurMaskFilter.Blur.SOLID));

        if (!isInEditMode()) {
            //shadowPaint.setShadowLayer(shadowRadius, dx, dy,getResources().getColor(R.color.default_shadow_color));
            //shadowPaint.setMaskFilter(new BlurMaskFilter(5,BlurMaskFilter.Blur.SOLID));
        }

        canvas.drawRect(shadowRectDown, shadowPaint);

        return output;
    }

}
