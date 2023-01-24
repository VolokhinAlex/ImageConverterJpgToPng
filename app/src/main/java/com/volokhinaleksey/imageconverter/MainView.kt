package com.volokhinaleksey.imageconverter

import android.graphics.Bitmap
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface MainView : MvpView {

    fun onSuccess(bitmap: Bitmap)
    fun onError(error: Throwable)

}