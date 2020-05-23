package com.example.psnews.helper

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.psnews.BuildConfig
import com.example.psnews.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.random.Random


object Common {

    fun isNetworkConnected(context: Context): Boolean {
        val cm: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities: NetworkCapabilities = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }

        } else {
            return cm.getActiveNetworkInfo() != null && cm.activeNetworkInfo.isConnected()
        }
        return false
    }

    fun startLoading(view: ImageView, root: ViewGroup, context: Context) {
        root.bringChildToFront(view)
        context as Activity
        root.visibility = View.VISIBLE
        val animation: AnimatedVectorDrawableCompat =
            AnimatedVectorDrawableCompat.create(context, R.drawable.loading_anim)!!
        view.setImageDrawable(animation.current)
        animation.start()

        context.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )

        animation.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                if (root.visibility == View.VISIBLE)
                    view.post { animation.start() }
                else
                    context.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }

    fun saveTempImage(context: Context, view: ImageView, url: String): Uri {

        if (view.contentDescription.equals(Constants.IMAGE_FAILD))
            return Uri.EMPTY

        val tempFile = File(context.cacheDir.toString() + "/NewsImage")
        if (!tempFile.isDirectory) if (!tempFile.mkdirs()) return Uri.EMPTY

        val tempImg = File(tempFile, url.substring(url.lastIndexOf("/") + 1))
        if (tempImg.exists()) return FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            tempImg
        )

        val bitmap = view.drawable.toBitmap()
        val stream = FileOutputStream(tempImg)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()

        return FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            tempImg
        );

    }

    fun correctRotation(uri: Uri, bitmap: Bitmap, context: Context): Bitmap {

        val filePath = getRealPathFromURI(uri.toString(), context)
        val bmp: Bitmap = bitmap

        val exif: ExifInterface

        exif = ExifInterface(filePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, 0
        )
        val matrix = Matrix()
        if (orientation == 6) {
            matrix.postRotate(90f)
        } else if (orientation == 3) {
            matrix.postRotate(180f)
        } else if (orientation == 8) {
            matrix.postRotate(270f)
        }
        val rotatedBitmap = Bitmap.createBitmap(
            bmp, 0, 0,
            bmp.getWidth(), bmp.getHeight(), matrix,
            true
        )

        return rotatedBitmap
    }

    fun reduceImageSize(bitmap: Bitmap, context: Context): File {
        val filesDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val imageFile = File(filesDir, "${Random.nextInt(0, 100).toString()}.jpeg")
        val os: OutputStream

        os = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, os)
        os.flush()
        os.close()
        return imageFile
    }

    private fun getRealPathFromURI(contentURI: String, context: Context): String? {
        val contentUri = Uri.parse(contentURI)
        val cursor: Cursor = context.contentResolver.query(contentUri, null, null, null, null)!!
        return run {
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(index)
        }
    }

}