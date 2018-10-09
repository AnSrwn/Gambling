package com.jerem.imagebackground

import android.view.View
import com.bumptech.glide.request.target.ViewTarget
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.transition.Transition


abstract class ViewBackgroundTarget<Z>(view: View) : ViewTarget<View, Z>(view){


    override fun onResourceReady(resource: Z, transition: Transition<in Z>?) {
            setResource(resource)
    }

    fun setDrawable(drawable: Drawable) {
        setBackground(drawable)
    }

    fun getCurrentDrawable(): Drawable {
        return view.background
    }

    protected fun setBackground(drawable: Drawable) {
            view.background = drawable
    }
    protected abstract fun setResource(resource: Z)

}