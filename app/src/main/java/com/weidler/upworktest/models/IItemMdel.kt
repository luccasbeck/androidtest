package com.weidlersoftware.upworktest.models


import android.view.View


interface IItemMdel {
    fun getItemName(): String
    fun fillSubItems(view: View, dragListener: View.OnDragListener)

}