package com.weidlersoftware.upworktest

import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.MotionEvent
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_detailed.*
import android.content.Intent
import android.net.Uri


class DetailedActivity : AppCompatActivity() {

    private var x1: Float = 0.toFloat()
    private var x2: Float = 0.toFloat()
    private var y1: Float = 0.toFloat()
    private var y2: Float = 0.toFloat()
    private val MIN_DISTANCE = 30

    private var currentIndex: Int = 0

    private val imageUriList by lazy {
        intent?.extras?.getStringArrayList("image_array")
    }

    private val startImage by lazy {
        intent?.extras?.getString("image")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        startImage ?: finish()
        Glide.with(this@DetailedActivity)
            .load(startImage)
            .into(iv_photo)

        if (imageUriList.isNullOrEmpty()) finish()
        currentIndex = imageUriList!!.indexOf(startImage)

        btnShare.setOnClickListener {
            val i = Intent(Intent.ACTION_SEND)
            i.putExtra(Intent.EXTRA_EMAIL, arrayOf("tester@tester.edu"))
            i.putExtra(Intent.EXTRA_SUBJECT, "On The Job")
            i.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUriList!![currentIndex]))

            i.type = "image/png"
            startActivity(Intent.createChooser(i, "Share you on the jobing"))
        }


        var rY = 0F
        iv_photo.setOnTouchListener { v, event ->
            val dif = event.rawY - rY
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val orientation = resources.configuration.orientation
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        rY = event.rawY
                    }

                    //For Transition
                    x1 = event.x
                    y1 = event.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val orientation = resources.configuration.orientation
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        print(dif)
                        if (Math.abs(dif) > 900) {
                            iv_photo.alpha = 1F
                            ActivityCompat.finishAfterTransition(this)
                        } else
                            iv_photo.animate().translationY(0F)
                                .alpha(1F)
                                .setDuration(100)
                                .start()
                    }

                    //For Transition
                    x2 = event.x
                    y2 = event.y
                    val deltaX = x2 - x1
                    val deltaY = y2 - y1
                    if (deltaX > MIN_DISTANCE) {
                        swipeLeftToRight()
                    } else if (Math.abs(deltaX) > MIN_DISTANCE) {
                        swipeRightToLeft()
                    } else if (deltaY > MIN_DISTANCE) {
                        swipeTopToBottom()
                    } else if (Math.abs(deltaY) > MIN_DISTANCE) {
                        swipeBottomToTop()
                    }
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (Math.abs(dif) > 900) {
                        ActivityCompat.finishAfterTransition(this)
                    } else {
                        iv_photo.translationY = dif
                        iv_photo.alpha = 1 - Math.abs(dif) / 1000
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun swipeLeftToRight() {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            showBeforeImage()
        }
    }

    private fun swipeRightToLeft() {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            showNextImage()
        }
    }

    private fun swipeTopToBottom() {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            showBeforeImage()
        }
    }

    private fun swipeBottomToTop() {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            showNextImage()
        }
    }

    private fun showNextImage() {
        imageUriList ?: return
        currentIndex += 1
        if (currentIndex <= imageUriList!!.size - 1) {
            Glide.with(this@DetailedActivity)
                .load(imageUriList!![currentIndex])
                .into(iv_photo)
        } else
            currentIndex -= 1
    }

    private fun showBeforeImage() {
        imageUriList ?: return
        currentIndex -= 1
        if (currentIndex >= 0) {
            Glide.with(this@DetailedActivity)
                .load(imageUriList!![currentIndex])
                .into(iv_photo)
        } else
            currentIndex += 1


    }
}
