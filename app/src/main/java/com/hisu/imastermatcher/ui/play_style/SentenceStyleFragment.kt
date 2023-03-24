package com.hisu.imastermatcher.ui.play_style

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import com.hisu.imastermatcher.databinding.FragmentSentenceStyleBinding

class SentenceStyleFragment : Fragment() {

    private var _binding: FragmentSentenceStyleBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSentenceStyleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        initQuestion("This is a stupid queztion") TODO: complete this is pls
    }

    private fun initQuestion(question: String) {
        var sentence = question.split(" ")

        /*
        *
        *  <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:layout_marginEnd="8dp"
            android:background="@drawable/bg_word_border"
            android:paddingHorizontal="@dimen/_12sdp"
            android:paddingVertical="@dimen/_6sdp"
            android:text="is"
            android:textSize="@dimen/_17ssp" />
        *
        * */

        for(word in sentence) {
            Log.e("test", word)
            var wordView = TextView(requireContext().applicationContext)

            wordView.text = word
            wordView.width = LayoutParams.WRAP_CONTENT
            wordView.height = LayoutParams.WRAP_CONTENT

            var params = LinearLayout.LayoutParams(wordView.width, wordView.height)
            params.setMargins(0, 8, 8,0)
            wordView.layoutParams = params

//            wordView.background =

            binding.flQuestionContainer.addView(wordView, binding.flQuestionContainer.childCount);
        }

    }
}