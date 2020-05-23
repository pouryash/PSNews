package com.example.psnews.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import com.example.psnews.Adapter.NewsAdapter
import com.example.psnews.R
import com.example.psnews.databinding.ActivityMainBinding
import com.example.psnews.extentions.toast
import com.example.psnews.helper.Constants
import com.example.psnews.helper.SharedPrefrenceManager
import com.example.psnews.network.Status
import com.example.psnews.viewmodel.MainNewsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class MainActivity : AppCompatActivity() {

    lateinit var popup: PopupMenu
    lateinit var binding: ActivityMainBinding
    val sharedPreferences: SharedPrefrenceManager by inject()
    val viewmodel: MainNewsViewModel by inject(parameters = { parametersOf(this) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setupView()
    }

    override fun onResume() {
        super.onResume()

        checkAvatar()
    }

    fun checkAvatar() {
        if (viewmodel.profileAvatar != sharedPreferences.getUser().userAvatar) {
            viewmodel.profileAvatar = sharedPreferences.getUser().userAvatar
        }
    }

    @SuppressLint("RestrictedApi")
    fun setupView() {
        viewmodel.profileAvatar = sharedPreferences.getUser().userAvatar
        binding.newsViewmodel = viewmodel

        civ_profile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, Profile::class.java))
            civ_profile.isEnabled = false
            Handler().postDelayed(Runnable { civ_profile.isEnabled = true }, 500)
        })

        iv_main_menu.setOnClickListener(View.OnClickListener {
            popup.show()
            val menuHelper = MenuPopupHelper(this, (popup.menu as MenuBuilder), iv_main_menu)
            menuHelper.setForceShowIcon(true)
            menuHelper.gravity = Gravity.END
            menuHelper.show()
        })

        viewmodel.mutableNewsResponseList.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    progress_main_newsList.visibility = View.GONE
                }
                Status.ERROR -> {
                    toast(msg = it.error!!)
                    progress_main_newsList.visibility = View.GONE
                }
            }
        })

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_contact_us -> {
                    startActivity(Intent(this, ContactUs::class.java))
                    false
                }
                R.id.menu_add_news -> {
                    startActivityForResult(
                        Intent(this, AddNews::class.java),
                        Constants.REQUEST_INSERT_NEWS
                    )
                    false
                }
                else -> {
                    false
                }
            }
        }

    }

    fun initView() {

        popup = PopupMenu(this, iv_main_menu)
        popup.menuInflater.inflate(R.menu.main_menu, popup.getMenu())

        viewmodel.getNews()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.NEWS_REQUEST_ID -> {
                if (resultCode == Activity.RESULT_OK) {
                    val result = data!!.getStringExtra("result")
                    val pos = data.getIntExtra("pos", 0)
                    val newsAdapter = rcl_main_news.adapter as NewsAdapter
                    newsAdapter.updateNewsLikeCount(result!!, pos)
                }
            }
            Constants.REQUEST_INSERT_NEWS -> {
                if (resultCode == Activity.RESULT_OK) {
                    val shouldUpdateList = data!!.getBooleanExtra("shouldUpdate", false)
                    if (shouldUpdateList) {
                        viewmodel.getNews()
                    }
                }
            }
        }
    }
}
