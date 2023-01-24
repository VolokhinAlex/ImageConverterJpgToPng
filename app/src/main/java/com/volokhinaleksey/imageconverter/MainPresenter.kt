package com.volokhinaleksey.imageconverter

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit

class MainPresenter(private val repository: MainRepository) :
    MvpPresenter<MainView>() {

    var model: ImageData? = null

    private val compositeDisposable = CompositeDisposable()

    fun convertJpgToPng() {
        model?.let {
            compositeDisposable.add(
                repository.convertImageToPng(it)
                    .delay(3, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ data ->
                        viewState.onSuccess(data)
                    }, { error ->
                        viewState.onError(error)
                    })
            )
        }
    }

}