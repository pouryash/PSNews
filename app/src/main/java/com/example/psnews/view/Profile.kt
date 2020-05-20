package com.example.psnews.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.psnews.R
import com.example.psnews.databinding.ActivityProfileBinding
import com.example.psnews.extentions.toast
import com.example.psnews.helper.*
import com.example.psnews.model.User
import com.example.psnews.network.Status
import com.example.psnews.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import java.io.File
import java.io.InputStream
import java.net.URLEncoder


class Profile : RuntimePermissionsActivity() {

    val pickerFragment = PickerSheetFragment()
    lateinit var binding: ActivityProfileBinding
    val sharedPreferences: SharedPrefrenceManager = get()
    private val userViewModel: UserViewModel by inject(
        qualifier = named("a"),
        parameters = { parametersOf(sharedPreferences.getUser()) })
    lateinit var avatarFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setupView()

    }

    override fun onPermissionsGranted(requestCode: Int) {

        pickerFragment.handlePermission(requestCode)

    }

    override fun onPermissionsDeny(requestCode: Int) {
        pickerFragment.dismiss()
    }

    private fun initView() {
        binding.userViewModel = userViewModel
    }


    private fun setupView() {

        btn_save_profile.setOnClickListener(View.OnClickListener {
            val user: User = User(
                et_editName_profile.text.toString(),
                et_editemail_profile.text.toString(),
                "",
                sharedPreferences.getUser().id.toString(),
                ""
            )
            if (Validator.emailValidator(user.email, this@Profile)) {
                userViewModel.updateUser(user)
            } else if (et_editName_profile.text.toString().trim().isEmpty()) {
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

        userViewModel.mutableDeleteAvatar.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Commen.startLoading(loading, lin_loading_dim, this)
                }
                Status.SUCCESS -> {
                    userViewModel.userAvatar = "123"
                    lin_loading_dim.visibility = View.INVISIBLE
                    toast(msg = it.data!!.message)
                }
                Status.ERROR -> {
                    toast(msg = it.error!!)
                    lin_loading_dim.visibility = View.INVISIBLE
                }
            }
        })

        userViewModel.mutableUserUploadImage.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    Commen.startLoading(loading, lin_loading_dim, this)
                }
                Status.SUCCESS -> {
                    var user: User = sharedPreferences.getUser()
                    user.userAvatar = it.data!!.data.userAvatar
                    sharedPreferences.saveUser(user)
                    lin_loading_dim.visibility = View.INVISIBLE
                    toast(msg = it.data!!.message)
                    avatarFile.delete()
                }
                Status.ERROR -> {
                    toast(msg = it.error!!)
                    lin_loading_dim.visibility = View.INVISIBLE
                    avatarFile.delete()
                }
            }
        })

        btn_logout.setOnClickListener(View.OnClickListener {
            sharedPreferences.clearUserSession()
            var intent: Intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            this.finish()
        })

        iv_back.setOnClickListener(View.OnClickListener {
            this.finish()
        })

        civ_add_avatar.setOnClickListener(View.OnClickListener {

            pickerFragment.show(supportFragmentManager, pickerFragment.tag)
            pickerFragment.userViewModel = userViewModel

        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_EXTERNAL_STORAGE) {
                try {
                    val imageUri: Uri? = data!!.data
                    val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    val rotatedImage = Commen.correctRotation(imageUri, selectedImage, this)
                    civ_profile.clearColorFilter()
                    civ_profile.setImageBitmap(rotatedImage)

                    //prepare data for upload avatar
                    avatarFile = Commen.reduceImageSize(rotatedImage, this)
                    val requestFile: RequestBody =
                        RequestBody.create(MediaType.parse("multipart/form-data"), avatarFile)
                    val avatar =
                        MultipartBody.Part.createFormData(
                            "image",
                            URLEncoder.encode(avatarFile.name, "utf-8"),
                            requestFile
                        )
                    val uid = RequestBody.create(
                        MediaType.parse("text/plain"),
                        sharedPreferences.getUser().id!!
                    )
                    userViewModel.updateAvatar(uid, avatar, "update")
                } catch (e: Exception) {
                    e.printStackTrace()
                    toast("Something went wrong")
                }

            } else if (requestCode == Constants.REQUEST_CAMERA) {
                val photo: Bitmap? = data!!.extras!!["data"] as Bitmap?
                avatarFile = Commen.reduceImageSize(photo!!, this)
                val capturedUri = Uri.fromFile(avatarFile)
                civ_profile.clearColorFilter()
                civ_profile.setImageBitmap(photo)

                val requestFile: RequestBody =
                    RequestBody.create(MediaType.parse("multipart/form-data"), avatarFile)
                val avatar =
                    MultipartBody.Part.createFormData(
                        "image",
                        URLEncoder.encode(avatarFile.name, "utf-8"),
                        requestFile
                    )
                val uid = RequestBody.create(
                    MediaType.parse("text/plain"),
                    sharedPreferences.getUser().id!!
                )
                userViewModel.updateAvatar(uid, avatar, "update")
            }
        } else {
            toast("You haven't picked Image")
        }
    }


    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(
                this,
                R.color.authenthication_secoundry
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().decorView.systemUiVisibility = 0
            }
        }
    }

}
