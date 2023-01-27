package com.volokhinaleksey.imageconverter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import io.reactivex.rxjava3.core.Single
import java.io.FileOutputStream

interface MainRepository {
    fun convertImageToPng(data: ImageData): Single<Bitmap>
}

class MainRepositoryImpl : MainRepository {
    override fun convertImageToPng(data: ImageData): Single<Bitmap> = Single.create {
        val pathImage = data.path.split("/")
        val imageName = pathImage[pathImage.size - 1]
            .split(".")[0]
        val outPutStream =
            FileOutputStream("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}/$imageName.png")
        if (data.bitmap.compress(Bitmap.CompressFormat.PNG, 100, outPutStream)) {
            it.onSuccess(BitmapFactory.decodeFile(data.path))
        } else {
            it.onError(Exception("Conversion problem"))
        }
    }
}