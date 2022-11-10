package com.hienthai.tungkinhonline

import android.app.AlertDialog
import android.content.Context
import android.view.View

fun Context.showAlertDialog(
    title: String? = null,
    msg: String = "",
    positiveButton: String? = null,
    negativeButton: String? = null,
    onPositiveButtonClick: View.OnClickListener? = null,
    onNegativeButtonClick: View.OnClickListener? = null,
    isCanTouchOutside: Boolean = false,
    isCancelable: Boolean = true
) {
    val alertDialog: AlertDialog? = AlertDialog.Builder(this).create()
    alertDialog?.setTitle(title)
    alertDialog?.setMessage(msg)
    alertDialog?.setCanceledOnTouchOutside(isCanTouchOutside)
    alertDialog?.setCancelable(isCancelable)
    alertDialog?.setButton(
        AlertDialog.BUTTON_POSITIVE, positiveButton
    ) { dialog, _ ->
        run {
            dialog.dismiss()
            onPositiveButtonClick?.onClick(null)
        }
    }
    onNegativeButtonClick?.let {
        alertDialog?.setButton(
            AlertDialog.BUTTON_NEGATIVE, negativeButton
        ) { dialog, _ ->
            run {
                dialog.dismiss()
                it.onClick(null)
            }
        }
    }
    alertDialog?.show()
}