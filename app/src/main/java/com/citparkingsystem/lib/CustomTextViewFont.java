package com.citparkingsystem.lib;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Walter Ybanez on 9/9/2017.
 */

public class CustomTextViewFont extends android.support.v7.widget.AppCompatTextView {

    public CustomTextViewFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTextViewFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextViewFont(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Roboto-Thin.ttf");
        setTypeface(tf);
    }

}
