package ir.magiccodes.magicbazar.model.repository.product

import ir.magiccodes.magicbazar.model.data.Ads
import ir.magiccodes.magicbazar.model.data.Product

interface ProductRepository {

    suspend fun getAllProducts(isInternetConnected: Boolean): List<Product>
    suspend fun getAllAds(isInternetConnected: Boolean): List<Ads>
    suspend fun getAllProductsByCategory(category: String): List<Product>
    suspend fun getProductById(productId: String): Product
}