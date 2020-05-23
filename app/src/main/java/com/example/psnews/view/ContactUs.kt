package com.example.psnews.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.example.psnews.R
import com.example.psnews.extentions.toast
import com.example.psnews.helper.Common
import com.example.psnews.helper.SharedPrefrenceManager
import kotlinx.android.synthetic.main.activity_contact_us.*
import kotlinx.android.synthetic.main.activity_contact_us.lin_loading_dim
import kotlinx.android.synthetic.main.activity_contact_us.loading
import kotlinx.android.synthetic.main.activity_profile.iv_back
import org.koin.android.ext.android.inject

class ContactUs : AppCompatActivity() {

    val sharedPreferences: SharedPrefrenceManager by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        initView()
        setupView()
    }

    private fun initView() {
        et_editemail_contact_us.setText(sharedPreferences.getUser().email)
    }

    private fun setupView() {
        iv_back.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        btn_submit_contact_us.setOnClickListener {
            if (Common.isNetworkConnected(this)) {
                if (et_editName_contact_us.text.isNotEmpty() && et_message_contact_us.text.isNotEmpty()) {
                    Common.startLoading(loading, lin_loading_dim, this)
                    Handler().postDelayed(Runnable {
                        toast("your message is successfuly submit")
                        lin_loading_dim.visibility = View.INVISIBLE
                    }, 2000)

                } else {
                    toast(getString(R.string.enter_all_filed), Toast.LENGTH_SHORT)
                }
            }else{
                toast("check your connection, your not connect to network!")
            }
        }
    }
}

