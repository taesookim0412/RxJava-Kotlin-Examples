package com.example.rxjavasample.viewmodels

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class StateViewModel(application: Application): AndroidViewModel(application){
    var disposables = CompositeDisposable()
    var progress: Boolean = false
    var firstData = ""
    var secondData = ""

    init{
        addAttempt("First Attempt")

    }

    private fun addAttempt(strAttempt: String) {
        disposables.add(firstObservable(strAttempt)
            .subscribeOn(Schedulers.newThread())
            /*.switchMap { firstObservable("Third Attempt") }*/
            .subscribeWith(object : DisposableObserver<String>() {
                override fun onComplete() {
                    println("Completed")
                }

                override fun onNext(t: String?) {
                    if (t == null) onError(Throwable("Data returned null")).run { return }
                    firstData = t ?: ""
                    println(firstData)
                    addAttempt("Third Attempt")

                }

                override fun onError(e: Throwable?) {
                    println("Error: " + e?.message)
                }


            })
        )
    }

    fun firstObservable(numberAttempt: String): Observable<String> {
        return Observable.defer {
            SystemClock.sleep(1000)

            if (numberAttempt == "Third Attempt"){
                println(numberAttempt)
                disposables.clear()
                println(disposables.size())
                4/0
            }

            return@defer Observable.just(numberAttempt)
        }
    }
}