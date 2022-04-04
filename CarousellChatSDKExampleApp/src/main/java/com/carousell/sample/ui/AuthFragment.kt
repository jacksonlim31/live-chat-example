package com.carousell.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.carousell.chat.remotes.common.ResultState
import com.carousell.sample.R
import com.carousell.sample.configs.Constant
import com.carousell.sample.databinding.FragmentAuthBinding
import com.carousell.sample.model.Resource
import com.carousell.sample.model.User
import com.carousell.sample.utils.ChatUtil
import com.carousell.sample.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {
    private var authBinding: FragmentAuthBinding? = null
    private val authViewModel: AuthViewModel by viewModels()
    private val binding get() = authBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authBinding = FragmentAuthBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authBinding?.let { oauthBinding ->
            ChatUtil.retrieveClassInSharedPreferences(
                view.context,
                Constant.USER_DATA,
                User::class.java,
                ""
            ).let { user ->
                if (user.accessToken.isNotBlank()) {
                    authViewModel.isAuthenticated(user.profile.accountId.toString()) { isAuthenticated ->
                        oauthBinding.progressCircular.visibility = View.GONE
                        if (isAuthenticated) {
                            val action =
                                AuthFragmentDirections.redirectToChannel(user.profile.accountId.toString())
                            view.findNavController().navigate(action)
                        }
                    }
                }
            }

            oauthBinding.btnSubmit.setOnClickListener {
                val edtPhone = oauthBinding.edtPhone.text.toString()
                val edtPassword = oauthBinding.edtPassword.text.toString()
                authViewModel.login(it.context, edtPhone, edtPassword)
            }

            authViewModel.authLiveData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Loading -> oauthBinding.progressCircular.visibility = View.VISIBLE
                    is Resource.Loaded -> {
                        oauthBinding.progressCircular.visibility = View.GONE
                        if (response.result != null) {
                            if (response.result is ResultState.Success<*>) {
                                ChatUtil.retrieveClassInSharedPreferences(
                                    view.context,
                                    Constant.USER_DATA,
                                    User::class.java,
                                    ""
                                ).let { user ->
                                    val action =
                                        AuthFragmentDirections.redirectToChannel(user.profile.accountId.toString())
                                    view.findNavController().navigate(action)
                                }
                            } else {
                                val snackbar = Snackbar.make(
                                    oauthBinding.root,
                                    "Not able to activate the chat!",
                                    Snackbar.LENGTH_LONG
                                )
                                snackbar.show()
                            }
                        }
                    }
                    else -> {
                        oauthBinding.progressCircular.visibility = View.GONE
                        val snackbar = Snackbar.make(
                            oauthBinding.root,
                            "Not able to activate the chat!",
                            Snackbar.LENGTH_LONG
                        )
                        snackbar.show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        authBinding = null
        authViewModel.dispose()
    }
}