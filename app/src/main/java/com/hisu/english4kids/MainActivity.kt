package com.hisu.english4kids

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hisu.english4kids.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
//
//    override fun onNavigateUp(): Boolean {
//        return super.onNavigateUp()
//    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}