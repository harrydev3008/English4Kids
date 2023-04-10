package com.hisu.imastermatcher.utils

import android.app.Activity

class MyUtils {
    companion object {
        fun loadJsonFromAssets(activity: Activity, fileName: String): String =
            activity.assets.open(fileName).bufferedReader()
                .use { it.readText() }
    }
}