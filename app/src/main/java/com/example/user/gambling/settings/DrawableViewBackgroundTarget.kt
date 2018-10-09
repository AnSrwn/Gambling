package com.jerem.imagebackground

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.request.transition.Transition

class DrawableViewBackgroundTarget(view: View) : ViewBackgroundTarget<Drawable>(view){
    override fun setResource(resource: Drawable) {
        setBackground(resource)
    }
}