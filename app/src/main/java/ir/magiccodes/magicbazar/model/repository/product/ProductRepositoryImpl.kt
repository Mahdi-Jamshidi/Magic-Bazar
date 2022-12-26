package ir.magiccodes.magicbazar.model.repository.product

import ir.magiccodes.magicbazar.model.data.Ads
import ir.magiccodes.magicbazar.model.data.Product
import ir.magiccodes.magicbazar.model.database.ProductDao
import ir.magiccodes.magicbazar.model.net.ApiService

class ProductRepositoryImpl(
    private val apiService: ApiService,
    private val productDao: ProductDao
): ProductRepository {
    override suspend fun getAllProducts(isInternetConnected: Boolean): List<Product> {

        if (isInternetConnected){
            // get data from data
            val dataFromServer = apiService.getAllProducts()
            if (dataFromServer.success){
                productDao.insertOrUpdate(dataFromServer.products)
                return dataFromServer.products
            }
        } else {
            // get data from local
            return productDao.getAll()
        }
        return listOf()
    }

    override suspend fun getAllAds(isInternetConnected: Boolean): List<Ads> {

        if (isInternetConnected){
            val dataFromServer = apiService.getAllAds()
            if (dataFromServer.success){
                return dataFromServer.ads
            }
        }
        return listOf()
    }

    override suspend fun getAllProductsByCategory(category: String): List<Product> {
        return productDao.getAllByCategory(category)
    }

    override suspend fun getProductById(productId: String): Product {
         return productDao.getProductById(productId)
    }
}