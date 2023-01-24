package com.volokhinaleksey.imageconverter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import io.reactivex.rxjava3.core.Single
import java.io.FileOutputStream

interface MainRepository {
    fun convertImageToPng(data: ImageData): Single<Bitmap>
}

class MainRepositoryImpl : MainRepository {
    override fun convertImageToPng(data: ImageData): Single<Bitmap> = Single.create {
        val outPutStream = FileOutputStream(data.path)
        if (data.bitmap.compress(Bitmap.CompressFormat.PNG, 100, outPutStream)) {
            Log.e("", data.path)
            it.onSuccess(BitmapFactory.decodeFile(data.path))
        } else {
            it.onError(Exception("Conversion problem"))
        }
    }
}