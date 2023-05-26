package com.hisu.english4kids.ui.leader_board

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gdacciaro.iOSDialog.iOSDialog
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.hisu.english4kids.R
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.LeaderBoardResponseModel
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.databinding.FragmentLeaderBoardBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.LoadingDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeaderBoardFragment : Fragment() {

    private var _binding: FragmentLeaderBoardBinding? = null
    private val binding get() = _binding!!
    private lateinit var leaderBoardAdapter: LeaderBoardAdapter
    private lateinit var localDataManager: LocalDataManager
    private lateinit var currentPlayer: Player
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLeaderBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        loadUserInfo()
        setUpLeaderBoardRecyclerView()
        backToPrev()
        getLeaderBoard()
    }

    private fun loadUserInfo() = binding.apply {
        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())
        val json = localDataManager.getUserInfo()
        currentPlayer = Gson().fromJson(json, Player::class.java)
        tvLeaderBoardTitle.text = "Chào mừng "
        tvLeaderBoardTitle.append(MyUtils.spannableText(currentPlayer.username, "#FD4C4A"))
        tvLeaderBoardTitle.append(" đến với bảng xếp hạng tuần này!")
    }

    private fun setUpLeaderBoardRecyclerView() = binding.rvLeaderBoard.apply {
        leaderBoardAdapter = LeaderBoardAdapter(requireContext(), currentPlayer)
        adapter = leaderBoardAdapter
    }

    private fun backToPrev() = binding.btnBack.setOnClickListener {
        findNavController().navigate(R.id.action_leaderBoardFragment_to_homeFragment)
    }

    private fun getLeaderBoard() {
        if (MyUtils.isNetworkAvailable(requireContext())) {
            requireActivity().runOnUiThread {
                mLoadingDialog.showDialog()
            }

            API.apiService.getLeaderBoard("Bearer ${localDataManager.getUserAccessToken()}").enqueue(handleGetLeaderBoard)
        } else {
            requireActivity().runOnUiThread {
                iOSDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.confirm_otp))
                    .setSubtitle(requireContext().getString(R.string.err_network_not_available))
                    .setPositiveListener(
                        requireContext().getString(R.string.confirm_otp),
                        iOSDialog::dismiss
                    ).build().show()
            }
        }
    }

    private val handleGetLeaderBoard = object: Callback<LeaderBoardResponseModel> {
        override fun onResponse(call: Call<LeaderBoardResponseModel>, response: Response<LeaderBoardResponseModel>) {

            if(response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    leaderBoardAdapter.leaderBoardUsers = this.data.players
                    leaderBoardAdapter.notifyDataSetChanged()

                    requireActivity().runOnUiThread {
                        mLoadingDialog.dismissDialog()
                    }
                }
            } else {
                requireActivity().runOnUiThread {
                    mLoadingDialog.dismissDialog()
                    iOSDialogBuilder(requireContext()).setTitle(requireContext().getString(R.string.request_err))
                        .setSubtitle(requireContext().getString(R.string.err_getting_leader_board))
                        .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                            it.dismiss()
                        }.build().show()
                }
            }
        }

        override fun onFailure(call: Call<LeaderBoardResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
                iOSDialogBuilder(requireContext()).setTitle(requireContext().getString(R.string.request_err))
                    .setSubtitle(t.message?: requireContext().getString(R.string.confirm_otp_err_occur_msg))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }
            Log.e(LeaderBoardFragment::class.java.name, t.message ?: "error message")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}