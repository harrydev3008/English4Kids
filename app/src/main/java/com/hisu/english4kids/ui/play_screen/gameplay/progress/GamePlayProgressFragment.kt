package com.hisu.english4kids.ui.play_screen.gameplay.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hisu.english4kids.R
import com.hisu.english4kids.data.BUNDLE_LESSON_DATA
import com.hisu.english4kids.data.model.game_play.Gameplay
import com.hisu.english4kids.databinding.FragmentGamePlayProgressBinding

class GamePlayProgressFragment : Fragment() {

    private var _binding: FragmentGamePlayProgressBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGamePlayProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        back()
    }

    private fun setUpRecyclerView() = binding.rvGameplays.apply {
        val gridLayoutManager = GridLayoutManager(context, 2)
        gridLayoutManager.spanSizeLookup = spanSizeLookUp

        layoutManager = gridLayoutManager

        val gameProgressAdapter = GameplayProgressAdapter(requireContext(), ::handleGameplayRoundClick)

        val itemType = object : TypeToken<List<Object>>() {}.type
        val temp = Gson().fromJson<List<Object>>(arguments?.getString(BUNDLE_LESSON_DATA), itemType)

        val mGamePlays = mutableListOf<Gameplay>()

        for(i in 0 until temp.size) {
            mGamePlays.add( Gameplay(1))
        }

        gameProgressAdapter.gameplays = mGamePlays

        adapter = gameProgressAdapter
    }

    private fun back() = binding.btnHome.setOnClickListener {
        findNavController().popBackStack()
    }

    private fun handleGameplayRoundClick() {
        findNavController().navigate(R.id.action_gamePlayProgressFragment_to_playFragment, arguments)
    }

    private val spanSizeLookUp = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            if (position == 0 || position % 5 == 0) return 2
            return 1
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}