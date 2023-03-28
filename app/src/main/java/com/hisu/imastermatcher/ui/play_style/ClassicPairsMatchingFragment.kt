package com.hisu.imastermatcher.ui.play_style

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentClassicPairsMatchingBinding
import com.hisu.imastermatcher.model.Card
import com.hisu.imastermatcher.widget.CustomCard
import com.makeramen.roundedimageview.RoundedImageView

class ClassicPairsMatchingFragment : Fragment() {

    private var _binding: FragmentClassicPairsMatchingBinding? = null
    private val binding get() = _binding!!

    private var prev: Card?= null
    private lateinit var prevImage: RoundedImageView
//    private val myNavArgs: PlayFragmentArgs by navArgs()

    private var counter = 0;
    private var wrongMove = 0;
    private var score = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClassicPairsMatchingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val temp = listOf(
            Card(1, "1", R.drawable.img_test_1, false),
            Card(2, "3", R.drawable.img_test_3, false),
            Card(3, "2", R.drawable.img_test_2, false),
            Card(4, "4", R.drawable.img_test_4, false),
            Card(5, "-1", -1, true),
            Card(6, "4", R.drawable.img_test_4, false),
            Card(7, "3", R.drawable.img_test_3, false),
            Card(8, "2", R.drawable.img_test_2, false),
            Card(9, "1", R.drawable.img_test_1, false)
        )

        temp.forEach {
            binding.rvMatchingPairs.addView(CustomCard(requireContext(), it, ::cardItemClick))
        }

        //Give user around 1s to memorize
        Handler(requireContext().mainLooper).postDelayed({
            for (i in 0 until binding.rvMatchingPairs.childCount) {
                (binding.rvMatchingPairs.getChildAt(i) as CustomCard).flipCard()
            }
        }, 800)

    }

    private fun cardItemClick(card: Card, front: View) {
        if (prev != null) {

            if (prev?.cardID == card.cardID) return

            flipCard(front as ImageView, card, false)

            if (card.id.lowercase().equals(prev?.id)) {
                Handler(requireContext().mainLooper).postDelayed({
                    front.visibility = View.GONE
                    prevImage.visibility = View.GONE
                    prev = null

                    //When all the pairs are matched
                    if (counter == 4) {
                        //todo: implement later
                    }

                }, 500)
            } else {
                Handler(requireContext().mainLooper).postDelayed({
                    prev = null
                    flipCard(front, card, true)
                    flipCard(prevImage, card, true)

                    wrongMove++
//                    binding.pbStar.progress = binding.pbStar.progress - 1
//                    binding.tvMoves.text = binding.pbStar.progress.toString()
//
//                    if(wrongMove == 4) {
//                        findNavController().navigate(R.id.game_over)
//                    }

                }, 400)
            }
        } else {
            flipCard(front as ImageView, card, false)
            prev = card
            prevImage = front as RoundedImageView
        }
    }

        private fun flipCard(image: ImageView, card: Card, hide: Boolean) {
            val oa1 = ObjectAnimator.ofFloat(image, "scaleX", 1f, 0f)
            val oa2 = ObjectAnimator.ofFloat(image, "scaleX", 0f, 1f)

            oa1.interpolator = DecelerateInterpolator()
            oa2.interpolator = DecelerateInterpolator()

            oa1.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                    super.onAnimationEnd(animation, isReverse)

                    if (hide)
                        image.setImageResource(R.drawable.placeholder)
                    else
                        image.setImageResource(card.imagePath)

                    oa2.start()
                }
            })

            oa1.start()
            oa1.duration = 200
            oa2.duration = 200
        }

    fun getScreenWidth(): Int {
        return resources.displayMetrics.widthPixels
    }

    fun getScreenHeight(): Int {
        return resources.displayMetrics.heightPixels
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}