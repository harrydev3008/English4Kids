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
import com.hisu.english4kids.databinding.LayoutStartRoundDialogBinding

class StartRoundDialog() {
    private lateinit var context: Context
    private lateinit var dialog: Dialog
    private lateinit var binding: LayoutStartRoundDialogBinding
    private lateinit var title: String
    private var status:Int = 0
    private lateinit var btnStart: () -> Unit

    constructor(context: Context, title: String, status: Int) : this() {
        this.context = context
        this.title = title
        this.status = status
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
        val size = (context.resources.displayMetrics.widthPixels * 0.95).toInt()
        window.setLayout(size, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttr = window.attributes
        windowAttr.gravity = Gravity.CENTER
        window.attributes = windowAttr

        binding.apply {
            tvRound.text = title
        }

        if(status== 1) {
            binding.cardParent.setCardBackgroundColor(ContextCompat.getColor(context, R.color.daily))
            binding.tvPlayed.visibility = View.VISIBLE
            binding.tvNotPlayed.visibility = View.GONE
            binding.btnStart.setTextColor(ContextCompat.getColor(context, R.color.daily))
            binding.btnStart.text = context.getString(R.string.start_played_round_to_earn_pattern)
        } else {
            binding.cardParent.setCardBackgroundColor(ContextCompat.getColor(context, R.color.classic))
            binding.tvPlayed.visibility = View.GONE
            binding.tvNotPlayed.visibility = View.VISIBLE
            binding.btnStart.setTextColor(ContextCompat.getColor(context, R.color.classic))
            binding.btnStart.text = context.getString(R.string.start_round_to_earn_pattern)
        }

        binding.btnClose.setOnClickListener {
            dismissDialog()
        }

        binding.btnStart.setOnClickListener {
            btnStart.invoke()
        }
    }

    fun showDialog() = dialog.show()

    fun dismissDialog() = dialog.dismiss()
}