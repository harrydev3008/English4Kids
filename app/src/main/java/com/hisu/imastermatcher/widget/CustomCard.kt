package com.hisu.imastermatcher.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.model.card.Card
import com.makeramen.roundedimageview.RoundedImageView

class CustomCard(context: Context) : RoundedImageView(context) {

    private val TAG: String = "MyCustomCard";

    constructor(
        context: Context,
        card: Card,
        itemTapListener: (card: Card, img: View) -> Unit
    ) : this(context) {

        val myLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        myLayoutParams.setMargins(
            0,
            0,
            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._4sdp),
            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._4sdp)
        )

        visibility = if (card.visible) View.INVISIBLE else View.VISIBLE

        myLayoutParams.width =
            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._70sdp)
        myLayoutParams.height =
            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._70sdp)

        cornerRadius = context.resources.getDimension(com.intuit.sdp.R.dimen._6sdp)
        scaleType = ScaleType.CENTER_CROP
        setImageResource(card.imagePath)

        layoutParams = myLayoutParams
        textAlignment = TEXT_ALIGNMENT_CENTER

        setOnClickListener {
            itemTapListener.invoke(card, this)
        }
    }

    fun flipCard() {
        val oa1 = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0f)
        val oa2 = ObjectAnimator.ofFloat(this, "scaleX", 0f, 1f)

        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = DecelerateInterpolator()

        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)

                setImageResource(R.drawable.placeholder)

                oa2.start()
            }
        })

        oa1.start()
        oa1.duration = 200
        oa2.duration = 200
    }
}