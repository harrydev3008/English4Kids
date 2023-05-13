package com.hisu.english4kids.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.google.gson.Gson
import com.hisu.english4kids.R
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.databinding.LayoutPurchaseHeartBinding
import com.hisu.english4kids.utils.local.LocalDataManager

class PurchaseHeartDialog(){

    private lateinit var context: Context
    private lateinit var dialog: Dialog
    private lateinit var binding: LayoutPurchaseHeartBinding
    private lateinit var btnPurchaseCallback: (amount: Int) -> Unit

    constructor(context: Context) : this() {
        this.context = context
        initDialog()
    }

    fun setPurchaseCallback(btnPurchaseCallback: (amount: Int) -> Unit) {
        this.btnPurchaseCallback = btnPurchaseCallback
    }

    private fun initDialog() {
        binding = LayoutPurchaseHeartBinding.inflate(LayoutInflater.from(context), null, false)

        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)

        val size = (context.resources.displayMetrics.widthPixels * 0.95).toInt()

        val window = dialog.window ?: return
        window.setLayout(size, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttr = window.attributes
        windowAttr.gravity = Gravity.CENTER
        window.attributes = windowAttr

        handleExitButton()
        handleFullRestoreButton()
        handleRestoreThreeButton()

        val localDataManager = LocalDataManager()
        localDataManager.init(context)

        val currentUser = Gson().fromJson(localDataManager.getUserInfo(), Player::class.java)

        binding.tvCurrentCoin.text = String.format(
            context.getString(R.string.you_are_having_pattern),
            currentUser.golds
        )

    }

    private fun handleExitButton() = binding.btnExit.setOnClickListener {
        dismissDialog()
    }

    private fun handleFullRestoreButton() = binding.btnPurchaseFullLife.setOnClickListener {
        btnPurchaseCallback.invoke(5)
    }

    private fun handleRestoreThreeButton() = binding.btnPurchaseThreeLife.setOnClickListener {
        btnPurchaseCallback.invoke(3)
    }

    fun showDialog() = dialog.show()

    fun dismissDialog() = dialog.dismiss()
}