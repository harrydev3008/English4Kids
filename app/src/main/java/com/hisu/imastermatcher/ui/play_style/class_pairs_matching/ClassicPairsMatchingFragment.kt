package com.hisu.imastermatcher.ui.play_style.class_pairs_matching

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentClassicPairsMatchingBinding
import com.hisu.imastermatcher.model.Card
import com.hisu.imastermatcher.model.CardsResponse
import com.hisu.imastermatcher.widget.CustomCard
import com.makeramen.roundedimageview.RoundedImageView


class ClassicPairsMatchingFragment(
    private val itemTapListener: () -> Unit,
    private val wrongAnswerListener: () -> Unit
) : Fragment() {

    private var _binding: FragmentClassicPairsMatchingBinding? = null
    private val binding get() = _binding!!
    private val _result = MutableLiveData<Boolean>()
    private val result: LiveData<Boolean> = _result

    private var prev: Card? = null
    private lateinit var prevImage: RoundedImageView
    private lateinit var cardsResponse: CardsResponse
//    private val myNavArgs: PlayFragmentArgs by navArgs()

    private var counter = 0;
    private var wrongMove = 0;

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

        val cards = listOf(
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

        cardsResponse = CardsResponse(cards, 4, 6)

        val redText = SpannableString(" ${cardsResponse.allowedWrongMoveAmount} ")
        redText.setSpan(ForegroundColorSpan(resources.getColor(R.color.light_red)), 0, redText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvModeLevel.append(redText)
        binding.tvModeLevel.append("lượt")

        cardsResponse.cards.forEach {
            binding.rvMatchingPairs.addView(CustomCard(requireContext(), it, ::cardItemClick))
        }

        //Give user around 1s to memorize
        Handler(requireContext().mainLooper).postDelayed({
            for (i in 0 until binding.rvMatchingPairs.childCount) {
                (binding.rvMatchingPairs.getChildAt(i) as CustomCard).flipCard()
            }
        }, 800)

        result.observe(viewLifecycleOwner) {
            if (it == true) {
                lockViews()
                binding.btnCheck.btnNextRound.isEnabled = true
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.GONE
                binding.btnCheck.tvCorrect.visibility = View.VISIBLE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.correct))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_correct))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
            } else {
                lockViews()
                binding.btnCheck.btnNextRound.isEnabled = true
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.VISIBLE
                binding.btnCheck.tvCorrect.visibility = View.GONE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.incorrect))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_incorrect))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))

                binding.btnCheck.tvCorrectAnswerDesc.text =
                    "Không thể hoàn thành trong ${cardsResponse.allowedWrongMoveAmount} lượt..."
                binding.btnCheck.tvCorrectAnswer.text = "Cố gắng hơn ở lần sau bạn nhé!"

                wrongAnswerListener.invoke()
            }
        }

        addActionForBtnCheck()
    }

    private fun lockViews() {
        for(i in 0 until binding.rvMatchingPairs.childCount) {
            (binding.rvMatchingPairs.getChildAt(i) as CustomCard).isClickable = false
        }
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

                    counter++

                    //When all the pairs are matched
                    if (counter == cardsResponse.totalPairs) {
                        _result.postValue(true)
                    }
                }, 500)
            } else {
                prev = null

                Handler(requireContext().mainLooper).postDelayed({
                    flipCard(front, card, true)
                    flipCard(prevImage, card, true)

                    wrongMove++

                    if (wrongMove == cardsResponse.allowedWrongMoveAmount) {
                        _result.postValue(false)
                    }
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

    private fun addActionForBtnCheck() = binding.btnCheck.btnNextRound.setOnClickListener {
        if (binding.btnCheck.btnNextRound.text.equals(requireContext().getString(R.string.next))) {
            itemTapListener.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}