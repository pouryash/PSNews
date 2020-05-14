package com.example.psnews.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.psnews.Profile
import com.example.psnews.R
import com.example.psnews.databinding.ActivityMainBinding
import com.example.psnews.viewmodel.NewsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    val viewmodel: NewsViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       initView()
       setupView()
    }

    fun setupView() {
        binding.newsViewmodel = viewmodel

        civ_profile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, Profile::class.java))
        })
    }

    fun initView() {
        viewmodel.getNews()
    }
}
