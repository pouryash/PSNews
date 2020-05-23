package com.example.psnews.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.psnews.R
import com.example.psnews.extentions.toast
import com.example.psnews.helper.Common
import com.example.psnews.helper.SharedPrefrenceManager
import com.example.psnews.helper.Validator
import com.example.psnews.model.User
import com.example.psnews.network.Status
import com.example.psnews.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_register.*
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named


class Register : AppCompatActivity() {

    private val sharedPrefrenceManager: SharedPrefrenceManager by inject()
    private val userViewModel: UserViewModel by inject(named("b"))
    var isBackPressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        checkUserSession()

        setupView()
    }

    private fun checkUserSession() {

        if (!sharedPrefrenceManager.getUser().email.equals("")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupView() {

        btn_link_to_login.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        })
        userViewModel.userLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Common.startLoading(loading, lin_loading_dim, this)
                }
                Status.SUCCESS -> {
                    sharedPrefrenceManager.saveUser(it.data!!.data)
                    toast(msg = it.data!!.message)
                    lin_loading_dim.visibility = View.INVISIBLE
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                Status.ERROR -> {
                    toast(msg = it.error!!)
                    lin_loading_dim.visibility = View.INVISIBLE

                }
            }
        })

        btn_register.setOnClickListener(View.OnClickListener {
            val user: User =
                User(name.text.toString(), email.text.toString(), "", "", password.text.toString())
            if (user.password.isNotEmpty() && user.name.isNotEmpty() && Validator.emailValidator(
                    user.email,
                    this@Register
                )
            ) {
                userViewModel.registerUser(user)
            } else if (name.text.toString().trim().isEmpty() || password.text.toString().trim()
                    .isEmpty()
            ) {
                toast(getString(R.string.enter_all_filed), Toast.LENGTH_SHORT)
            }
        })

    }

    override fun onBackPressed() {
        if (isBackPressed) {
            isBackPressed = false
            finish()


        } else {
            isBackPressed = true
            toast(msg = "please presse back button again to exist!!", length = Toast.LENGTH_SHORT)
        }
        Handler().postDelayed(Runnable {
            if (isBackPressed)
                isBackPressed = false
        }, 3000)
    }
}

