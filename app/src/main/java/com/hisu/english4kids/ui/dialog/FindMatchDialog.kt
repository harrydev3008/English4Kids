package com.hisu.english4kids.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.hisu.english4kids.databinding.LayoutFindMatchBinding

class FindMatchDialog() {
    private lateinit var context: Context
    private var gravity: Int = 0
    private lateinit var dialog: Dialog
    private lateinit var binding: LayoutFindMatchBinding

    constructor(context: Context, gravity: Int): this() {
        this.context = context
        this.gravity = gravity

        initDialog()
    }

    private fun initDialog() {
        binding = LayoutFindMatchBinding.inflate(LayoutInflater.from(context), null, false)

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

        handleAcceptButton()
        handleCancelButton()
    }

    private fun handleAcceptButton() = binding.btnAccept.setOnClickListener {
        //todo: impl later
        dismissDialog()
    }

    private fun handleCancelButton() = binding.btnCancel.setOnClickListener {
        //todo: cancel socket call impl later
        dismissDialog()
    }

    fun showDialog() = dialog.show()

    fun dismissDialog() = dialog.dismiss()
}