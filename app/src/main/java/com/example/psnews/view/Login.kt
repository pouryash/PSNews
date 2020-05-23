package com.example.psnews.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.psnews.R
import com.example.psnews.extentions.toast
import com.example.psnews.helper.Common
import com.example.psnews.helper.SharedPrefrenceManager
import com.example.psnews.helper.Validator
import com.example.psnews.model.User
import com.example.psnews.network.Status
import com.example.psnews.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.lin_loading_dim
import kotlinx.android.synthetic.main.activity_register.loading
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class Login : AppCompatActivity() {

    private val userViewModel: UserViewModel by inject(named("b"))
    private val sharedPrefrenceManager: SharedPrefrenceManager by inject()
    var isBackPressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupView()
    }

    private fun setupView() {

        btn_link_to_register.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, Register::class.java))
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

        btn_login.setOnClickListener(View.OnClickListener {
            val user: User =
                User("", et_email_login.text.toString(), "", "", et_password_login.text.toString())
            if (user.password.isNotEmpty() && Validator.emailValidator(
                    user.email,
                    this@Login
                )
            ) {
                userViewModel.loginUser(user)
            } else if (et_password_login.text.toString().trim().isEmpty()) {
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
