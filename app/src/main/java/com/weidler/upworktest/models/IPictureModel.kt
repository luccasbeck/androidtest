package com.weidlersoftware.upworktest.models


import android.view.View

interface IPictureModel {
    fun getViewId(): Int
    fun fillItem(v: View)
    fun collSpan(): Int
}