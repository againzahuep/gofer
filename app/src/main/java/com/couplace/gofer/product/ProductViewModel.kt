package com.couplace.gofer.product


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.couplace.gofer.model.Product
import com.couplace.gofer.repository.DataRepositoryImpl
import javax.inject.Inject

class ProductViewModel @Inject constructor(private val dataRepositoryImpl: DataRepositoryImpl) : ViewModel() {

    var loading = MutableLiveData<Boolean>()
    val products = MutableLiveData<List<Product>>()
    val errorMessage = MutableLiveData<String>()

   fun fetchData(): MutableLiveData<List<Product>> {
        loading.value = false
        return products
    }

    fun addData(value: Product) {
        dataRepositoryImpl.add(value)
        products.postValue(dataRepositoryImpl.get())
    }

    fun addAllData(value: List<Product>) {
        dataRepositoryImpl.addAll(value)
        products.postValue(dataRepositoryImpl.get())
    }



    fun updateProductsList(numProducts: Int, offset: Int) {
        val allProducts: List<Product> = dataRepositoryImpl.createProductsList(numProducts, offset)
        val size = dataRepositoryImpl.get().size
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