package com.couplace.gofer.model

import android.graphics.drawable.Drawable

data class Product(val name: String, val description: String, val image: Drawable){

    override fun toString(): String {
        return "Products(name='$name', description='$description', url='$image')"
    }
}





