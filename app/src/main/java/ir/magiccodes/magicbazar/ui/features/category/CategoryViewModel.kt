package ir.magiccodes.magicbazar.ui.features.category

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.magiccodes.magicbazar.model.data.Ads
import ir.magiccodes.magicbazar.model.data.Product
import ir.magiccodes.magicbazar.model.repository.product.ProductRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val productRepository: ProductRepository
): ViewModel() {

    val dataProduct = mutableStateOf<List<Product>>(listOf())


    fun loadDataByCategory(category: String){

        viewModelScope.launch {
            val dataFromProduct = productRepository.getAllProductsByCategory(category)
            dataProduct.value = dataFromProduct

        }
    }

}