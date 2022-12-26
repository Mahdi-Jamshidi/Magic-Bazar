package ir.magiccodes.magicbazar.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.magiccodes.magicbazar.model.data.Product

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase:RoomDatabase() {
    abstract fun productDao() : ProductDao
}