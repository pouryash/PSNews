package com.example.psnews.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.example.psnews.Adapter.NewsAdapter
import com.example.psnews.databinding.ActivityMainBinding
import com.example.psnews.extentions.toast
import com.example.psnews.helper.Commen
import com.example.psnews.helper.Constants
import com.example.psnews.helper.SharedPrefrenceManager
import com.example.psnews.network.Status
import com.example.psnews.viewmodel.MainNewsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.news_row.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    val sharedPreferences: SharedPrefrenceManager by inject()
    val viewmodel: MainNewsViewModel by inject(parameters = { parametersOf(this)})

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

    fun checkAvatar(){
        if (viewmodel.profileAvatar != sharedPreferences.getUser().userAvatar){
            viewmodel.profileAvatar = sharedPreferences.getUser().userAvatar
        }
    }

    fun setupView() {
        viewmodel.profileAvatar = sharedPreferences.getUser().userAvatar
        binding.newsViewmodel = viewmodel

        civ_profile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, Profile::class.java))
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
    }

    fun initView() {
        viewmodel.getNews()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.NEWS_REQUEST_ID) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra("result")
                val pos = data.getIntExtra("pos", 0)
                val newsAdapter = rcl_main_news.adapter as NewsAdapter
                newsAdapter.updateNewsLikeCount(result!!, pos)
            }
        }
    }
}
