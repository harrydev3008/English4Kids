package com.hisu.english4kids.utils

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.hisu.english4kids.R

class MyUtils {
    companion object {
        fun loadJsonFromAssets(activity: Activity, fileName: String): String =
            activity.assets.open(fileName).bufferedReader()
                .use { it.readText() }

        fun spannableText(text: String, color: String): SpannableString {
            val textSpan = SpannableString(text)

            textSpan.setSpan(
                ForegroundColorSpan(Color.parseColor(color)),
                0,
                textSpan.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            return textSpan
        }

        fun isNetworkAvailable(context: Context?): Boolean {
            if (context == null) return false

            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

                if (capabilities != null) {
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                            return true
                        }
                    }
                }
            } else {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    return true
                }
            }
            return false
        }

        fun getLetterFromName(name: String): String {
//            var letters = name.split(" ")
//            if (letters.size == 1)
            return name.split(" ")[0][0].uppercase()
//            return "${letters[0][0]}${letters[1][0]}".uppercase()
        }

        fun createImageFromText(context: Context, text: String): Bitmap {

            val width = 150
            val height = 150

            val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)

            val paintCircle = Paint()
            val paintText = Paint()

            val rect = Rect(0, 0, width, height)
            val rectF = RectF(rect)
            val density = context.resources.displayMetrics.density
            val roundPx = 100 * density;

            paintCircle.color = ContextCompat.getColor(context, R.color.daily)
            paintCircle.isAntiAlias = true

//            canvas.drawARGB(1, 108, 117, 125)

            canvas.drawRoundRect(rectF, roundPx, roundPx, paintCircle)

            paintText.textAlign = Paint.Align.CENTER
            paintText.color = context.getColor(R.color.white)
            paintText.textSize = 64f

            val xPos = (canvas.width / 2).toFloat()
            val yPos = ((canvas.height / 2) - ((paintText.descent() + paintText.ascent()) / 2))

            canvas.drawText(getLetterFromName(text), xPos, yPos, paintText);

            return output
        }
    }
}