package com.example.psnews

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.psnews.databinding.ActivityProfileBinding
import com.example.psnews.extentions.toast
import com.example.psnews.helper.Commen
import com.example.psnews.helper.SharedPrefrenceManager
import com.example.psnews.helper.Validator
import com.example.psnews.model.User
import com.example.psnews.network.Status
import com.example.psnews.view.Login
import com.example.psnews.view.MainActivity
import com.example.psnews.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.lin_loading_dim
import kotlinx.android.synthetic.main.activity_profile.loading
import kotlinx.android.synthetic.main.activity_register.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named


class Profile : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    val sharedPreferences: SharedPrefrenceManager = get()
    private val userViewModel: UserViewModel by inject(qualifier = named("a"), parameters = { parametersOf(sharedPreferences.getUser())})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setupView()

    }

    private fun initView() {
        binding.userViewModel = userViewModel
    }

    private fun setupView() {

        btn_save_profile.setOnClickListener(View.OnClickListener {
            val user: User = User(et_editName_profile.text.toString(), et_editemail_profile.text.toString(), "", sharedPreferences.getUser().id.toString(), "")
            if (Validator.emailValidator(user.email, this@Profile)) {
                userViewModel.updateUser(user)
            }else if (et_editName_profile.text.toString().trim().isEmpty()){
                toast(getString(R.string.enter_all_filed), Toast.LENGTH_SHORT)
            }
        })

        userViewModel.userLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Commen.startLoading(loading, lin_loading_dim, this)
                }
                Status.SUCCESS -> {
                    sharedPreferences.saveUser(it.data!!.data)
                    userViewModel.name = it.data!!.data.name
                    userViewModel.email = it.data!!.data.email
                    toast(msg = it.data!!.message)
                    lin_loading_dim.visibility = View.INVISIBLE
                }
                Status.ERROR -> {
                    toast(msg = it.error!!)
                    lin_loading_dim.visibility = View.INVISIBLE
                }
            }
        })

        btn_logout.setOnClickListener(View.OnClickListener {
            sharedPreferences.clearUserSession()
            var intent:Intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            this.finish()
        })

        iv_back.setOnClickListener(View.OnClickListener {
            this.finish()
        })

    }

    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.authenthication_secoundry)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().decorView.systemUiVisibility = 0
            }
        }
    }

}
