package com.hisu.english4kids.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.hisu.english4kids.databinding.LayoutLoadingBinding

class LoadingDialog() {
    private lateinit var context: Context
    private var gravity: Int = 0
    private lateinit var dialog: Dialog
    private lateinit var binding: LayoutLoadingBinding

    constructor(context: Context, gravity: Int) : this() {
        this.context = context
        this.gravity = gravity

        initDialog()
    }

    private fun initDialog() {
        binding = LayoutLoadingBinding.inflate(LayoutInflater.from(context), null, false)

        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        val window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttr = window.attributes
        windowAttr.gravity = gravity
        window.attributes = windowAttr

        dialog.setCancelable(false)
    }


    fun showDialog() = dialog.show()

    fun dismissDialog() = dialog.dismiss()
}