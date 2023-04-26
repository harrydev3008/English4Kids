package com.hisu.english4kids.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.FragmentDisplayNameBinding
import com.hisu.english4kids.model.leader_board.User
import com.hisu.english4kids.utils.local.LocalDataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DisplayNameFragment : Fragment() {

    private var _binding: FragmentDisplayNameBinding?= null
    private val binding get() = _binding!!

    private val myArgs: DisplayNameFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisplayNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleUserNameTextChange()
        handleSaveButton()
    }

    private fun handleUserNameTextChange() = binding.edtUsername.addTextChangedListener {
        it?.apply {
            binding.btnSave.isEnabled = it.isNotEmpty()
        }
    }

    private fun handleSaveButton() = binding.btnSave.setOnClickListener {
        val localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                localDataManager.setUserLoinState(true)
                val user = User(1, binding.edtUsername.text.toString(), myArgs.phoneNumber)
                localDataManager.setUserInfo(Gson().toJson(user))
            }

            findNavController().navigate(R.id.action_displayNameFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}