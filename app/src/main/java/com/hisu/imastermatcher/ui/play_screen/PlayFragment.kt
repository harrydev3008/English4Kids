package com.hisu.imastermatcher.ui.play_screen

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentPlayBinding
import com.hisu.imastermatcher.model.Card
import com.makeramen.roundedimageview.RoundedImageView

class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private lateinit var cardAdapter: CardAdapter
    private var prev: String = ""
    private lateinit var prevImage: RoundedImageView

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
        cardAdapter.items = listOf(
            Card("1", false), Card("3", false), Card("2", false),
            Card("4", false), Card("-1", false), Card("4", false),
            Card("3", false), Card("2", false), Card("1", false)
        )
    }

    private fun initRecyclerView() = binding.rvCards.apply {
        cardAdapter = CardAdapter(itemTapListener = ::cardItemClick)

        adapter = cardAdapter

        setHasFixedSize(true)
    }

    //todo: demo
    private fun cardItemClick(card: Card, img: View) {

        /**
         * Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(500);
         */

        val fadein = AlphaAnimation(0f, 1f)
        fadein.setInterpolator(DecelerateInterpolator());
        fadein.setDuration(500);

        (img as RoundedImageView).setImageResource(R.mipmap.ic_launcher)
        (img as RoundedImageView).animation = fadein

        if (prev.isNotEmpty()) {
            if (card.id.lowercase().equals(prev)) {
                Handler(requireContext().mainLooper).postDelayed({
                    img.visibility = View.GONE
                    prevImage.visibility = View.GONE
                    prev = ""
                }, 500)
            } else {
                Handler(requireContext().mainLooper).postDelayed({
                    prev = ""
                    (img as RoundedImageView).setImageResource(R.drawable.placeholder)
                    prevImage.setImageResource(R.drawable.placeholder)
                }, 400)
            }
        } else {
            prev = card.id
            prevImage = img as RoundedImageView
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}