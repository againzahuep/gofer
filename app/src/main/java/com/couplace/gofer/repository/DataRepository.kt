package com.couplace.gofer.repository

import com.couplace.gofer.model.Product

interface DataRepository {
    fun add(value: Product)
    fun addAll(value: List<Product>)
    fun remove(index: Int)
    fun get(): List<Product>
    fun move(from: Int, to: Int)
    fun createProductsList(numProducts: Int, offset: Int): List<Product>
}