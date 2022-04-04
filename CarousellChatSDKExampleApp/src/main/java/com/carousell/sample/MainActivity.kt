package com.carousell.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.carousell.sample.configs.Constant
import com.carousell.sample.databinding.ActivityMainBinding
import com.carousell.sample.model.Resource
import com.carousell.sample.model.User
import com.carousell.sample.utils.ChatUtil
import com.carousell.sample.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        authViewModel.authLiveData.observe(this) { response ->
            when (response) {
                is Resource.Logout -> {
                    ChatUtil.saveClassInSharedPreferences(binding.root.context, Constant.USER_DATA, User())
                    authViewModel.clearChatSDK()
                    val navController = findNavController(R.id.chat_fragment)
                    navController.navigate(R.id.authFragment)
                }
                is Resource.UnexpectedError -> {
                    val snackbar = Snackbar.make(
                        binding.root,
                        "Failed to logout!",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.show()
                }
                else -> {}
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.chat_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                ChatUtil.retrieveClassInSharedPreferences(
                    this,
                    Constant.USER_DATA,
                    User::class.java,
                    ""
                ).let {
                    authViewModel.logout(it.accessToken)
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}