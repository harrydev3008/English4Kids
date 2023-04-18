package com.hisu.imastermatcher.splash_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentSplashScreenBinding
import kotlinx.coroutines.*

class SplashScreenFragment : Fragment() {

    private var _binding: FragmentSplashScreenBinding?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val isLogIn =
                withContext(Dispatchers.Default) { getLoginStatus() }

            if (isLogIn)
                findNavController().navigate(R.id.splash_to_home)
            else
                findNavController().navigate(R.id.splash_to_regis)
        }
    }

    private suspend fun  getLoginStatus(): Boolean {
        delay(3000L)
        return false
    }
}