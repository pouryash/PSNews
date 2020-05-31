package com.example.psnews.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.example.psnews.R
import com.example.psnews.helper.Constants
import com.example.psnews.helper.SharedPrefrenceManager
import com.example.psnews.viewmodel.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.image_picker_fragment.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.koin.android.ext.android.get


class PickerSheetFragment : BottomSheetDialogFragment() {

    public lateinit var userViewModel: UserViewModel
    val sharedPreferences: SharedPrefrenceManager = get()
    lateinit var sheetView: View

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        sheetView = LayoutInflater.from(context).inflate(R.layout.image_picker_fragment, null)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(sheetView)

        (sheetView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))

        val params =
            (sheetView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior


        if (behavior is BottomSheetBehavior) {
            behavior.setBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            dismiss()
                        }
                        BottomSheetBehavior.STATE_DRAGGING,
                        BottomSheetBehavior.STATE_SETTLING,
                        BottomSheetBehavior.STATE_HALF_EXPANDED,
                        BottomSheetBehavior.STATE_EXPANDED,
                        BottomSheetBehavior.STATE_COLLAPSED
                        -> {
                        }
                        else -> {
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }
        setupView()
    }


    private fun setupView() {

        sheetView.lin_galery.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requireActivity().requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    , Constants.REQUEST_EXTERNAL_STORAGE
                )
            } else {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                requireActivity().startActivityForResult(
                    photoPickerIntent,
                    Constants.REQUEST_EXTERNAL_STORAGE
                )
                dismiss()
            }
        }

        sheetView.lin_take_photo.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requireActivity().requestPermissions(
                    arrayOf(Manifest.permission.CAMERA)
                    , Constants.REQUEST_CAMERA
                )
            } else {
                val photoPickerIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                requireActivity().startActivityForResult(
                    photoPickerIntent,
                    Constants.REQUEST_CAMERA
                )
                dismiss()
            }
        }

        sheetView.lin_remove_avatar.setOnClickListener {
            val uid = RequestBody.create(
                MediaType.parse("text/plain"),
                sharedPreferences.getUser().id!!
            )

               userViewModel.deleteAvatar(uid, "delete")
               dismiss()

        }
    }

    fun handlePermission(req: Int) {

        when (req) {
            Constants.REQUEST_EXTERNAL_STORAGE -> {
                sheetView.lin_galery.performClick()
            }
            Constants.REQUEST_CAMERA -> {
                sheetView.lin_take_photo.performClick()
            }
            else -> {
            }
        }
    }

}