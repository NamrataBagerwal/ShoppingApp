package com.android_dev_challenge.shoppingapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_dev_challenge.shoppingapp.ui.dto.ProductDto
import com.android_dev_challenge.shoppingapp.usecase.ProductUsecase
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class ProductActivityViewModel(private val productUsecase: ProductUsecase) : ViewModel() {

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + viewModelScope.coroutineContext + Dispatchers.IO

    private val productLiveData: MutableLiveData<ProductDto.Result<ProductDto>> by lazy {
        MutableLiveData<ProductDto.Result<ProductDto>>().also {
            updateProductLiveData()
        }
    }

    fun getProductLiveData(): LiveData<ProductDto.Result<ProductDto>> {
        return productLiveData
    }

    private fun updateProductLiveData() {
        viewModelScope.launch(context = coroutineContext) {
            productLiveData.postValue(productUsecase.getProducts())
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (coroutineContext.isActive) {
            coroutineContext.cancelChildren()
        }
        if (parentJob.isActive) {
            parentJob.cancel()
        }
    }
}

