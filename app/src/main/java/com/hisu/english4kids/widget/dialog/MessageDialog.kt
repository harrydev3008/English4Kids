package com.hisu.english4kids.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import androidx.core.content.ContextCompat
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.game_play.BaseGamePlay
import com.hisu.english4kids.databinding.LayoutLoadingBinding
import com.hisu.english4kids.databinding.LayoutMessageDialogBinding
import com.hisu.english4kids.databinding.LayoutStartRoundDialogBinding

class MessageDialog() {
    private lateinit var context: Context
    private lateinit var dialog: Dialog
    private lateinit var binding: LayoutMessageDialogBinding
    private lateinit var title: String
    private lateinit var subTitle: String
    private var isLocked:Boolean = false
    private lateinit var btnStartCallback: () -> Unit

    constructor(context: Context, title: String, subTitle: String, isLocked: Boolean = false) : this() {
        this.context = context
        this.title = title
        this.subTitle = subTitle
        this.isLocked = isLocked
        initDialog()
    }

    fun setStartBtnEvent(btnStartCallback: () -> Unit) {
        this.btnStartCallback = btnStartCallback
    }

    private fun initDialog() {
        binding = LayoutMessageDialogBinding.inflate(LayoutInflater.from(context), null, false)

        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        val window = dialog.window ?: return
        val size = (context.resources.displayMetrics.widthPixels * 0.95).toInt()
        window.setLayout(size, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttr = window.attributes
        windowAttr.gravity = Gravity.CENTER
        window.attributes = windowAttr

        initView()
    }

    private fun initView() = binding.apply {
        tvTitle.text = title
        tvSubtitle.text = subTitle

        if(isLocked) {
            cardParent.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_blue_gray))
            cardParent.strokeColor = ContextCompat.getColor(context, R.color.gray_af)
            tvTitle.setTextColor(ContextCompat.getColor(context, R.color.gray))
            tvSubtitle.setTextColor(ContextCompat.getColor(context, R.color.gray))
            btnStart.visibility = View.GONE
        } else {
            cardParent.setCardBackgroundColor(ContextCompat.getColor(context, R.color.classic))
            tvTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
            tvSubtitle.setTextColor(ContextCompat.getColor(context, R.color.white))
            btnStart.visibility = View.VISIBLE
            btnStart.text = context.getString(R.string.start_round_to_earn_pattern)

            btnStart.setOnClickListener {
                btnStartCallback.invoke()
            }
        }
    }

    fun showDialog() = dialog.show()

    fun dismissDialog() = dialog.dismiss()
}