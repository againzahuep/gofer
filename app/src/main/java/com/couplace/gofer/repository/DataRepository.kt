package com.couplace.gofer.repository

import androidx.lifecycle.MutableLiveData
import com.couplace.gofer.model.Product

interface DataRepository {
    fun add(value: Product)
    fun addAll(value: List<Product>)
    fun get(): List<Product>
    fun createProductsList(numProducts: Int, offset: Int): List<Product>
}