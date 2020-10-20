package com.android_dev_challenge.shoppingapp.di
import com.android_dev_challenge.shoppingapp.BuildConfig
import com.android_dev_challenge.shoppingapp.remote_repository.ErrorHandler
import com.android_dev_challenge.shoppingapp.remote_repository.networking_retrofit.RetrofitFactory
import com.android_dev_challenge.shoppingapp.remote_repository.networking_retrofit.interceptor.CacheInterceptor
import com.android_dev_challenge.shoppingapp.remote_repository.networking_retrofit.interceptor.OfflineCacheInterceptor
import com.android_dev_challenge.shoppingapp.remote_repository.webservice.products.ProductApi
import com.android_dev_challenge.shoppingapp.remote_repository.webservice.products.ProductRepositoryImpl
import com.android_dev_challenge.shoppingapp.ui.viewmodel.ProductActivityViewModel
import com.android_dev_challenge.shoppingapp.usecase.ProductUsecase
import org.koin.dsl.module
import org.koin.android.viewmodel.dsl.viewModel

object DependencyInjectionModule {

    val retrofitModule = module {
        single { CacheInterceptor() }
        single { OfflineCacheInterceptor() }
    }

    val errorHandlerModule = module {
        factory { ErrorHandler() }
    }

    val productModule = module {
        single {
            val productApi: ProductApi =
                RetrofitFactory.retrofit(BuildConfig.BASE_URL).create(ProductApi::class.java)
            return@single ProductRepositoryImpl(productApi, get())
        }
        single { ProductUsecase(get() as ProductRepositoryImpl) }
    }

    val viewModelModule = module {
        viewModel {
            ProductActivityViewModel(get())
        }
    }

}