package com.android_dev_challenge.shoppingapp.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android_dev_challenge.shoppingapp.common.AppConstants
import com.android_dev_challenge.shoppingapp.common.ErrorHolder
import com.android_dev_challenge.shoppingapp.common.util.TestCoroutineRule
import com.android_dev_challenge.shoppingapp.common.util.TestData
import com.android_dev_challenge.shoppingapp.di.DependencyInjectionModule
import com.android_dev_challenge.shoppingapp.remote_repository.BaseRemoteRepository
import com.android_dev_challenge.shoppingapp.remote_repository.webservice.products.ProductRepositoryImpl
import com.android_dev_challenge.shoppingapp.remote_repository.webservice.products.entity.ProductApiResponse
import com.android_dev_challenge.shoppingapp.ui.dto.ProductDto
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ProductUsecaseTest : KoinTest {

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

    private val productUsecase: ProductUsecase by inject()

    private lateinit var productRepository: BaseRemoteRepository

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
        productRepository = declareMock<ProductRepositoryImpl>()
    }

    @Test
    fun `Verify ProductUsecase getProducts called from ProductActivityViewModel calls ProductRepositoryImpl getProducts`() {

        Assert.assertNotNull(
            "Verify ProductUsecase getProducts called from ProductActivityViewModel calls ProductRepositoryImpl getProducts",
            productRepository as ProductRepositoryImpl
        )

        testCoroutineRule.runBlockingTest {
            val mockProductApiResponse: BaseRemoteRepository.Result.Success<ProductApiResponse> =
                Mockito.mock(BaseRemoteRepository.Result.Success::class.java as Class<BaseRemoteRepository.Result.Success<ProductApiResponse>>)
            val testProductApiResponse = TestData.getTestProductApiResponse()
            Mockito.doReturn(testProductApiResponse)
                .`when`(mockProductApiResponse)
                .data
            Mockito.doReturn(mockProductApiResponse)
                .`when`(productRepository as ProductRepositoryImpl)
                .getProducts()

            productUsecase.getProducts()

            verify(productRepository as ProductRepositoryImpl).getProducts()

            val result: BaseRemoteRepository.Result<ProductApiResponse> =
                (productRepository as ProductRepositoryImpl).getProducts()
            Assert.assertEquals(mockProductApiResponse, result)
        }
    }

    @Test
    fun `Verify ProductUsecase getProducts returns ProductDto Success Result for ProductRepositoryImpl getProducts Success Result`() {

        testCoroutineRule.runBlockingTest {
            val mockProductApiResponse: BaseRemoteRepository.Result.Success<ProductApiResponse> =
                Mockito.mock(BaseRemoteRepository.Result.Success::class.java as Class<BaseRemoteRepository.Result.Success<ProductApiResponse>>)

            val testProductApiResponse = TestData.getTestProductApiResponse()
            Mockito.doReturn(testProductApiResponse)
                .`when`(mockProductApiResponse)
                .data
            Mockito.doReturn(mockProductApiResponse)
                .`when`(productRepository as ProductRepositoryImpl)
                .getProducts()

            val productRepositoryImplResult: BaseRemoteRepository.Result<ProductApiResponse> =
                (productRepository as ProductRepositoryImpl).getProducts()
            Assert.assertThat(
                productRepositoryImplResult,
                Matchers.instanceOf(BaseRemoteRepository.Result.Success::class.java as Class<BaseRemoteRepository.Result.Success<ProductApiResponse>>)
            )
            Assert.assertEquals(
                testProductApiResponse,
                (productRepositoryImplResult as BaseRemoteRepository.Result.Success<ProductApiResponse>).data
            )
            val productUseCaseResult: ProductDto.Result<ProductDto> = productUsecase.getProducts()
            Assert.assertThat(
                productUseCaseResult,
                Matchers.instanceOf(ProductDto.Result.Success::class.java as Class<ProductDto.Result.Success<ProductDto>>)
            )
            val productDtoList: List<ProductDto> =
                (productUseCaseResult as ProductDto.Result.Success<ProductDto>).data
            val productApiResponseList: List<ProductApiResponse.Result> =
                testProductApiResponse.results
            Assert.assertEquals(productDtoList.size, productApiResponseList.size)
            val productDto = productDtoList[0]
            val productApiResponse = productApiResponseList[0]
            Assert.assertEquals(productApiResponse.name, productDto.productName)
            Assert.assertEquals(productApiResponse.designer.name, productDto.designerName)
            Assert.assertEquals(productApiResponse.price.value, productDto.price)
            Assert.assertEquals(productApiResponse.price.currencyIso, productDto.currencyCode)
            Assert.assertEquals(productApiResponse.primaryImageMap.medium.url, productDto.imageUrl)
            Assert.assertEquals(
                productApiResponse.primaryImageMap.medium.altText,
                productDto.imageAltText
            )
        }
    }

    @Test
    fun `Verify ProductUsecase getProducts returns ProductDto Error Result for ProductRepositoryImpl getProducts Error Result`() {

        testCoroutineRule.runBlockingTest {
            val mockProductApiResponse: BaseRemoteRepository.Result.Error =
                Mockito.mock(BaseRemoteRepository.Result.Error::class.java)

            val errorHolder: ErrorHolder = Mockito.mock(ErrorHolder::class.java)
            given(errorHolder.message).willReturn(AppConstants.ERROR_MSG_OOPS_SOMETHING_WENT_WRONG)
            given(mockProductApiResponse.errorHolder).willReturn(errorHolder)

            Mockito.doReturn(mockProductApiResponse)
                .`when`(productRepository as ProductRepositoryImpl)
                .getProducts()

            val productRepositoryImplResult: BaseRemoteRepository.Result<ProductApiResponse> =
                (productRepository as ProductRepositoryImpl).getProducts()
            Assert.assertThat(
                productRepositoryImplResult,
                Matchers.instanceOf(BaseRemoteRepository.Result.Error::class.java)
            )
            Assert.assertEquals(
                errorHolder,
                (productRepositoryImplResult as BaseRemoteRepository.Result.Error).errorHolder
            )
            Assert.assertEquals(
                errorHolder.message,
                productRepositoryImplResult.errorHolder.message
            )
            val productUseCaseResult: ProductDto.Result<ProductDto> = productUsecase.getProducts()
            Assert.assertThat(
                productUseCaseResult,
                Matchers.instanceOf(ProductDto.Result.Error::class.java)
            )
            Assert.assertEquals(
                productRepositoryImplResult.errorHolder,
                (productUseCaseResult as ProductDto.Result.Error).errorHolder
            )
            Assert.assertEquals(
                productRepositoryImplResult.errorHolder.message,
                productUseCaseResult.errorHolder.message
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}