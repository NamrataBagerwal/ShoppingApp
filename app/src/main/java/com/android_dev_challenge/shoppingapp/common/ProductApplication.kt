package com.android_dev_challenge.shoppingapp.common

import android.app.Application
import com.android_dev_challenge.shoppingapp.di.DependencyInjectionModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class ProductApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // get list of all modules
        val diModuleList = listOf(
            DependencyInjectionModule.retrofitModule,
            DependencyInjectionModule.errorHandlerModule,
            DependencyInjectionModule.productModule,
            DependencyInjectionModule.viewModelModule
        )
        // start koin with the module list
        startKoin {
            // Android context
            androidContext(this@ProductApplication)
            // modules
            modules(diModuleList)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}