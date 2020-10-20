package com.android_dev_challenge.shoppingapp.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android_dev_challenge.shoppingapp.R
import com.android_dev_challenge.shoppingapp.ui.viewmodel.ProductActivityViewModel
import com.android_dev_challenge.shoppingapp.ui.adapter.ProductAdapter
import com.android_dev_challenge.shoppingapp.ui.dto.ProductDto
import org.koin.android.viewmodel.ext.android.viewModel


class ProductActivity : AppCompatActivity() {

    private val productActivityViewModel: ProductActivityViewModel by viewModel()

    private val errorTextView: TextView by lazy { findViewById(R.id.errorTextView) }
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.productRecyclerView) }
    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById(
            R.id.contentLoadingProgressBar
        )
    }

    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        if(savedInstanceState == null){
            showProgressBar()
        }
        observeProductList()
    }

    private fun observeProductList() {
        val productLivedata: LiveData<ProductDto.Result<ProductDto>> = productActivityViewModel.getProductLiveData()
        productLivedata.removeObservers(this@ProductActivity)
        productLivedata.observe(
            this@ProductActivity,
            { productDtoResult: ProductDto.Result<ProductDto> ->
                when(productDtoResult){
                    is ProductDto.Result.Success -> {
                        val productList = productDtoResult.data
                        if (productList.isNullOrEmpty())
                            showErrorView(getString(R.string.generic_error_msg))
                        else {
                            showProductList()
                            setAdapter(productList)
                        }
                    }
                    is ProductDto.Result.Error -> {
                        showErrorView(productDtoResult.errorHolder.message)
                    }
                }
            })
    }

    private fun setAdapter(productList: List<ProductDto>) {
        recyclerView.layoutManager = GridLayoutManager(this@ProductActivity, 2)
        productAdapter = ProductAdapter ()
        recyclerView.adapter = productAdapter
        productAdapter.productList = productList
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        progressBar.show()
    }

    private fun hideProgressBar() {
        progressBar.hide()
        progressBar.visibility = View.GONE

    }

    private fun showErrorView(errorMessage: String) {
        hideProgressBar()
        errorTextView.visibility = View.VISIBLE
        errorTextView.text = errorMessage
        recyclerView.visibility = View.GONE
    }

    private fun showProductList() {
        hideProgressBar()
        errorTextView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }
}