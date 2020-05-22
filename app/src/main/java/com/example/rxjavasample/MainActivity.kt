package com.example.rxjavasample

import android.os.Bundle
import android.os.SystemClock
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.example.rxjavasample.viewmodels.StateViewModel
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val stateViewModel by viewModels<StateViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        dataGrab()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    private fun dataGrab() {
        if (!stateViewModel.ST_INITIAL) return
        if (!stateViewModel.initialIntegrity()) return
        stateViewModel.initial_INTEGRITY_FAIL = true
        stateViewModel.prospective()
            .subscribeOn(Schedulers.newThread())
            .subscribeWith(object : DisposableObserver<Boolean>() {
                override fun onComplete() {
                    println("completed initial?")
                }

                override fun onNext(t: Boolean?) {
                    stateViewModel.ST_INITIAL = false
                    stateViewModel.fetchDataSets("First Attempt")
                }

                override fun onError(e: Throwable?) {
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
