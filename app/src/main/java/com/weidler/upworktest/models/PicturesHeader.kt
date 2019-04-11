package com.weidlersoftware.upworktest.models

import android.view.View
import android.widget.TextView

import com.weidlersoftware.upworktest.R

class PicturesHeader(date: String): IPictureModel {
    override fun collSpan(): Int {
        return 6
    }

    override fun fillItem(v: View) {
        v.findViewById<TextView>(R.id.tv_date).text = title
    }

    private var title: String = date

    override fun getViewId(): Int {
       return R.layout.pictures_header
    }

}