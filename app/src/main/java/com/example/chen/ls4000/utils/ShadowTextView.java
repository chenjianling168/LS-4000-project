package com.example.chen.ls4000.utils;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2018-11-30.
 */

public class ShadowTextView extends android.support.v7.widget.AppCompatTextView {
    private static float DEFAULT_MIN_TEXT_SIZE = 10;
    private static float DEFAULT_MAX_TEXT_SIZE = 85;
    // Attributes
    private Paint testPaint;
    private float minTextSize, maxTextSize;

    private String TAG = "ShadowTextView";

    public ShadowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    public ShadowTextView(Context context) {
        super(context);
    }

    public ShadowTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        refitText(text.toString(), this.getWidth(), this.getHeight());
    }

    private void initialise() {
        testPaint = new Paint();
        testPaint.set(this.getPaint());
        // max size defaults to the intially specified text size unless it is
        // too small
        maxTextSize = this.getTextSize();
        if (maxTextSize <= DEFAULT_MAX_TEXT_SIZE) {
            maxTextSize = DEFAULT_MAX_TEXT_SIZE;
        }
        minTextSize = DEFAULT_MIN_TEXT_SIZE;
    }

    ;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw) {
//            refitText(this.getText().toString(), w);  //原本是需要执行的
        }
    }

    /**
     * Re size the font so the specified text fits in the text box * assuming
     * the text box is the specified width.
     */
    private void refitText(String text, int textWidth, int textHeight) {

        int Length = text.length();
        //直接根据字符长度来调整字体大小  最大长度为20
//        this.setTextSize(DensityUtils.dp2px(this.getContext(), trySize));
//        testPaint.set(this.getPaint());
        if (textWidth > 0) {
            int availableWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();  //获取改TextView的画布可用大小
            float trySize = maxTextSize;
            float scaled = getContext().getResources().getDisplayMetrics().scaledDensity;
//            Log.v(TAG, "availableWidth="+availableWidth + ";scaled="+scaled);
            testPaint.setTextSize(trySize * scaled);   //模拟 注意乘以scaled
            while ((trySize > minTextSize) && (testPaint.measureText(text) > availableWidth)) {

                trySize -= 2;
                Paint.FontMetrics fm = testPaint.getFontMetrics();
                double rowFontHeight = (Math.ceil(fm.descent - fm.top) + 2);
                float scaled1 = (float) (this.getHeight() / rowFontHeight);  //字体的行数   textview的总高度/每行字的高度
                float scaled2 = (float) ((testPaint.measureText(text) / availableWidth));  //也是行数  所有字的总长度/textview的有效宽度

//                Log.v(TAG, "trySize="+trySize + ";testPaint.measureText(text)="+testPaint.measureText(text)+";scaled1="+scaled1+";scaled2="+scaled2+";rowFontHeight="+rowFontHeight);
                if ((scaled2 * rowFontHeight * 1.9) < this.getHeight())  //1.9代表是1.9的行高（1个字体本身，0.9的行距 ，大致差不多，没有实际测过）
                    break;
                if (trySize <= minTextSize) {
                    trySize = minTextSize;
                    break;
                }
                testPaint.setTextSize(trySize * scaled);
            }
            this.setTextSize(trySize);
//            Log.v(TAG, "trySize="+trySize+";maxTextSize="+maxTextSize+";minTextSize="+minTextSize);
        }
    }
}
