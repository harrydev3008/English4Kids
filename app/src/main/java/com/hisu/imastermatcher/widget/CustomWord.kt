package com.hisu.imastermatcher.widget

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.hisu.imastermatcher.R
import com.nex3z.flowlayout.FlowLayout

class CustomWord(context: Context): AppCompatTextView(context) {
    private val TAG: String = "MyCustomWord"

    constructor(context: Context, word: String): this(context) {
        text = word

        val myLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        //using scalable size unit to programmatically set the padding and margins in dp
        myLayoutParams.setMargins(
            0,
            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp),
            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp),
            0
        )

        setPadding(
            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._12sdp), //left
            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._6sdp), //top
            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._12sdp), //right
            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._6sdp) //bottom
        )

        setTextColor(ContextCompat.getColor(context, R.color.gray))
        layoutParams = myLayoutParams
        textAlignment = TEXT_ALIGNMENT_CENTER

        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(com.intuit.ssp.R.dimen._17ssp))

        background = ContextCompat.getDrawable(context, R.drawable.bg_word_border)
    }

    fun goToGroupView(myCustomLayout: MyCustomLayout, sentenceLayout: FlowLayout) {
        val parent: ViewParent = getParent()

        if(parent is MyCustomLayout) {
            myCustomLayout.removeViewCustomLayout(this)
            sentenceLayout.addView(this)
        } else {
            sentenceLayout.removeView(this)
            myCustomLayout.addViewAt(this)
        }
    }
}