package com.hisu.imastermatcher.ui.play_screen

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentPlayBinding
import com.hisu.imastermatcher.model.Card
import com.hisu.imastermatcher.ui.mode_level.ClassModeLevelFragmentDirections
import com.makeramen.roundedimageview.RoundedImageView

class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private lateinit var cardAdapter: CardAdapter
    private var prev: Card?= null
    private lateinit var prevImage: RoundedImageView
    private val myNavArgs: PlayFragmentArgs by navArgs()

    private var counter = 0;
    private var wrongMove = 0;
    private var score = 0;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        val lvl = myNavArgs.level
        val mode = myNavArgs.mode
        var levelPlaceHolder = ""

        if (mode == 0)
            levelPlaceHolder = "${getString(R.string.mode_classic)} ${lvl}"
        else if (mode == 1)
            levelPlaceHolder = "${getString(R.string.mode_timer)} ${lvl}"

        binding.tvModeLevel.text = levelPlaceHolder

        cardAdapter.items = listOf(
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

        //Give user around 1s to memorize
        Handler(requireContext().mainLooper).postDelayed({
            cardAdapter.hideCards()
        }, 800)

        pauseGame()
        giveHint()

        binding.pbStar.max = 4
    }

    private fun initRecyclerView() = binding.rvCards.apply {
        cardAdapter = CardAdapter(itemTapListener = ::cardItemClick)

        adapter = cardAdapter

        setHasFixedSize(true)
    }

    //todo: demo
    private fun cardItemClick(card: Card, front: View) {
        if (prev != null) {

            if(prev?.cardID == card.cardID) return

            flipCard(front as ImageView, card, false)

            if (card.id.lowercase().equals(prev?.id)) {
                Handler(requireContext().mainLooper).postDelayed({
                    front.visibility = View.GONE
                    prevImage.visibility = View.GONE
                    prev = null

                    //todo: calculate player's scores
                    counter++
                    score = (score * counter) + 1
                    binding.tvScore.text = "Score: $score"

                    if(counter == 4) {
                        val action = PlayFragmentDirections.gameFinish(moves = counter)
                        findNavController().navigate(action)
                    }

                }, 500)
            } else {
                Handler(requireContext().mainLooper).postDelayed({
                    prev = null
                    flipCard(front, card, true)
                    flipCard(prevImage, card, true)

                    wrongMove++
                    binding.pbStar.progress = binding.pbStar.progress - 1
                    binding.tvMoves.text = binding.pbStar.progress.toString()

                    if(wrongMove == 4) {
                        findNavController().navigate(R.id.game_over)
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

    private fun pauseGame() = binding.btnPause.setOnClickListener {
        findNavController().navigate(R.id.game_over)
    }

    private fun giveHint() = binding.btnHint.setOnClickListener {
        //todo: give hint feature
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}