package com.ssk.wanandroid.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;
import com.ssk.wanandroid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shisenkun on 2019-06-29.
 * 原github地址：https://github.com/viviant1224/CircleTextImage
 * 这里修改了随机颜色的值，修改显示单字显示，字体大小自适应问题
 */
public class CircleTextImageView extends AppCompatImageView {

    private static final float DEFAULT_TEXT_SIZE_RATIO = 0.6f;

    private int mCircleColor = Color.RED;//Default background color
    private int mCircleTextColor = Color.WHITE;//Text color
    private boolean mUseRandomBackgroundColor = false;//Use random background color
    private boolean mSubFirstCharacter = false;//Is whether intercept first character
    private int mTextSize;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mPaintTextForeground;
    private Paint mPaintTextBackground;
    private float textSizeRatio = DEFAULT_TEXT_SIZE_RATIO;
    private Paint.FontMetrics mFontMetrics;

    private String mText;
    private int mRadius;
    private int mCenterX;
    private int mCenterY;

    private List<String> mRandomColorList = new ArrayList<String>();

    public CircleTextImageView(Context context) {
        super(context);
        init();
    }

    public CircleTextImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    public CircleTextImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleTextImageView);
        mCircleColor = typedArray.getColor(R.styleable.CircleTextImageView_circle_color, Color.RED);
        mCircleTextColor = typedArray.getColor(R.styleable.CircleTextImageView_circle_text_color, Color.WHITE);
        mUseRandomBackgroundColor = typedArray.getBoolean(R.styleable.CircleTextImageView_random_color, false);
        mSubFirstCharacter = typedArray.getBoolean(R.styleable.CircleTextImageView_sub_first_character, false);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.CircleTextImageView_circle_text_size, -1);

        typedArray.recycle();
    }

    private void init() {

        mPaintTextForeground = new Paint();
        mPaintTextForeground.setColor(mCircleTextColor);
        mPaintTextForeground.setAntiAlias(true);
        mPaintTextForeground.setTextAlign(Paint.Align.CENTER);

        mPaintTextBackground = new Paint();
        mPaintTextBackground.setColor(mCircleTextColor);
        mPaintTextBackground.setAntiAlias(true);
        mPaintTextBackground.setStyle(Paint.Style.FILL);

        mRandomColorList.add("#4cc154");
        mRandomColorList.add("#f15e3f");
        mRandomColorList.add("#FF9600");
        mRandomColorList.add("#444444");
        mRandomColorList.add("#FF9B07");
        mRandomColorList.add("#39a3cc");
        mRandomColorList.add("#69A89B");
        mRandomColorList.add("#C969C9");
        mRandomColorList.add("#7EB5A9");
        mRandomColorList.add("#FF8533");
        mRandomColorList.add("#BB4B00");
        mRandomColorList.add("#1556B8");
        mRandomColorList.add("#5F8844");
        mRandomColorList.add("#656D5F");
        mRandomColorList.add("#7A525A");
        mRandomColorList.add("#AA2222");
        mRandomColorList.add("#A38B29");
        mRandomColorList.add("#FF77C9");

        if (mUseRandomBackgroundColor) {
            mPaint.setColor(Color.parseColor(getRandomColor()));
        } else {
            mPaint.setColor(mCircleColor);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 100 * 2;
        int desiredHeight = 100 * 2;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (null != mText && !mText.trim().equals("")) {
            int realSize = (int) mPaintTextForeground.measureText(mText,0, mText.length()) + 60;


            if (realSize < 200) {
                realSize = 100 * 2;
            }

            //Measure Width
            if (widthMode == MeasureSpec.EXACTLY) {
                //Must be this size
                width = widthSize;
            } else if (widthMode == MeasureSpec.AT_MOST) {
                //Can't be bigger than...
                width = realSize;
            } else {
                //Be whatever you want
                width = realSize;
            }

            //Measure Height
            if (heightMode == MeasureSpec.EXACTLY) {
                //Must be this size
                height = heightSize;
            } else if (heightMode == MeasureSpec.AT_MOST) {
                //Can't be bigger than...
                height = realSize;
            } else {
                //Be whatever you want
                height = realSize;
            }
        } else {
            //Measure Width
            if (widthMode == MeasureSpec.EXACTLY) {
                //Must be this size
                width = widthSize;
            } else if (widthMode == MeasureSpec.AT_MOST) {
                //Can't be bigger than...
                width = Math.min(desiredWidth, widthSize);
            } else {
                //Be whatever you want
                width = desiredWidth;
            }

            //Measure Height
            if (heightMode == MeasureSpec.EXACTLY) {
                //Must be this size
                height = heightSize;
            } else if (heightMode == MeasureSpec.AT_MOST) {
                //Can't be bigger than...
                height = Math.min(desiredHeight, heightSize);
            } else {
                //Be whatever you want
                height = desiredHeight;
            }

        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //get padding
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        //deal padding
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;
        int radius = Math.min(width, height) / 2;

        if (null != mText && !mText.trim().equals("")) {
            drawText(canvas);
        } else {
            canvas.drawCircle(width / 2, height / 2, radius, mPaint);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int contentWidth = w - paddingLeft - getPaddingRight();
        int contentHeight = h - paddingTop - getPaddingBottom();
        mRadius = contentWidth < contentHeight ? contentWidth / 2 : contentHeight / 2;
        mCenterX = paddingLeft + mRadius;
        mCenterY = paddingTop + mRadius;
        refreshTextSizeConfig();
    }

    private void drawText(Canvas canvas) {
        mPaintTextBackground.setColor(mCircleTextColor);
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
        canvas.drawText(mText, 0, mText.length(), mCenterX, mCenterY + Math.abs(mFontMetrics.top + mFontMetrics.bottom) / 2, mPaintTextForeground);

    }

    private void refreshTextSizeConfig() {
        if(mTextSize == -1) {
            mPaintTextForeground.setTextSize(textSizeRatio * 2 * mRadius);
        }
        mFontMetrics = mPaintTextForeground.getFontMetrics();

    }

    /**
     * setText for the view.
     * @param text
     */
    public void setText4CircleImage(String text) {
        if (mSubFirstCharacter) {
            this.mText = subFirstCharacter(text);
        } else {
            this.mText = text;
        }
        invalidate();

    }

    public String getRandomColor() {

        return mRandomColorList.get((int)(Math.random() * mRandomColorList.size()));
    }

    public String subFirstCharacter(String str) {
        char firstChar = 0;
        for(int i = 0; i < str.length(); i++) {
            firstChar = str.charAt(i);
            if(!Character.isWhitespace(firstChar)) {
                break;
            }
        }
        if (Character.isLetter(firstChar)) {
            return Character.toUpperCase(firstChar)+"";
        } else {
            return firstChar +"";
        }
    }

}

