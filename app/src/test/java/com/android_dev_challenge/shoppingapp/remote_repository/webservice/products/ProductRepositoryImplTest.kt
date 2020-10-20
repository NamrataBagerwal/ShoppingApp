package com.android_dev_challenge.shoppingapp.remote_repository.webservice.products

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android_dev_challenge.shoppingapp.common.util.TestCoroutineRule
import com.android_dev_challenge.shoppingapp.di.DependencyInjectionModule
import com.android_dev_challenge.shoppingapp.remote_repository.ErrorHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ProductRepositoryImplTest : KoinTest {

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mock(clazz.java)
    }

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private val productRepositoryImpl: ProductRepositoryImpl by inject()

    private lateinit var productApi: ProductApi
    private lateinit var errorHandler: ErrorHandler

    @Before
    fun setUp() {
        startKoin {
            modules(
                listOf(
                    DependencyInjectionModule.retrofitModule,
                    DependencyInjectionModule.errorHandlerModule,
                    DependencyInjectionModule.productModule,
                    DependencyInjectionModule.viewModelModule
                )
            )
        }
        productApi = declareMock()
        errorHandler = declareMock()
    }

    @Test
    fun `assert productApi and errorHandler not null`() {
        assertNotNull(productApi)
        assertNotNull(errorHandler)
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}