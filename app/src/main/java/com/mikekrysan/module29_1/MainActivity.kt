package com.mikekrysan.module29_1

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Math.hypot
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private var isRevealed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        reveal()    //*

        Executors.newSingleThreadExecutor().execute {   //newSingleThreadExecutor - запускаем один поток для выполнения задачи, и в нем вызываем метод execute в вариации с лямбдой
            //В этом методе будем проверять когда у нас будет прикреплено наше view к экрану. Это можно сделать при помощи цикла while, который будет true (бесконечный)
            while (true) {
                //проверяем, прикреплено ли наше view к экрану
                if(buttons_container.isAttachedToWindow) {
                    //Чтобы приложение не падало, нужно работать с UI  из UI-тредами. Так как мы создали новый тред, чтобы наш код заработал, нам нужно вернутся опять в UI-thread
                    runOnUiThread {
                        reveal()
                    }
                    //выходим из метода execute(), чтобы тред был уничтожен и не потреблял ресурсы
                    return@execute
                }
            }
        }

        fab.setOnClickListener {
            if(!isRevealed) {
                reveal()
            } else {
                hide()
            }
        }
    }

    private fun reveal() {
        val x: Int = fab.x.roundToInt() + fab.width / 2
        val y: Int = fab.y.roundToInt() + fab.height / 2
        val startRadius = 0
        val endRadius = hypot(buttons_container.width.toDouble(), buttons_container.height.toDouble())
        val anim = ViewAnimationUtils.createCircularReveal(buttons_container, x, y, startRadius.toFloat(), endRadius.toFloat())
        anim.duration = 1000

        buttons_container.visibility = View.VISIBLE
        anim.start()
        isRevealed = true
    }

    private fun hide() {
        val x: Int = fab.x.roundToInt() + fab.width / 2
        val y: Int = fab.y.roundToInt() + fab.height / 2
        val startRadius: Int = main_container.width.coerceAtLeast(buttons_container.height)
        val endRadius = 0

        val anim = ViewAnimationUtils.createCircularReveal(buttons_container, x, y, startRadius.toFloat(), endRadius.toFloat())
        anim.duration = 500

        //Слушатель конца анимации, когда анимация закончится, мы скроем View
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animator: Animator) {
                buttons_container.visibility = View.GONE
            }
        })
        anim.start()
        isRevealed = false
    }
}

/*
* - Проверять нужно в отдельном потоке, потому что в главном потоке UI, то наше приложение может зафризится, на самом деле приложение не пойдет даже к выполнению.
*   Поэтому, нам нужно создать отдельный поток. Вариантов создать отдельный поток масса, один из них, с помощью класса Executor
*   Также новый тред можно сделать при помощи Hendler, RX, Coroutine
 */