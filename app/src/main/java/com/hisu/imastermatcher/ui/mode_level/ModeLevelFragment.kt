package com.hisu.imastermatcher.ui.mode_level

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentModeLevelBinding
import com.hisu.imastermatcher.model.card.Card
import com.hisu.imastermatcher.model.course.CourseLevelsResponse


class ClassModeLevelFragment : Fragment() {

    private var _binding: FragmentModeLevelBinding? = null
    private val binding get() = _binding!!
    private val myNavArgs: ClassModeLevelFragmentArgs by navArgs()
    private var levelAdapter: LevelAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModeLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mode = myNavArgs.mode
        binding.tvMode.text = mode

        continueLevel()
        backToHomePage()
        setUpLevels()
        loadLevel()
    }

    private fun backToHomePage() = binding.btnHome.setOnClickListener {
        activity?.onBackPressed()
    }

    private fun continueLevel() = binding.btnContinue.setOnClickListener {
        findNavController().navigate(R.id.mode_to_play)
    }

    private fun setUpLevels() = binding.rvLevels.apply {
        levelAdapter = LevelAdapter() {
            findNavController().navigate(R.id.mode_to_play)
        }

        adapter = levelAdapter
        setHasFixedSize(true)
    }

    private fun loadLevel() {
        val levels = Gson().fromJson(myNavArgs.courseLevels, CourseLevelsResponse::class.java)

        levelAdapter?.items = levels
    }
}