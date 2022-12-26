package ir.magiccodes.magicbazar.model.repository.cart

import ir.magiccodes.magicbazar.model.data.CheckOut
import ir.magiccodes.magicbazar.model.data.SubmitOrder
import ir.magiccodes.magicbazar.model.data.UserCartInfo

interface CartRepository {

    suspend fun addToCart(productId: String): Boolean
    suspend fun removeFromCart(productId: String): Boolean
    suspend fun getCartSize(): Int
    suspend fun getUserCartInfo(): UserCartInfo
    suspend fun submitOrder(address: String, postalCode: String): SubmitOrder
    suspend fun checkOut(orderId: String): CheckOut

    fun setOrderId(orderId: String)
    fun getOderId(): String

    fun setPurchaseStatus(status: Int)
    fun getPurchaseStatus():Int
}