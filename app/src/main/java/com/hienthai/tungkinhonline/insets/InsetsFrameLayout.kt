package com.hienthai.tungkinhonline.insets

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.hienthai.tungkinhonline.R

class InsetsFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.InsetsLayout)
        val type = array.getInt(R.styleable.InsetsLayout_insetsType, -1)
        val padding = array.getInt(R.styleable.InsetsLayout_insetsPadding, -1)
        val ignoringVisibility = array.getBoolean(R.styleable.InsetsLayout_ignoringVisibility, false)
        if (type != -1 && padding != -1) {
            applySystemWindowInsetsPadding(
                applyLeft = padding and PADDING_LEFT == PADDING_LEFT,
                applyTop = padding and PADDING_TOP == PADDING_TOP,
                applyRight = padding and PADDING_RIGHT == PADDING_RIGHT,
                applyBottom = padding and PADDING_BOTTOM == PADDING_BOTTOM,
                ignoringVisibility = ignoringVisibility,
                type = type
            )
        }
        array.recycle()
    }
}