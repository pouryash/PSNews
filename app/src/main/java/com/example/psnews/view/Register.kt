package com.example.psnews.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.psnews.R
import com.example.psnews.di.retrofitModlue
import com.example.psnews.extentions.toast
import com.example.psnews.helper.Commen
import com.example.psnews.helper.Validator
import com.example.psnews.model.User
import com.example.psnews.network.Status
import com.example.psnews.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_register.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.context.startKoin
import retrofit2.Retrofit


class Register : AppCompatActivity() {

    lateinit var viewModel: UserViewModel
    var isBackPressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        setupView()


    }

    private fun setupView() {

        btn_link_to_login.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        })
        viewModel.registerLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Commen.startLoading(loading, lin_loading_dim,this)
                }
                Status.SUCCESS -> {
                    toast(msg = it.data!!.message)
                    lin_loading_dim.visibility = View.INVISIBLE
                }
                Status.ERROR -> {
                    toast(msg = it.error!!)
                    lin_loading_dim.visibility = View.INVISIBLE

                }
            }
        })

        btn_register.setOnClickListener(View.OnClickListener {
            val user: User =
                User(name.text.toString(), email.text.toString(), "", password.text.toString())
            if (user.password.isNotEmpty() && user.password.isNotEmpty() && Validator.emailValidator(
                    user.email,
                    this@Register
                )
            ) {
                viewModel.registerUser(user)
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
            toast(msg = "please presse back button again to exist!!", lengh = Toast.LENGTH_SHORT)
        }
        Handler().postDelayed(Runnable {
            if (isBackPressed)
                isBackPressed = false
        }, 3000)
    }
}

