package com.hisu.english4kids.utils

import android.app.Activity
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

class MyUtils {
    companion object {
        fun loadJsonFromAssets(activity: Activity, fileName: String): String =
            activity.assets.open(fileName).bufferedReader()
                .use { it.readText() }

        fun SpannableText(text: String, color: String): SpannableString {
            val textSpan = SpannableString(text)

            textSpan.setSpan(
                ForegroundColorSpan(Color.parseColor(color)),
                0,
                textSpan.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            return textSpan
        }
    }
}