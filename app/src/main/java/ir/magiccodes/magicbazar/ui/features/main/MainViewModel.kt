package ir.magiccodes.magicbazar.ui.features.main

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.magiccodes.magicbazar.model.data.Ads
import ir.magiccodes.magicbazar.model.data.CheckOut
import ir.magiccodes.magicbazar.model.data.Product
import ir.magiccodes.magicbazar.model.repository.cart.CartRepository
import ir.magiccodes.magicbazar.model.repository.product.ProductRepository
import ir.magiccodes.magicbazar.util.coroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    isInternetConnected: Boolean
): ViewModel() {

    val dataProduct = mutableStateOf<List<Product>>(listOf())
    val dataAds = mutableStateOf<List<Ads>>(listOf())
    val showProgressBar = mutableStateOf(false)
    val badgeNumber = mutableStateOf(0)

    val showPaymentResultDialog = mutableStateOf(false)
    val checkoutData = mutableStateOf(CheckOut(null, null))

    init {
        refreshAllDataFromNet(isInternetConnected)
    }

    fun getCheckOutData(){

        viewModelScope.launch(coroutineExceptionHandler) {

            val result = cartRepository.checkOut(cartRepository.getOderId())

            if (result.success!!){
                checkoutData.value = result
                showPaymentResultDialog.value = true
            }
        }
    }

    fun getPaymentStatus():Int{
        return  cartRepository.getPurchaseStatus()
    }

    fun setPaymentStatus(status: Int) {
        cartRepository.setPurchaseStatus(status)
    }

    private fun refreshAllDataFromNet(isInternetConnected: Boolean){

        viewModelScope.launch(coroutineExceptionHandler) {
            if (isInternetConnected)
                showProgressBar.value = true

            val newDataProduct = async { productRepository.getAllProducts(isInternetConnected) }
            val newDataAds = async { productRepository.getAllAds(isInternetConnected) }

            updateData(newDataProduct.await() , newDataAds.await())

            showProgressBar.value = false
        }
    }

    private fun updateData(product: List<Product> , ads: List<Ads>){
        dataProduct.value = product
        dataAds.value = ads
    }

    fun loadBadgeNumber() {
        viewModelScope.launch(coroutineExceptionHandler) {
            badgeNumber.value = cartRepository.getCartSize()
        }
    }
}