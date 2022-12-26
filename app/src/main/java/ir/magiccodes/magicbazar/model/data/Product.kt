package ir.magiccodes.magicbazar.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

data class ProductResponse(
    val success: Boolean,
    val products: List<Product>
)

@Entity("product_table")
data class Product(

    @PrimaryKey
    val productId: String,

    val name: String,
    val imgUrl: String,
    val detailText: String,
    val price: String,
    val soldItem: String,
    val category: String,
    val material: String,
    val tags: String,
    val quantity: String?

)