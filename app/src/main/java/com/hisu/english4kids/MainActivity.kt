package com.hisu.english4kids

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hisu.english4kids.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!
    private var backPressTime = 0L
    private val PRESS_TIME_INTERVAL = 2 * 1000
    private lateinit var mExitToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onNavigateUp(): Boolean {
        return super.onNavigateUp()
    }

    override fun onBackPressed() {
        /*
          Press back button twice within 2s to exit program
         */
        if (backPressTime + PRESS_TIME_INTERVAL > System.currentTimeMillis()) {
            mExitToast.cancel();
            moveTaskToBack(true); //Only move to back not close the whole app
            return
        } else {
            mExitToast =
                Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT)

            mExitToast.show()
        }

        backPressTime = System.currentTimeMillis()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}