package com.example.psnews.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.psnews.R
import com.example.psnews.extentions.toast
import com.example.psnews.helper.Common
import com.example.psnews.helper.Constants
import com.example.psnews.helper.RuntimePermissionsActivity
import com.example.psnews.helper.SharedPrefrenceManager
import com.example.psnews.model.News
import com.example.psnews.network.Status
import com.example.psnews.viewmodel.AddNewsViewModel
import com.example.psnews.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_add_news.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import java.io.File
import java.io.InputStream
import java.net.URLEncoder

class AddNews : RuntimePermissionsActivity() {

    val sharedPrefrenceManager: SharedPrefrenceManager by inject()
    private val addNewsViewModel: AddNewsViewModel by viewModel()
    var newsFile: File= File("")
    var shouldUpdateNewsList = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_news)

        setupView()
    }

    override fun onPermissionsGranted(requestCode: Int) {
        fab_add_image.performClick()
    }

    override fun onPermissionsDeny(requestCode: Int) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun setupView() {

        addNewsViewModel.mutableInsertedNewsResponse.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Common.startLoading(loading, lin_loading_dim, this)
                }
                Status.SUCCESS -> {
                    shouldUpdateNewsList = true
                    toast(msg = it.data!!.message)
                    lin_loading_dim.visibility = View.INVISIBLE

                    val returnIntent = Intent()
                    returnIntent.putExtra("shouldUpdate", shouldUpdateNewsList)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
                Status.ERROR -> {
                    shouldUpdateNewsList = false
                    toast(msg = it.error!!)
                    lin_loading_dim.visibility = View.INVISIBLE
                }
            }
        })

        iv_cancel.setOnClickListener {
            onBackPressed()
        }

        fab_add_image.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                this.requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    , Constants.REQUEST_EXTERNAL_STORAGE
                )
            } else {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                this.startActivityForResult(
                    photoPickerIntent,
                    Constants.REQUEST_EXTERNAL_STORAGE
                )
            }
        }

        fab_share_news.setOnClickListener {
            var news: News? = null
            if (et_title_share_news.text.toString().isNotEmpty() && et_content_share_news.text.toString()
                    .isNotEmpty() && newsFile.exists()
            ) {
                val requestFile: RequestBody =
                    RequestBody.create(MediaType.parse("multipart/form-data"), newsFile)
                val image =
                    MultipartBody.Part.createFormData(
                        "image",
                        URLEncoder.encode(newsFile.name, "utf-8"),
                        requestFile
                    )
                news = News(
                    "",
                    et_title_share_news.text.toString(),
                    et_content_share_news.text.toString(),
                    "",
                    "",
                    sharedPrefrenceManager.getUser().name,
                    "",
                    sharedPrefrenceManager.getUser().id.toString()
                )
                addNewsViewModel.insertNews(news, image, "newsMedia")
            } else if (!newsFile.exists()) {
                news = News(
                    "",
                    et_title_share_news.text.toString(),
                    et_content_share_news.text.toString(),
                    "",
                    "",
                    sharedPrefrenceManager.getUser().name,
                    "",
                    sharedPrefrenceManager.getUser().id.toString()
                )
                addNewsViewModel.insertNews(news, MultipartBody.Part.createFormData("", ""), "news")
            } else {
                toast(getString(R.string.enter_all_filed), Toast.LENGTH_SHORT)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_EXTERNAL_STORAGE) {
                try {
                    val imageUri: Uri? = data!!.data
                    val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    val rotatedImage = Common.correctRotation(imageUri, selectedImage, this)
                    iv_newsImage_addNews.setImageBitmap(rotatedImage)

                    //prepare data for upload newsImage
                    newsFile = Common.reduceImageSize(rotatedImage, this)

                } catch (e: Exception) {
                    e.printStackTrace()
                    toast("Something went wrong")
                }

            }
        }
    }


    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
