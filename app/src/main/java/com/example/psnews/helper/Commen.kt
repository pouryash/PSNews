package com.example.psnews.helper

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.psnews.R


object Commen {

    fun startLoading(view: ImageView, root: ViewGroup, context: Context) {
        root.bringChildToFront(view)

        root.visibility = View.VISIBLE
        val animation: AnimatedVectorDrawableCompat =
            AnimatedVectorDrawableCompat.create(context, R.drawable.loading_anim)!!
        view.setImageDrawable(animation.current)
        animation.start()

        animation.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                if (root.visibility == View.VISIBLE)
                    view.post { animation.start() }
            }
        })
    }

}