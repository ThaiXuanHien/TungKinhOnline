package com.hienthai.tungkinhonline.insets

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

const val PADDING_TOP = 1
const val PADDING_LEFT = 2
const val PADDING_RIGHT = 4
const val PADDING_BOTTOM = 8

fun View.applySystemWindowInsetsPadding(
    applyLeft: Boolean = false,
    applyTop: Boolean = false,
    applyRight: Boolean = false,
    applyBottom: Boolean = false,
    ignoringVisibility: Boolean = false,
    type: Int = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemWindowInsets = if (ignoringVisibility) insets.getInsetsIgnoringVisibility(type) else insets.getInsets(type)

        val leftInset = if (applyLeft) systemWindowInsets.left else 0
        val topInset = if (applyTop) systemWindowInsets.top else 0
        val rightInset = if (applyRight) systemWindowInsets.right else 0
        val bottomInset = if (applyBottom) systemWindowInsets.bottom else 0

        view.updatePadding(
            left = leftInset,
            top = topInset,
            right = rightInset,
            bottom = bottomInset
        )

        insets
    }
}