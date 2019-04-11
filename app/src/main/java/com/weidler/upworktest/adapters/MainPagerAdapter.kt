package com.weidlersoftware.upworktest.adapters

import android.content.Context
import android.content.res.Resources
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.weidlersoftware.upworktest.R
import com.weidlersoftware.upworktest.fragments.AlbumsFragment
import com.weidlersoftware.upworktest.fragments.PicturesFragment

class MainPagerAdapter(fm: FragmentManager?, context: Context) : FragmentPagerAdapter(fm) {
    private val resources: Resources = context.resources


    override fun getItem(postion: Int): Fragment {
        return when(postion){
            0 -> PicturesFragment()
            else -> AlbumsFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return resources.getString(when(position){
            0 -> R.string.pictures
            else -> R.string.albums
        })
    }
    override fun getCount() = 2
}