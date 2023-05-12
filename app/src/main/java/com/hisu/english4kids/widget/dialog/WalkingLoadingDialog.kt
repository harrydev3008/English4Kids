package com.hisu.english4kids.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import com.hisu.english4kids.databinding.LayoutWalkingLoadingDialogBinding

class WalkingLoadingDialog() {
    private lateinit var context: Context
    private lateinit var dialog: Dialog
    private lateinit var binding: LayoutWalkingLoadingDialogBinding

    constructor(context: Context) : this() {
        this.context = context
        initDialog()
    }

    private fun initDialog() {
        binding =
            LayoutWalkingLoadingDialogBinding.inflate(LayoutInflater.from(context), null, false)

        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)

        val size = (context.resources.displayMetrics.widthPixels * 0.95).toInt()

        val window = dialog.window ?: return
        window.setLayout(size, size)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttr = window.attributes
        windowAttr.gravity = Gravity.CENTER
        window.attributes = windowAttr
    }

    fun setLoadingText(loadingText: String) {
        binding.tvLoadingText.text = loadingText
    }

    fun showDialog() = dialog.show()

    fun dismissDialog() = dialog.dismiss()
}