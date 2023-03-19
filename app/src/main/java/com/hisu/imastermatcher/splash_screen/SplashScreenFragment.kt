package com.hisu.imastermatcher.splash_screen

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentSplashScreenBinding

class SplashScreenFragment : Fragment() {

    private var _binding: FragmentSplashScreenBinding?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(requireContext().mainLooper).postDelayed({
            findNavController().navigate(R.id.splash_to_course)
        }, 3000)
    }
}