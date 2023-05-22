package com.hisu.english4kids.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.result.FinalResult
import com.hisu.english4kids.databinding.LayoutGameplayFinishBinding

class GameFinishDialog() {

    private lateinit var context: Context
    private lateinit var dialog: Dialog
    private lateinit var binding: LayoutGameplayFinishBinding
    private lateinit var resultJson: JsonElement
    private lateinit var btnExitCallback: () -> Unit
    private lateinit var btnNextLessonCallback: () -> Unit

    constructor(context: Context, resultJson: JsonElement) : this() {
        this.context = context
        this.resultJson = resultJson
        initDialog()
    }

    fun setExitCallback(btnExitCallback: () -> Unit) {
        this.btnExitCallback = btnExitCallback
    }

    fun setNextLessonCallback(btnNextLessonCallback: () -> Unit) {
        this.btnNextLessonCallback = btnNextLessonCallback
    }

    private fun initDialog() {
        binding = LayoutGameplayFinishBinding.inflate(LayoutInflater.from(context), null, false)

        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)

        val width = (context.resources.displayMetrics.widthPixels * 0.95).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.95).toInt()

        val window = dialog.window ?: return
        window.setLayout(width, height)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttr = window.attributes
        windowAttr.gravity = Gravity.CENTER
        window.attributes = windowAttr

        val result = Gson().fromJson(resultJson, FinalResult::class.java)

        handleExitButton()
        handleNextLessonButton()

        displayUserResult(result)
    }

    private fun displayUserResult(result: FinalResult) = binding.apply {
        tvFastScore.text = result.fastScore
        tvPerfectScore.text = String.format(context.getString(R.string.perfect_score_pattern), result.perfectScore)
        tvGold.text = String.format(context.getString(R.string.bonus_gold_pattern), result.golds)
        tvWeeklyScore.text = String.format(context.getString(R.string.bonus_gold_pattern), result.totalScore)
    }

    private fun handleExitButton() = binding.btnExit.setOnClickListener {
        btnExitCallback.invoke()
    }

    private fun handleNextLessonButton() = binding.btnNextLevel.setOnClickListener {
        btnNextLessonCallback.invoke()
    }

    fun showDialog() = dialog.show()

    fun dismissDialog() = dialog.dismiss()
}