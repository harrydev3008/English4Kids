package com.hisu.english4kids.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.LayoutAppSettingsBinding
import com.hisu.english4kids.model.leader_board.User
import com.hisu.english4kids.utils.local.LocalDataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingDialog() {

    private lateinit var context: Context
    private var gravity: Int = 0
    private lateinit var dialog: Dialog
    private lateinit var binding: LayoutAppSettingsBinding
    private lateinit var btnSaveClickCallBack: (isRemindLearning: Boolean, isRemindDaily: Boolean) -> Unit

    constructor(context: Context, gravity: Int) : this() {
        this.context = context
        this.gravity = gravity
        initDialog()
    }

    fun setSaveCallback(saveEvent: (isRemindLearning: Boolean, isRemindDaily: Boolean) -> Unit): SettingDialog  {
        btnSaveClickCallBack = saveEvent
        return this
    }

    private fun initDialog() {
        val localDataManager = LocalDataManager()
        localDataManager.init(context)

        binding = LayoutAppSettingsBinding.inflate(LayoutInflater.from(context), null, false)

        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)

        val window = dialog.window ?: return
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttr = window.attributes
        windowAttr.gravity = gravity
        window.attributes = windowAttr

        handleExitNotificationSetting()
        handleSaveNotificationSetting()

        binding.btnSwitchLearningRemind.isChecked = localDataManager.getUserRemindLearningState()
        binding.btnSwitchDailyRewardRemind.isChecked = localDataManager.getUserRemindDailyRewardState()
    }

    private fun handleSaveNotificationSetting() = binding.tvSaveSetting.setOnClickListener {
        btnSaveClickCallBack.invoke(binding.btnSwitchLearningRemind.isChecked, binding.btnSwitchDailyRewardRemind.isChecked)
        dismissDialog()
    }

    private fun handleExitNotificationSetting() = binding.tvExit.setOnClickListener {
        dismissDialog()
    }

    fun showDialog() = dialog.show()

    private fun dismissDialog() = dialog.dismiss()
}