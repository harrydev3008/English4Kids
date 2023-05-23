package com.hisu.english4kids.ui.play_screen.gameplay.match_pairs

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.card.Card
import com.hisu.english4kids.data.model.game_play.GameStyleSix
import com.hisu.english4kids.databinding.FragmentMatchingWordPairsBinding
import es.dmoral.toasty.Toasty
import java.lang.ref.WeakReference
import java.util.Random

class MatchingWordPairsFragment(
    private val itemTapListener: () -> Unit,
    private val wrongAnswerListener: (roundId: String, position: Int, playStatus: String) -> Unit,
    private val correctAnswerListener: (score: Int, roundId: String, playStatus: String) -> Unit,
    private val gameStyleSix: GameStyleSix,
    private var gameIndex: Int
) : Fragment() {

    private var _binding: FragmentMatchingWordPairsBinding? = null
    private val binding get() = _binding!!

    private lateinit var wordPairsAdapter: MatchingWordPairsAdapter
    private val _result = MutableLiveData<Boolean>()
    private val result: LiveData<Boolean> = _result

    private var prev: Card? = null
    private var prevPos: Int = -1
    private var counter = 0
    private var wrongMoves = 0

    private val wrongMsgs = listOf(
        "Úi sai rồi!",
        "Sai rồi! Làm lại nào!",
        "Hic! Dịch sai rồi",
        "Suy nghĩ thật kỹ bạn nhé!",
        "Dù sai vẫn cố! Đừng nản!"
    )

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMatchingWordPairsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvModeLevel.text = requireContext().getString(R.string.tap_connect_pairs)

        wordPairsAdapter = MatchingWordPairsAdapter(WeakReference(requireActivity()), requireContext(), ::handle)

        wordPairsAdapter.pairs = gameStyleSix.cards

        binding.rvMatchingPairs.adapter = wordPairsAdapter

        result.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.btnCheck.btnNextRound.isEnabled = true
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.GONE
                binding.btnCheck.tvCorrect.visibility = View.VISIBLE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.correct))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_correct))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
                correctAnswerListener.invoke(gameStyleSix.score, gameStyleSix.roundId, gameStyleSix.playStatus?:"NONE")
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
                        gameStyleSix.allowedMoves
                    )
                binding.btnCheck.tvCorrectAnswer.text =
                    requireContext().getString(R.string.better_luck_next_time)

                wrongAnswerListener.invoke(gameStyleSix.roundId, gameIndex, gameStyleSix.playStatus?:"NONE")
            }
        }

        checkAnswer()
    }

    private fun handle(item: Card, position: Int) {
        if (prev != null) {

            //ensure self select
            if (prev?.cardId == item.cardId) return

            //in audio - word gameplay
            //if previous item & current item is audio, then reset select color of prev item
            if(prev?.isAudio == true && item.isAudio == true) {
                wordPairsAdapter.resetPairsSelected(prevPos)
                prev = item
                prevPos = position
                return
            }

            if (item.pairId == prev?.pairId) {
                prev = null

                counter++

                wordPairsAdapter.correctPairsSelected(position)
                if (prevPos != -1)
                    wordPairsAdapter.correctPairsSelected(prevPos)

                prevPos = -1

                if (counter == gameStyleSix.totalPairs) {
                    _result.postValue(true)
                }

            } else {
                prev = null

                wrongMoves++

                if(wrongMoves > gameStyleSix.allowedMoves) {
                    _result.postValue(false)
                    return
                }

                Toasty.error(requireContext(), wrongMsgs[Random().nextInt(wrongMsgs.size)], Toasty.LENGTH_SHORT, true).show()

                Handler(requireActivity().mainLooper).postDelayed({
                    wordPairsAdapter.resetPairsSelected(position)
                    wordPairsAdapter.resetPairsSelected(prevPos)
                    prevPos = -1
                }, 500)
            }
        } else {
            prev = item
            prevPos = position
        }
    }

    private fun checkAnswer() = binding.btnCheck.btnNextRound.setOnClickListener {
        itemTapListener.invoke()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}