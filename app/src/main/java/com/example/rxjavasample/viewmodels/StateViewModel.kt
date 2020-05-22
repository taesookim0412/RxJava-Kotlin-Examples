package com.example.rxjavasample.viewmodels

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers


/*go crazy
grab sharedprefs ->
DATASET0
emit TRUE/FALSE

observe prospective on UI ->
-> zip -> valid -> emit valid


DATASET1
DATASET2
EMIT ST_DATASETS_FETCHED
DATASET3
EMIT COMPLETED (on ST_VALID)


 */
class StateViewModel(application: Application) : AndroidViewModel(application) {
    var disposables = CompositeDisposable()
    var firstData = ""
    var secondData = ""




    // State variables
    var ST_INITIAL = true

    var ST_DATASETS_FETCHED = false
    var ST_VALID = false


    fun fetchDataSets(strAttempt: String) {
        firstObservable(strAttempt).zipWith(firstObservable("Second Attempt"),
            BiFunction<String, String, String> { str1, str2 ->
                val comb = str1+str2
                firstData = str1
                secondData = str2
                ST_DATASETS_FETCHED = true
                return@BiFunction comb
            })
            .subscribeOn(Schedulers.newThread())
            .subscribeWith(object : DisposableObserver<String>() {
                //! Not covered by tryCatch
                override fun onComplete() {
                    println("Completed")
                    ST_VALID = true
                    //4/0

                }

                override fun onNext(t: String?) {
                    if (t == null) onError(Throwable("Data returned null")).run { return }

                }

                override fun onError(e: Throwable?) {
                    println("Error: " + e?.message)
                }


            })

    }

    var initial_INTEGRITY_FAIL = false
    fun initialIntegrity(): Boolean{
        if (initial_INTEGRITY_FAIL) return false
        val newIntegrity = !initial_INTEGRITY_FAIL
        initial_INTEGRITY_FAIL = newIntegrity
        return newIntegrity
    }

    fun prospective(): Observable<Boolean>{
        return Observable.defer{
            //Emulate some kind of getstr
            SystemClock.sleep(2000)
            return@defer Observable.just(true)
        }
    }

    fun firstObservable(numberAttempt: String): Observable<String> {
        return Observable.defer {
            SystemClock.sleep(2000)
            println(numberAttempt + "in the works")
            if (numberAttempt == "Third Attempt") {
                disposables.clear()
                println(disposables.size())

                //?Error check
//                4 / 0
            }

            return@defer Observable.just(numberAttempt)
        }
    }

}