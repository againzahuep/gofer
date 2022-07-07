package com.couplace.gofer.product


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.couplace.gofer.model.Product
import com.couplace.gofer.repository.DataRepository
import javax.inject.Inject

class ProductViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {

    var loading = MutableLiveData<Boolean>()
    val products = MutableLiveData<List<Product>>()
    val errorMessage = MutableLiveData<String>()

    fun removeData(index: Int) {
        try {
            repository.remove(index)
            products.postValue(repository.get())
        } catch (e: Exception) {
            onError("Error : ${e.message ?: "Unspecified Error"} ")
        }
    }

    fun fetchData(): MutableLiveData<List<Product>> {
        loading.value = false
        return products
    }

    fun addData(value: Product) {
        repository.add(value)
        products.postValue(repository.get())
    }

    fun addAllData(value: List<Product>) {

        repository.addAll(value)
        products.postValue(repository.get())
    }

    fun updateDataPosition(from: Int, to: Int) {
        val size = repository.get().size
        if (from in 0..size && to in 0..size) {
            repository.move(from, to)
            products.postValue(repository.get())
        } else {
            onError("Error index")
        }
    }

    fun updateProductsList(numProducts: Int, offset: Int) {
        val allProducts: List<Product> = repository.createProductsList(numProducts, offset)
        val size = repository.get().size
        try {
            if(offset * numProducts < size){
                products.postValue(allProducts)
            }
        } catch (e: Exception) {
            onError("Error : ${e.message} ")
        }
    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

}