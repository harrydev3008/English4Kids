package com.hisu.english4kids.ui.play_screen.gameplay.class_pairs_matching

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.card.Card
import com.hisu.english4kids.data.model.game_play.GameStyleOne
import com.hisu.english4kids.databinding.FragmentClassicPairsMatchingBinding
import com.hisu.english4kids.utils.MyUtils

class ClassicPairsMatchingFragment(
    private val nextQuestionListener: () -> Unit,
    private val wrongAnswerListener: (roundId: String, position: Int, playStatus: String) -> Unit,
    private val correctAnswerListener: (score: Int, roundId: String, playStatus: String) -> Unit,
    private var gameStyleOne: GameStyleOne,
    private var gameIndex: Int
) : Fragment() {

    private var _binding: FragmentClassicPairsMatchingBinding? = null
    private val binding get() = _binding!!
    private val _result = MutableLiveData<Boolean>()
    private val result: LiveData<Boolean> = _result

    private var prev: Card? = null
    private lateinit var prevCardBack: TextView
    private lateinit var prevCardFront: LinearLayout
    private lateinit var prevCardParent: RelativeLayout

    private var counter = 0
    private var wrongMove = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassicPairsMatchingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvModeLevel.append(MyUtils.spannableText(" ${gameStyleOne.allowedMoves} ", "#ca5866"))
        binding.tvModeLevel.append("lượt")

        observeResult()
        addActionForBtnCheck()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() = binding.rvMatchingPairs.apply {
        val cardAdapter = CardAdapter(requireContext(), ::cardItemClick)

        if(gameStyleOne.cards.size > 4) {
            val tempCards = mutableListOf<Card>()
            tempCards.addAll(gameStyleOne.cards)
            tempCards.add(4, Card("-1", "-1", "", false))
            cardAdapter.cards = tempCards
        } else {
            cardAdapter.cards = gameStyleOne.cards
        }

        val gridLayoutMgr = GridLayoutManager(
            requireContext(),
            if (gameStyleOne.cards.size > 4) 3 else 2
        )

        binding.rvMatchingPairs.layoutManager = gridLayoutMgr
        binding.rvMatchingPairs.adapter = cardAdapter

        //Give user around 2-5s to memorize
        Handler(requireContext().mainLooper).postDelayed({
            cardAdapter.hideCards()
        }, 4000)
    }

    private fun observeResult() {
        result.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.btnCheck.btnNextRound.isEnabled = true
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.GONE
                binding.btnCheck.tvCorrect.visibility = View.VISIBLE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.correct))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_correct))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))

                correctAnswerListener.invoke(gameStyleOne.score, gameStyleOne.roundId, gameStyleOne.playStatus?:"NONE")
            } else {
                binding.btnCheck.btnNextRound.isEnabled = true
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.VISIBLE
                binding.btnCheck.tvCorrect.visibility = View.GONE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.incorrect))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_incorrect))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))

                binding.btnCheck.tvCorrectAnswerDesc.text =
                    String.format(
                        requireContext().getString(R.string.complete_within_turn),
                        gameStyleOne.allowedMoves
                    )
                binding.btnCheck.tvCorrectAnswer.text =
                    requireContext().getString(R.string.better_luck_next_time)

                wrongAnswerListener.invoke(gameStyleOne.roundId, gameIndex, gameStyleOne.playStatus?:"NONE")
            }
        }
    }

    private fun cardItemClick(card: Card, frontCard: LinearLayout, backCard: TextView, parent: RelativeLayout) {
        if (prev != null) {

            if (prev?.cardId == card.cardId) return

            flip(parent, frontCard, backCard, false)

            if (card.pairId == prev?.pairId) {
                Handler(requireContext().mainLooper).postDelayed({
                    parent.isClickable = false
                    prevCardParent.isClickable = false

                    frontCard.visibility = View.INVISIBLE
                    backCard.visibility = View.INVISIBLE
                    prevCardBack.visibility = View.INVISIBLE
                    prevCardFront.visibility = View.INVISIBLE

                    prev = null
                    counter++

                    //When all the pairs are matched
                    if (counter == gameStyleOne.totalPairs) {
                        _result.postValue(true)
                    }
                }, 500)
            } else {
                prev = null

                Handler(requireContext().mainLooper).postDelayed({
                    flip(parent, frontCard, backCard, true)
                    flip(prevCardParent, prevCardFront, prevCardBack, true)

                    wrongMove++

                    if (wrongMove == gameStyleOne.allowedMoves) {
                        _result.postValue(false)
                    }
                }, 400)
            }
        } else {
            prev = card
            prevCardFront = frontCard
            prevCardBack = backCard
            prevCardParent = parent
            flip(parent, frontCard, backCard, false)
        }
    }

    private fun flip(parent: View, front: LinearLayout, back: TextView, isHidden: Boolean) {
        val oa1 = ObjectAnimator.ofFloat(parent, "scaleX", 1f, 0f)
        val oa2 = ObjectAnimator.ofFloat(parent, "scaleX", 0f, 1f)

        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = DecelerateInterpolator()

        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)

                if (isHidden) {
                    front.visibility = View.INVISIBLE
                    back.visibility = View.VISIBLE
                } else {
                    front.visibility = View.VISIBLE
                    back.visibility = View.INVISIBLE
                }

                oa2.start()
            }
        })

        oa1.start()
        oa1.duration = 200
        oa2.duration = 200
    }

    private fun addActionForBtnCheck() = binding.btnCheck.btnNextRound.setOnClickListener {
        if (binding.btnCheck.btnNextRound.text.equals(requireContext().getString(R.string.next))) {
            counter = 0
            wrongMove = 0
            prev = null
            nextQuestionListener.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}