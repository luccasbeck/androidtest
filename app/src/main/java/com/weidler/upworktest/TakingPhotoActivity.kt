package com.weidler.upworktest

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Button
import android.widget.Toast
import com.weidlersoftware.upworktest.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.graphics.Matrix


//[Testee]
class TakingPhotoActivity : AppCompatActivity(), SurfaceHolder.Callback, Camera.PictureCallback {

    private var surfaceHolder: SurfaceHolder? = null
    private var camera: Camera? = null

    private var surfaceView: SurfaceView? = null

    private val neededPermissions = arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE)

    private val photoUri by lazy {
        intent!!.extras!!.getString(EXTRA_OUTPUT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_taking_photo)

        surfaceView = findViewById(R.id.surfaceView)
        val result = checkPermission()
        if (result) {
            setupSurfaceHolder()
        }
    }

    private fun checkPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            val permissionsNotGranted = ArrayList<String>()
            for (permission in neededPermissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNotGranted.add(permission)
                }
            }
            if (permissionsNotGranted.size > 0) {
                var shouldShowAlert = false
                for (permission in permissionsNotGranted) {
                    shouldShowAlert = ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
                }

                val arr = arrayOfNulls<String>(permissionsNotGranted.size)
                val permissions = permissionsNotGranted.toArray(arr)
                if (shouldShowAlert) {
                    showPermissionAlert(permissions)
                } else {
                    requestPermissions(permissions)
                }
                return false
            }
        }
        return true
    }

    private fun showPermissionAlert(permissions: Array<String?>) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle(R.string.permission_required)
        alertBuilder.setMessage(R.string.permission_message)
        alertBuilder.setPositiveButton(android.R.string.yes) { _, _ -> requestPermissions(permissions) }
        val alert = alertBuilder.create()
        alert.show()
    }

    private fun requestPermissions(permissions: Array<String?>) {
        ActivityCompat.requestPermissions(this@TakingPhotoActivity, permissions, REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                for (result in grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this@TakingPhotoActivity, R.string.permission_warning, Toast.LENGTH_LONG).show()
                        setViewVisibility(R.id.showPermissionMsg, View.VISIBLE)
                        return
                    }
                }

                setupSurfaceHolder()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setViewVisibility(id: Int, visibility: Int) {
        val view = findViewById<View>(id)
        view!!.visibility = visibility
    }

    private fun setupSurfaceHolder() {
        setViewVisibility(R.id.startBtn, View.VISIBLE)
        setViewVisibility(R.id.surfaceView, View.VISIBLE)

        surfaceHolder = surfaceView!!.holder
        surfaceHolder!!.addCallback(this)
        setBtnClick()
    }

    private fun setBtnClick() {
        val startBtn = findViewById<Button>(R.id.startBtn)
        startBtn?.setOnClickListener { captureImage() }
    }

    private fun captureImage() {
        if (camera != null) {
            camera!!.takePicture(null, null, this)
        }
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        startCamera()
    }

    private fun startCamera() {
        camera = Camera.open()
        camera!!.setDisplayOrientation(90)
        try {
            camera!!.setPreviewDisplay(surfaceHolder)
            camera!!.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        resetCamera()
    }

    private fun resetCamera() {
        if (surfaceHolder!!.surface == null) {
            // Return if preview surface does not exist
            return
        }

        // Stop if preview surface is already running.
        camera!!.stopPreview()
        try {
            // Set preview display
            camera!!.setPreviewDisplay(surfaceHolder)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Start the camera preview...
        camera!!.startPreview()
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        releaseCamera()
    }

    private fun releaseCamera() {
        camera!!.stopPreview()
        camera!!.release()
        camera = null
    }

    override fun onPictureTaken(bytes: ByteArray, camera: Camera) {

        saveImage(bytes)
        //resetCamera()
        releaseCamera()
        setResult(RESULT_OK, Intent())
        finish()
    }

    private fun toBitmap(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }

    private fun rotate(`in`: Bitmap, angle: Int): Bitmap {
        val mat = Matrix()
        mat.postRotate(angle.toFloat())
        return Bitmap.createBitmap(`in`, 0, 0, `in`.width, `in`.height, mat, true)
    }

    private fun saveImage(bytes: ByteArray) {
        val display = windowManager.defaultDisplay
        var rotation = 0
        when (display.rotation) {
            Surface.ROTATION_0 // This is display orientation
            -> rotation = 90
            Surface.ROTATION_90 -> rotation = 0
            Surface.ROTATION_180 -> rotation = 270
            Surface.ROTATION_270 -> rotation = 180
        }

        var bitmap = toBitmap(bytes)
        bitmap = rotate(bitmap, rotation)
        val outStream: FileOutputStream
        try {
            val file = File(photoUri)
            outStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream.flush()
            outStream.close()

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val REQUEST_CODE = 100
        const val MEDIA_REQUEST_CODE = 20001
        const val EXTRA_OUTPUT = "extra_output"
    }
}