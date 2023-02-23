package com.hisu.imastermatcher.ui.game_over

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentGameOverBinding

class GameOverFragment : Fragment() {

    private var _mBinding: FragmentGameOverBinding?= null
    private val mBinding get() = _mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _mBinding = FragmentGameOverBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retry()
        quit()
    }

    private fun retry() = mBinding.btnRetry.setOnClickListener {
        findNavController().navigate(R.id.play_again)
    }

    private fun quit() = mBinding.btnQuit.setOnClickListener {
        findNavController().navigate(R.id.quit_game)
    }
}