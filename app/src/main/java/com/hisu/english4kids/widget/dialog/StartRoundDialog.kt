package com.hisu.english4kids.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.game_play.BaseGamePlay
import com.hisu.english4kids.databinding.LayoutLoadingBinding
import com.hisu.english4kids.databinding.LayoutStartRoundDialogBinding

class StartRoundDialog() {
    private lateinit var context: Context
    private lateinit var dialog: Dialog
    private lateinit var binding: LayoutStartRoundDialogBinding
    private lateinit var round: BaseGamePlay
    private lateinit var title: String
    private lateinit var btnStart: () -> Unit

    constructor(context: Context, title: String) : this() {
        this.context = context
//        this.round = round
        this.title = title
        initDialog()
    }

    fun setStartBtnEvent(btnStart: () -> Unit) {
        this.btnStart = btnStart
    }

    private fun initDialog() {
        binding = LayoutStartRoundDialogBinding.inflate(LayoutInflater.from(context), null, false)

        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        val window = dialog.window ?: return
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttr = window.attributes
        windowAttr.gravity = Gravity.CENTER
        window.attributes = windowAttr

        binding.apply {
            tvRound.text = title
        }

        binding.btnStart.setOnClickListener {
            btnStart.invoke()
        }
    }

    fun showDialog() = dialog.show()

    fun dismissDialog() = dialog.dismiss()
}