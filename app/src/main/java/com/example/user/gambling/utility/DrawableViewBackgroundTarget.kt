package com.jerem.imagebackground

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.request.transition.Transition

/**
 * A view target for a drawable resource.
 */
class DrawableViewBackgroundTarget(view: View) : ViewBackgroundTarget<Drawable>(view){
    override fun setResource(resource: Drawable) {
        setBackground(resource)
    }
}