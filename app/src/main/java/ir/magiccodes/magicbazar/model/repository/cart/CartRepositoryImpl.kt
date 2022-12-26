package ir.magiccodes.magicbazar.model.repository.cart

import android.content.SharedPreferences
import com.google.gson.JsonObject
import ir.magiccodes.magicbazar.model.data.CheckOut
import ir.magiccodes.magicbazar.model.data.SubmitOrder
import ir.magiccodes.magicbazar.model.data.UserCartInfo
import ir.magiccodes.magicbazar.model.net.ApiService
import ir.magiccodes.magicbazar.util.NO_PAYMENT

class CartRepositoryImpl(
    private val apiService: ApiService,
    private val sharePref: SharedPreferences
) : CartRepository {

    override suspend fun addToCart(productId: String): Boolean {

        val jsonObject = JsonObject().apply {
            addProperty("productId", productId)
        }

        val result = apiService.addProductToCart(jsonObject)
        return result.success
    }

    override suspend fun removeFromCart(productId: String): Boolean {

        val jsonObject = JsonObject().apply {
            addProperty("productId", productId)
        }

        val result = apiService.removeFromCart(jsonObject)
        return result.success
    }

    override suspend fun getCartSize(): Int {

        val result = apiService.getUserCart()
        if (result.success) {

            var counter = 0
            result.productList.forEach {
                counter += (it.quantity ?: "0").toInt()
            }
            return counter
        }
        return 0
    }

    override suspend fun getUserCartInfo(): UserCartInfo {
        return apiService.getUserCart()
    }

    override suspend fun submitOrder(address: String, postalCode: String): SubmitOrder {
        val jsonObject = JsonObject().apply {
            addProperty("address", address)
            addProperty("postalCode", postalCode)
        }

        val result = apiService.submitOrder(jsonObject)
        setOrderId(result.orderId.toString())

        return result
    }

    override suspend fun checkOut(orderId: String): CheckOut {

        val jsonObject = JsonObject().apply {
            addProperty("orderId", orderId)
        }
        return apiService.checkOut(jsonObject)
    }

    override fun setOrderId(orderId: String) {
        sharePref.edit().putString("orderId", orderId).apply()
    }

    override fun getOderId(): String {
        return sharePref.getString("orderId", "0")!!
    }

    override fun setPurchaseStatus(status: Int) {
        sharePref.edit().putInt("purchase_status", status).apply()
    }

    override fun getPurchaseStatus(): Int {
        return sharePref.getInt("purchase_status", NO_PAYMENT)
    }


}