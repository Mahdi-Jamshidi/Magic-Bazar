package ir.magiccodes.magicbazar.di

import android.content.Context
import androidx.room.Room
import ir.magiccodes.magicbazar.model.database.AppDatabase
import ir.magiccodes.magicbazar.model.net.createApiService
import ir.magiccodes.magicbazar.model.repository.cart.CartRepository
import ir.magiccodes.magicbazar.model.repository.cart.CartRepositoryImpl
import ir.magiccodes.magicbazar.model.repository.comment.CommentRepository
import ir.magiccodes.magicbazar.model.repository.comment.CommentRepositoryImpl
import ir.magiccodes.magicbazar.model.repository.product.ProductRepository
import ir.magiccodes.magicbazar.model.repository.product.ProductRepositoryImpl
import ir.magiccodes.magicbazar.model.repository.user.UserRepository
import ir.magiccodes.magicbazar.model.repository.user.UserRepositoryImpl
import ir.magiccodes.magicbazar.ui.features.cart.CartViewModel
import ir.magiccodes.magicbazar.ui.features.category.CategoryViewModel
import ir.magiccodes.magicbazar.ui.features.main.MainViewModel
import ir.magiccodes.magicbazar.ui.features.product.ProductViewModel
import ir.magiccodes.magicbazar.ui.features.profile.ProfileViewModel
import ir.magiccodes.magicbazar.ui.features.signIn.SignInViewModel
import ir.magiccodes.magicbazar.ui.features.signUp.SignUpViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val myModules = module {

    single { androidContext().getSharedPreferences("data", Context.MODE_PRIVATE) }
    single { createApiService() }

    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java ,"app_dataBase.db" ).build() }

    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<ProductRepository> { ProductRepositoryImpl(get(), get<AppDatabase>().productDao())}
    single<CommentRepository> { CommentRepositoryImpl(get()) }
    single<CartRepository> { CartRepositoryImpl(get(), get()) }

    viewModel { ProfileViewModel(get())}
    viewModel { ProductViewModel(get(), get(), get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { SignInViewModel(get()) }
    viewModel { (isNetConnected: Boolean) -> MainViewModel(get(), get(),isNetConnected) }
    viewModel { CategoryViewModel(get()) }
    viewModel { CartViewModel(get() , get()) }


}


