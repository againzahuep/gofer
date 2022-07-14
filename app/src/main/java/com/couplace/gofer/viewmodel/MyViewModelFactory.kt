package com.velmurugan.mvvmwithkotlincoroutinesandretrofit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.couplace.gofer.product.ProductViewModel
import com.couplace.gofer.repository.DataRepository
import com.couplace.gofer.repository.DataRepositoryImpl

class MyViewModelFactory constructor(private val repository: DataRepositoryImpl): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            ProductViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}