package com.weidlersoftware.upworktest.managers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

class PermissionManager {
    companion object {
        const val CAMERA_PERMISSION_REQUEST = 101
        const val EXTERNAL_STORAGE_PERMISSION_REQUEST = 102
        const val LOCATION_PERMISSION_REQUEST = 103
        const val READ_PHONE_STATE_REQUEST = 104
        const val CALENDAR_REQUEST = 106
        const val READ_CALL_LOG_REQUEST = 107
        const val WRITE_CALL_LOG_REQUEST = 108
        const val PROCESS_OUTGOING_CALLS_REQUEST = 109
        const val READ_CONTACTS_REQUEST = 110
        const val WRITE_CONTACTS_REQUEST = 111
        const val GET_ACCOUNTS_REQUEST = 112
        const val RECORD_AUDIO_REQUEST = 113
        const val READ_PHONE_NUMBERS_REQUEST = 114
        const val CALL_PHONE_REQUEST = 115
        const val ANSWER_PHONE_CALLS_REQUEST = 116
        const val ADD_VOICEMAIL_REQUEST = 117
        const val USE_SIP_REQUEST = 118
        const val BODY_SENSORS_REQUEST = 119
        const val SEND_SMS_REQUEST = 120
        const val RECEIVE_SMS_REQUEST = 121
        const val READ_SMS_REQUEST = 122
        const val RECEIVE_WAP_PUSH_REQUEST = 123
        const val RECEIVE_MMS_REQUEST  = 124

    }


    private var permissionCallbacks = HashMap<Int, () -> Unit>()

    private fun numToPermission(num: Int): Array<String> {
        return when (num) {
            CAMERA_PERMISSION_REQUEST -> arrayOf(Manifest.permission.CAMERA)
            EXTERNAL_STORAGE_PERMISSION_REQUEST -> arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            LOCATION_PERMISSION_REQUEST -> arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            READ_PHONE_STATE_REQUEST -> arrayOf(Manifest.permission.READ_PHONE_STATE)
            CALENDAR_REQUEST -> arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR)
            READ_CALL_LOG_REQUEST -> arrayOf(Manifest.permission.READ_CALL_LOG)
            WRITE_CALL_LOG_REQUEST -> arrayOf(Manifest.permission.WRITE_CALL_LOG)
            PROCESS_OUTGOING_CALLS_REQUEST -> arrayOf(Manifest.permission.PROCESS_OUTGOING_CALLS)
            READ_CONTACTS_REQUEST -> arrayOf(Manifest.permission.READ_CONTACTS)
            WRITE_CONTACTS_REQUEST -> arrayOf(Manifest.permission.WRITE_CONTACTS)
            GET_ACCOUNTS_REQUEST -> arrayOf(Manifest.permission.GET_ACCOUNTS)
            RECORD_AUDIO_REQUEST -> arrayOf(Manifest.permission.RECORD_AUDIO)
            READ_PHONE_NUMBERS_REQUEST -> arrayOf(Manifest.permission.READ_PHONE_NUMBERS)
            CALL_PHONE_REQUEST -> arrayOf(Manifest.permission.CALL_PHONE)
            ANSWER_PHONE_CALLS_REQUEST -> arrayOf(Manifest.permission.ANSWER_PHONE_CALLS)
            ADD_VOICEMAIL_REQUEST -> arrayOf(Manifest.permission.ADD_VOICEMAIL)
            USE_SIP_REQUEST -> arrayOf(Manifest.permission.USE_SIP)
            BODY_SENSORS_REQUEST -> arrayOf(Manifest.permission.BODY_SENSORS)
            SEND_SMS_REQUEST -> arrayOf(Manifest.permission.SEND_SMS)
            RECEIVE_SMS_REQUEST -> arrayOf(Manifest.permission.RECEIVE_SMS)
            READ_SMS_REQUEST -> arrayOf(Manifest.permission.READ_SMS)
            RECEIVE_WAP_PUSH_REQUEST -> arrayOf(Manifest.permission.RECEIVE_WAP_PUSH)
            RECEIVE_MMS_REQUEST -> arrayOf(Manifest.permission.RECEIVE_MMS)
            else -> emptyArray()
        }
    }

    private fun checker(permission: Int, context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            numToPermission(permission)[0]
        ) != PackageManager.PERMISSION_GRANTED
    }


    fun checkPermission(permission: Int, activity: AppCompatActivity?, callback: () -> Unit) {
        if(activity == null) return
        if (checker(permission, activity)) {
            permissionCallbacks[permission] = callback
            ActivityCompat.requestPermissions(activity, numToPermission(permission), permission)
        } else {
            callback.invoke()
        }

    }

    fun onPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionCallbacks[requestCode]?.invoke()
        }
    }
}