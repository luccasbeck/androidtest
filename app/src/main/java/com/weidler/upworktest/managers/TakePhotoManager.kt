package com.weidlersoftware.upworktest.managers

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import com.weidler.upworktest.TakingPhotoActivity
import com.weidlersoftware.upworktest.models.PicturesPicture
import io.reactivex.subjects.PublishSubject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class TakePhotoManager {
   private var mPhotoPath: String = ""
    private var i = 0
   var publisher = PublishSubject.create<PicturesPicture>()

    @Throws(IOException::class)
    private fun createImageFile(dir: File): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val f = File.createTempFile(timeStamp, ".jpg", dir)
         mPhotoPath =  f.canonicalPath
        Log.e("ImageCreated", mPhotoPath)
        return f
    }

//    fun takePicture( activity: Activity, dir: File) : PublishSubject<PicturesPicture>{
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra("android.intent.extra.quickCapture",true).also {takePic ->
//            takePic.resolveActivity(activity.packageManager)?.also {
//                val photoFile = try {
//                    createImageFile(dir)
//                }catch (e: IOException){
//                   e.printStackTrace()
//                    null
//                }
//                photoFile?.also {file ->
//                    val photoURI = FileProvider.getUriForFile(activity,
//                        "com.weidlersoftware.upworktest.fileprovider",
//                        file)
//                    takePic.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                    takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    activity.startActivityForResult(takePic, 101)
//                }
//            }
//        }
//
//       return publisher
//    }

//    fun activityResult(requestCode: Int, resultCode: Int, data: Intent?){
//        if(requestCode == 101 && resultCode == Activity.RESULT_OK && mPhotoPath.isNotEmpty()){
//            Log.e("Image", "saved")
//            val file = File(mPhotoPath)
//           if(file.exists() && file.length() > 0) {
//               val picture = PicturesPicture()
//               picture.imageUri = mPhotoPath
//               if (mPhotoPath.isNotEmpty()) {
//                   publisher.onNext(picture)
//               }
//           }else{
//               file.delete()
//           }
//
//        }
//    }


    //[Testee]
    fun takePicture( activity: Activity, dir: File) : PublishSubject<PicturesPicture>{
        Intent(activity, TakingPhotoActivity::class.java).also {takePic ->
            takePic.resolveActivity(activity.packageManager)?.also {
                val photoFile = try {
                    createImageFile(dir)
                }catch (e: IOException){
                    e.printStackTrace()
                    null
                }
                photoFile?.also {file ->
                    val photoURI = FileProvider.getUriForFile(activity,
                        "com.weidlersoftware.upworktest.fileprovider",
                        file)
                    takePic.putExtra(TakingPhotoActivity.EXTRA_OUTPUT, mPhotoPath)
                    activity.startActivityForResult(takePic, TakingPhotoActivity.MEDIA_REQUEST_CODE)
                }
            }
        }

        return publisher
    }


    fun activityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if(requestCode == TakingPhotoActivity.MEDIA_REQUEST_CODE && resultCode == Activity.RESULT_OK && mPhotoPath.isNotEmpty()){
            Log.e("Image", "saved")
            val file = File(mPhotoPath)
            if(file.exists() && file.length() > 0) {
                val picture = PicturesPicture()
                picture.imageUri = mPhotoPath
                if (mPhotoPath.isNotEmpty()) {
                    publisher.onNext(picture)
                }
            }else{
                file.delete()
            }

        }
    }

    companion object {

        val instance = TakePhotoManager()
    }
}