package ir.magiccodes.magicbazar.ui

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import dev.burnoo.cokoin.navigation.KoinNavHost
import ir.magiccodes.magicbazar.di.myModules
import ir.magiccodes.magicbazar.model.repository.TokenInMemory
import ir.magiccodes.magicbazar.model.repository.user.UserRepository
import ir.magiccodes.magicbazar.ui.features.IntroScreen
import ir.magiccodes.magicbazar.ui.features.cart.CartScreen
import ir.magiccodes.magicbazar.ui.features.category.CategoryScreen
import ir.magiccodes.magicbazar.ui.features.main.MainScreen
import ir.magiccodes.magicbazar.ui.features.product.ProductScreen
import ir.magiccodes.magicbazar.ui.features.profile.ProfileScreen
import ir.magiccodes.magicbazar.ui.features.signIn.SingInScreen
import ir.magiccodes.magicbazar.ui.features.signUp.SingUpScreen
import ir.magiccodes.magicbazar.ui.theme.BackgroundMain
import ir.magiccodes.magicbazar.ui.theme.MainAppTheme
import ir.magiccodes.magicbazar.util.KEY_CATEGORY_ARG
import ir.magiccodes.magicbazar.util.KEY_PRODUCT_ARG
import ir.magiccodes.magicbazar.util.MyScreens
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        setContent {

            Koin(appDeclaration = {
                androidContext(this@MainActivity)
                modules(myModules) }) {

                MainAppTheme {
                    Surface(color = BackgroundMain, modifier = Modifier.fillMaxSize()
                    ) {
                        val userRepository : UserRepository = get()
                        userRepository.loadToken()
                        MagicBazarUi()
                    }
                }
            }
        }
    }
}

@Composable
fun MagicBazarUi() {
    val navController = rememberNavController()
    KoinNavHost(
        navController = navController,
        startDestination = MyScreens.MainScreen.route
    ) {
        composable(MyScreens.MainScreen.route) {

           if (TokenInMemory.token != null){
               MainScreen()
           } else {
               IntroScreen()
           }
        }

        composable(
            route = MyScreens.ProductScreen.route + "/" + "{$KEY_PRODUCT_ARG}",
            arguments = listOf(navArgument(KEY_PRODUCT_ARG) {
                type = NavType.StringType
            })
        ) {
            ProductScreen(it.arguments!!.getString(KEY_PRODUCT_ARG, "null"))
        }

        composable(
            route = MyScreens.CategoryScreen.route + "/" + "{$KEY_CATEGORY_ARG}",
            arguments = listOf(navArgument(KEY_CATEGORY_ARG) {
                type = NavType.StringType
            })
        ) {
            CategoryScreen(it.arguments!!.getString(KEY_CATEGORY_ARG, "null"))
        }

        composable(MyScreens.ProfileScreen.route) {
            ProfileScreen()
        }

        composable(MyScreens.CartScreen.route) {
            CartScreen()
        }

        composable(MyScreens.SignInScreen.route) {
            SingInScreen()
        }

        composable(MyScreens.SignUpScreen.route) {
            SingUpScreen()
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainAppTheme {
        MainAppTheme {
            Surface(
                color = BackgroundMain,
                modifier = Modifier.fillMaxSize()
            ) {
                MagicBazarUi()
            }
        }
    }
}