package com.hisu.imastermatcher.widget

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.hisu.imastermatcher.R
import com.nex3z.flowlayout.FlowLayout

class MyCustomLayout(context: Context): FlowLayout(context) {
    lateinit var customWord: CustomWord
    var words = mutableListOf<View>()

    fun push(wordView: View) {
        words.add(wordView)
        addView(wordView)
    }

    fun removeViewCustomLayout(view: View) {
        //view when word is selected
        customWord = CustomWord(context, "")
        customWord.background = ContextCompat.getDrawable(context, R.drawable.bg_word_border_selected)

        val params =  LinearLayout.LayoutParams(view.width, view.height)
        params.setMargins(
            0,
            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp),
            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp),
            0
        )

        customWord.layoutParams= params

        removeView(view)
        addView(customWord, words.indexOf(view))
    }

    fun addViewAt(view: View) {
        if(getChildAt(words.indexOf(view)) != null) {
            removeViewAt(words.indexOf(view))
            addView(view, words.indexOf(view))
        } else {
            addView(view, childCount)
        }
    }

    fun empty(): Boolean = words.isEmpty()
}