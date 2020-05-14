package com.example.psnews.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.psnews.R
import com.example.psnews.databinding.ActivityMainBinding
import com.example.psnews.viewmodel.NewsViewModel
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
    }

    fun initView() {
        viewmodel.getNews()
    }
}
