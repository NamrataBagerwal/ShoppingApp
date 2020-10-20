package com.android_dev_challenge.shoppingapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.android_dev_challenge.shoppingapp.common.util.TestCoroutineRule
import com.android_dev_challenge.shoppingapp.di.DependencyInjectionModule
import com.android_dev_challenge.shoppingapp.ui.dto.ProductDto
import com.android_dev_challenge.shoppingapp.usecase.ProductUsecase
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ProductActivityViewModelTest: KoinTest {

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private val viewModel: ProductActivityViewModel by inject()

    private lateinit var productUsecase: ProductUsecase

    @Mock
    private lateinit var mockProductObserver: Observer<ProductDto.Result<ProductDto>>

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

        productUsecase = declareMock()
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun getProductLiveData() {
        testCoroutineRule.runBlockingTest {
            val mockProductDto: ProductDto.Result<ProductDto> =
                Mockito.mock(ProductDto.Result::class.java as Class<ProductDto.Result<ProductDto>>)

            Mockito.doReturn(mockProductDto)
                .`when`(productUsecase)
                .getProducts()

            viewModel.getProductLiveData().observeForever(mockProductObserver)
            verify(productUsecase).getProducts()

            val argumentCaptor = ArgumentCaptor.forClass(ProductDto.Result::class.java as Class<ProductDto.Result<ProductDto>>)
            verify(mockProductObserver).onChanged(argumentCaptor.capture())
            Assert.assertEquals(mockProductDto, argumentCaptor.value)

            viewModel.getProductLiveData().removeObserver(mockProductObserver)
        }

    }
}