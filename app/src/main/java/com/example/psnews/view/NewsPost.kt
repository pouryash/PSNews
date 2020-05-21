package com.example.psnews.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.psnews.R
import com.example.psnews.databinding.ActivityNewsPostBinding
import com.example.psnews.extentions.toast
import com.example.psnews.helper.Commen
import com.example.psnews.helper.Constants
import com.example.psnews.helper.SharedPrefrenceManager
import com.example.psnews.model.News
import com.example.psnews.network.Status
import com.example.psnews.viewmodel.MainNewsViewModel
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.activity_news_post.*
import kotlinx.android.synthetic.main.activity_profile.iv_back
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class NewsPost : AppCompatActivity() {

    val sharedPreferences: SharedPrefrenceManager by inject()
    lateinit var binding: ActivityNewsPostBinding
    private var newsViewModel: MainNewsViewModel = get(parameters = { parametersOf(this) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsPostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initView()
        setupView()

    }

    private fun setupView() {

        newsViewModel = MainNewsViewModel(intent.getParcelableExtra("news")!!, this)
        binding.newsViewModel = newsViewModel

        newsViewModel.isLike(sharedPreferences.getUser().id!!, newsViewModel.newsId!!)

        newsViewModel.mutableIsNewsLiked.observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    newsViewModel.liked = it.data!!.data
                }
                Status.ERROR -> {
                    toast(msg = it.error!!)
                }
            }
        })

        newsViewModel.mutableLikeAndDissLike.observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    iv_news_like.isEnabled = true
                }
                Status.ERROR -> {
                    iv_news_like.isEnabled = true
                    toast(msg = it.error!!)
                }
            }
        })

        fab_share_news.setOnClickListener {
            var share: Intent = Intent(Intent.ACTION_SEND)
            share.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            share.putExtra(Intent.EXTRA_SUBJECT, newsViewModel.title)
            share.putExtra(Intent.EXTRA_TEXT, newsViewModel.content)
            share.putExtra(
                Intent.EXTRA_STREAM,
                Commen.saveTempImage(this, news_image, newsViewModel.imageUrl)
            )
            share.type = "image/*"
            startActivity(Intent.createChooser(share, "set news to:"))

        }

        iv_back.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        iv_news_like.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton?) {
                newsViewModel.likeAndDisLike(
                    sharedPreferences.getUser().id.toString(),
                    newsViewModel.newsId.toString()
                )
                newsViewModel.likeCount = (newsViewModel.likeCount.toInt() + 1).toString()
            }

            override fun unLiked(likeButton: LikeButton?) {
                newsViewModel.likeAndDisLike(
                    sharedPreferences.getUser().id.toString(),
                    newsViewModel.newsId.toString()
                )
                newsViewModel.likeCount = (newsViewModel.likeCount.toInt() - 1).toString()
            }

        })
    }

    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(
                this,
                R.color.colorWhite
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().decorView.systemUiVisibility = 0
            }
        }
    }

    private fun initView() {
        //prevent like change before update
        iv_news_like.isEnabled = false
        //make news content scrollable
        tv_news_content.movementMethod = ScrollingMovementMethod()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        if (Build.VERSION.SDK_INT in 12..18) { // lower api
            val v = this.window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView = window.decorView
            val uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
        }
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        val news:News = intent.getParcelableExtra("news")!!
        if (!news.likeCount.equals(newsViewModel.likeCount)){
            returnIntent.putExtra("result", newsViewModel.likeCount)
            returnIntent.putExtra("pos", intent.getIntExtra("pos",0))
            setResult(Activity.RESULT_OK, returnIntent)
        }else{
            setResult(Activity.RESULT_CANCELED, returnIntent)
        }
        this.finish()
    }
}
