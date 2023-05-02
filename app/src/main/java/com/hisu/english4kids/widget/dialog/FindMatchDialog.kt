package com.hisu.english4kids.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.airbnb.lottie.RenderMode
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.LayoutFindMatchBinding

class FindMatchDialog() {
    private lateinit var context: Context
    private var gravity: Int = 0
    private lateinit var dialog: Dialog
    private lateinit var binding: LayoutFindMatchBinding
    private val RESEND_DELAY_TIME = 10 * 1000L
    private lateinit var acceptCallback: () -> Unit
    private lateinit var cancelCallback: () -> Unit

    constructor(context: Context, gravity: Int): this() {
        this.context = context
        this.gravity = gravity
        initDialog()
    }

    private fun initDialog() {
        binding = LayoutFindMatchBinding.inflate(LayoutInflater.from(context), null, false)
        binding.cimvAvatar.renderMode = RenderMode.HARDWARE

        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)

        val window = dialog.window ?: return
        //80% of device width
        val width = (context.resources.displayMetrics.widthPixels * 0.8).toInt()

        window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttr = window.attributes
        windowAttr.gravity = gravity
        window.attributes = windowAttr

        handleAcceptButton()
        handleCancelButton()
        handleFindAgainButton()

        countDownTimer.start()
    }

    fun setAcceptCallBack(acceptCallback: () -> Unit): FindMatchDialog {
        this.acceptCallback = acceptCallback
        return this
    }

    fun setCancelCallBack(cancelCallback: () -> Unit): FindMatchDialog {
        this.cancelCallback = cancelCallback
        return this
    }

    private fun handleAcceptButton() = binding.btnAccept.setOnClickListener {
        //todo: impl later
        dismissDialog()
        acceptCallback.invoke()
    }

    private fun handleCancelButton() = binding.btnCancel.setOnClickListener {
        //todo: cancel socket call impl later
        dismissDialog()
        cancelCallback.invoke()
    }

    private fun handleFindAgainButton() = binding.btnFindAgain.setOnClickListener {
        binding.btnFindAgain.visibility = View.GONE
        binding.cimvAvatar.resumeAnimation()
        //todo: impl later
    }

    fun showDialog() = dialog.show()

    private fun dismissDialog() = dialog.dismiss()

    private val countDownTimer =  object : CountDownTimer(RESEND_DELAY_TIME, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.pbProgress.progress = binding.pbProgress.progress + 1
        }

        override fun onFinish() {
            binding.cimvAvatar.pauseAnimation()
            binding.cimvAvatar.setAnimation(R.raw.swords)
            binding.cimvAvatar.resumeAnimation()
            binding.cimvAvatar.repeatCount = 3

            binding.btnAccept.visibility = View.VISIBLE
            binding.tvLoading.text = context.getString(R.string.found_opponent)
        }
    }
}