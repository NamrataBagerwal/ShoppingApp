package com.android_dev_challenge.shoppingapp.ui.activity

import android.os.Build
import android.view.View
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android_dev_challenge.shoppingapp.R
import com.android_dev_challenge.shoppingapp.common.AppConstants
import com.android_dev_challenge.shoppingapp.common.ErrorHolder
import com.android_dev_challenge.shoppingapp.common.util.TestData
import com.android_dev_challenge.shoppingapp.di.DependencyInjectionModule
import com.android_dev_challenge.shoppingapp.ui.adapter.ProductAdapter
import com.android_dev_challenge.shoppingapp.ui.dto.ProductDto
import com.android_dev_challenge.shoppingapp.ui.viewmodel.ProductActivityViewModel
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.verify
import kotlinx.android.synthetic.main.activity_product.*
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ProductActivityTest : KoinTest {

    companion object {
        private const val IS_NOT_VISIBLE = "is not visible"
        private const val IS_VISIBLE = "is visible"
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var viewModel: ProductActivityViewModel

    private lateinit var activityScenario: ActivityScenario<ProductActivity>

    @Mock
    private lateinit var mockProductLiveData: LiveData<ProductDto.Result<ProductDto>>

    @Captor
    private lateinit var productObserverCaptor: ArgumentCaptor<Observer<ProductDto.Result<ProductDto>>>

    @Before
    fun setUp() {
        koinApplication {
            modules(
                listOf(
                    DependencyInjectionModule.retrofitModule,
                    DependencyInjectionModule.errorHandlerModule,
                    DependencyInjectionModule.productModule,
                    DependencyInjectionModule.viewModelModule
                )
            )
        }

        viewModel = declareMock {
            given(this.getProductLiveData()).willReturn(mockProductLiveData)
        }

        activityScenario = launchActivity()
        activityScenario.moveToState(Lifecycle.State.CREATED)

        verify(mockProductLiveData)
            .observe(
                ArgumentMatchers.any(LifecycleOwner::class.java),
                productObserverCaptor.capture()
            )
    }

    @After
    fun tearDown() {
        activityScenario.close()
        stopKoin()
    }

    @Test
    fun onCreate() {
        `verify action bar title`()
        `has visible progress loading view on create`()
        `has error view hidden on create`()
        `has recycler view hidden on create`()
        `observe product list is called on create`()
        `verify result of observer onchange method for success result with non empty list`()
        `verify result of observer onchange method for success result with empty list`()
        `verify result of observer onchange method for error result`()
    }

    private fun `verify action bar title`() {
        activityScenario.onActivity { activity ->
            Assert.assertEquals(
                "Action Bar title is as expected",
                activity.getString(R.string.app_name),
                activity.appBarLayoutTitle.text
            )
        }
    }

    private fun `has visible progress loading view on create`() {

        activityScenario.onActivity { activity ->
            Assert.assertEquals(
                "Progress Loading View $IS_VISIBLE",
                View.VISIBLE,
                activity.contentLoadingProgressBar.visibility
            )
        }
    }

    private fun `has error view hidden on create`() {
        activityScenario.onActivity { activity ->
            Assert.assertEquals(
                "Error View $IS_NOT_VISIBLE",
                View.GONE,
                activity.errorTextView.visibility
            )
        }
    }

    private fun `has recycler view hidden on create`() {
        activityScenario.onActivity { activity ->
            Assert.assertEquals(
                "Recycler View $IS_NOT_VISIBLE",
                View.GONE,
                activity.productRecyclerView.visibility
            )
        }
    }

    private fun `observe product list is called on create`() {

        activityScenario.onActivity { activity ->
            verify(viewModel).getProductLiveData()

            val activityCaptor = ArgumentCaptor.forClass(LifecycleOwner::class.java)
            val viewModelProductLiveData: LiveData<ProductDto.Result<ProductDto>> =
                viewModel.getProductLiveData()
            verify(viewModelProductLiveData).removeObservers(activityCaptor.capture())
            Assert.assertEquals(
                "LifecycleOwner Captured is as expected",
                activity,
                activityCaptor.value
            )
            verify(viewModelProductLiveData).observe(
                activityCaptor.capture(),
                productObserverCaptor.capture()
            )
            Assert.assertEquals(
                "LifecycleOwner Captured is as expected",
                activity,
                activityCaptor.value
            )
            Assert.assertNotNull("Product Observer Captor is not null", productObserverCaptor.value)
        }
    }

    private fun `verify result of observer onchange method for success result with non empty list`() {

        val mockProductDto: ProductDto.Result.Success<ProductDto> =
            Mockito.mock(ProductDto.Result.Success::class.java as Class<ProductDto.Result.Success<ProductDto>>)

        val testProductDtoData = TestData.getTestProductDtoData()
        given(mockProductDto.data).willReturn(testProductDtoData)
        //Updating the observer with Success Mock Response
        productObserverCaptor.value.onChanged(mockProductDto)
        activityScenario.onActivity { activity ->
//            showProductList() will be called
            Assert.assertEquals(
                "Progress Loading View $IS_NOT_VISIBLE",
                View.GONE,
                activity.contentLoadingProgressBar.visibility
            )
            Assert.assertEquals(
                "Error View $IS_NOT_VISIBLE",
                View.GONE,
                activity.errorTextView.visibility
            )
            Assert.assertEquals(
                "Recycler View $IS_VISIBLE",
                View.VISIBLE,
                activity.productRecyclerView.visibility
            )
            // setAdapter(productList) will be called
            Assert.assertThat(
                activity.productRecyclerView.layoutManager,
                Matchers.instanceOf(GridLayoutManager::class.java)
            )
            Assert.assertNotNull(activity.productRecyclerView.adapter)
            Assert.assertThat(
                activity.productRecyclerView.adapter,
                Matchers.instanceOf(ProductAdapter::class.java)
            )
            val adapter: ProductAdapter = activity.productRecyclerView.adapter as ProductAdapter
            Assert.assertEquals(adapter.productList, testProductDtoData)
        }
    }

    private fun `verify result of observer onchange method for success result with empty list`() {

        val mockProductDto: ProductDto.Result.Success<ProductDto> =
            Mockito.mock(ProductDto.Result.Success::class.java as Class<ProductDto.Result.Success<ProductDto>>)

        given(mockProductDto.data).willReturn(emptyList())
        //Updating the observer with Success Mock Response
        productObserverCaptor.value.onChanged(mockProductDto)
        activityScenario.onActivity { activity ->
            //showErrorView() will be called
            Assert.assertEquals(
                "Progress Loading View $IS_NOT_VISIBLE",
                View.GONE,
                activity.contentLoadingProgressBar.visibility
            )
            Assert.assertEquals("Error Text View Message is as expected", activity.getString(R.string.generic_error_msg), activity.errorTextView.text)
            Assert.assertEquals(
                "Error View $IS_VISIBLE",
                View.VISIBLE,
                activity.errorTextView.visibility
            )
            Assert.assertEquals(
                "Recycler View $IS_NOT_VISIBLE",
                View.GONE,
                activity.productRecyclerView.visibility
            )
        }
    }

    private fun `verify result of observer onchange method for error result`() {

        val mockProductDto: ProductDto.Result.Error =
            Mockito.mock(ProductDto.Result.Error::class.java)

        val errorHolder: ErrorHolder = Mockito.mock(ErrorHolder::class.java)
        given(errorHolder.message).willReturn(AppConstants.ERROR_MSG_OOPS_SOMETHING_WENT_WRONG)
        given(mockProductDto.errorHolder).willReturn(errorHolder)
        //Updating the observer with Error Mock Response
        productObserverCaptor.value.onChanged(mockProductDto)
        activityScenario.onActivity { activity ->
            //showErrorView() will be called
            Assert.assertEquals(
                "Progress Loading View $IS_NOT_VISIBLE",
                View.GONE,
                activity.contentLoadingProgressBar.visibility
            )
            Assert.assertEquals("Error Text View Message is as expected", AppConstants.ERROR_MSG_OOPS_SOMETHING_WENT_WRONG, activity.errorTextView.text)
            Assert.assertEquals(
                "Error View $IS_VISIBLE",
                View.VISIBLE,
                activity.errorTextView.visibility
            )
            Assert.assertEquals(
                "Recycler View $IS_NOT_VISIBLE",
                View.GONE,
                activity.productRecyclerView.visibility
            )
        }
    }
}