package ir.magiccodes.magicbazar.ui.features.cart

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.navigation.getNavViewModel
import ir.dunijet.dunibazaar.util.NetworkChecker
import ir.magiccodes.magicbazar.R
import ir.magiccodes.magicbazar.model.data.Product
import ir.magiccodes.magicbazar.ui.features.profile.AddUserLocationDataDialog
import ir.magiccodes.magicbazar.ui.theme.Blue
import ir.magiccodes.magicbazar.ui.theme.PriceBackground
import ir.magiccodes.magicbazar.ui.theme.Shapes
import ir.magiccodes.magicbazar.util.MyScreens
import ir.magiccodes.magicbazar.util.PAYMENT_PENDING
import ir.magiccodes.magicbazar.util.stylePrice


@Composable
fun CartScreen() {

    val context = LocalContext.current
    val getDataDialogState = remember { mutableStateOf(false) }
    val navigation = getNavController()
    val viewModel = getNavViewModel<CartViewModel>()
    viewModel.loadCartData()

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
        ) {

            CartToolBar(
                onBackClicked = { navigation.popBackStack() },
                onProfileClicked = { navigation.navigate(MyScreens.ProfileScreen.route) }
            )

            if (viewModel.productList.value.isNotEmpty()) {

                CartList(
                    data = viewModel.productList.value,
                    isChangingNumber = viewModel.isChangingNumber.value,
                    onAddItemClicked = { viewModel.addItem(it) },
                    onRemoveItemClicked = { viewModel.removeItem(it) },
                    onItemClicked = { navigation.navigate(MyScreens.ProductScreen.route + "/" + it) }
                )

            } else {

                NoDataAnimation()
            }
        }

        PurchaseAll(totalPrice = viewModel.totalPrice.value.toString()) {

            if (viewModel.productList.value.isNotEmpty()) {

                val locationData = viewModel.getUserLocation()
                if (locationData.first == "click to add" || locationData.second == "click to add"){
                    getDataDialogState.value = true
                } else {

                    //pardakht
                    viewModel.purchaseAll(locationData.first , locationData.second){ sucess , link->

                        if (sucess){

                            Toast.makeText(context, "pay using zarinPal..", Toast.LENGTH_SHORT).show()

                            viewModel.setPaymentStatus(PAYMENT_PENDING)

                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                            context.startActivity(intent)

                        } else {
                            Toast.makeText(context, "Problem in payment...", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            } else {
                Toast.makeText(context, "Please add some products first..", Toast.LENGTH_SHORT).show()
            }
        }

        if (getDataDialogState.value){
            
            AddUserLocationDataDialog(
                showSaveLocation = true,
                onDismiss = { getDataDialogState.value = false },
                onSubmitClicked ={ address , postalCode, isChacked ->

                    if (NetworkChecker(context).isInternetConnected){

                        if (isChacked){
                            viewModel.setUserLocation(address, postalCode)
                        }
                        //pardakht
                        viewModel.purchaseAll(address , postalCode ){ sucess , link->

                            if (sucess){

                                Toast.makeText(context, "pay using zarinPal..", Toast.LENGTH_SHORT).show()

                                viewModel.setPaymentStatus(PAYMENT_PENDING)

                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                context.startActivity(intent)

                            } else {
                                Toast.makeText(context, "Problem in payment...", Toast.LENGTH_SHORT).show()
                            }

                        }

                    } else {
                        Toast.makeText(context, "please connect to internet first..", Toast.LENGTH_SHORT).show()
                    }

                }
            )
        }


    }

}

@Composable
fun PurchaseAll(
    totalPrice: String,
    onPurchaseClicked: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val fraction =
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 0.15f else 0.07f

    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                modifier = Modifier
                    .padding(start = 14.dp)
                    .size(182.dp, 40.dp),
                onClick = {
                    if (NetworkChecker(context).isInternetConnected) {
                        onPurchaseClicked.invoke()
                    } else {
                        Toast.makeText(context, "Please connect to internet", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            ) {
                Text(
                    modifier = Modifier.padding(2.dp),
                    text = "Let's Purchase !",
                    color = Color.White,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
                )
            }


            Surface(
                modifier = Modifier
                    .padding(2.dp, end = 10.dp)
                    .clip(Shapes.large),
                color = PriceBackground
            ) {

                Text(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
                    text = "total : " + stylePrice(totalPrice),
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}

@Composable
fun NoDataAnimation() {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_data))
    LottieAnimation(composition = composition, iterations = LottieConstants.IterateForever)
}

@Composable
fun CartToolBar(
    onBackClicked: () -> Unit,
    onProfileClicked: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { onBackClicked.invoke() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        elevation = 2.dp,
        backgroundColor = Color.White,
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(
                text = "My Cart",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 24.dp),
                textAlign = TextAlign.Center
            )
        },
        actions = {

            IconButton(
                modifier = Modifier.padding(end = 6.dp),
                onClick = { onProfileClicked.invoke() }
            ) {

                Icon(Icons.Default.Person, contentDescription = null)

            }
        }

    )
}

@Composable
fun CartList(
    data: List<Product>,
    isChangingNumber: Pair<String, Boolean>,
    onAddItemClicked: (String) -> Unit,
    onRemoveItemClicked: (String) -> Unit,
    onItemClicked: (String) -> Unit

) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp, top = 16.dp)
    ) {
        items(data.size) {
            CartItem(
                data = data[it],
                isChangingNumber = isChangingNumber,
                onAddItemClicked = onAddItemClicked,
                onRemoveItemClicked = onRemoveItemClicked,
                onItemClicked = onItemClicked
            )
        }
    }
}

@Composable
fun CartItem(
    data: Product,
    isChangingNumber: Pair<String, Boolean>,
    onAddItemClicked: (String) -> Unit,
    onRemoveItemClicked: (String) -> Unit,
    onItemClicked: (String) -> Unit

) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .clickable { onItemClicked.invoke(data.productId) },
        elevation = 4.dp,
        shape = Shapes.large
    ) {
        Column {
            AsyncImage(
                model = data.imgUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(
                    modifier = Modifier.padding(10.dp)
                ) {

                    Text(
                        text = data.name,
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = "From " + data.category + " Group",
                        style = TextStyle(
                            fontSize = 14.sp
                        )
                    )

                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = "Product authenticity guarantee",
                        style = TextStyle(
                            fontSize = 14.sp
                        )
                    )

                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = "Available in stoke to ship",
                        style = TextStyle(
                            fontSize = 14.sp
                        )
                    )

                    Surface(
                        modifier = Modifier
                            .padding(top = 18.dp, bottom = 6.dp)
                            .clip(Shapes.large),
                        color = PriceBackground
                    ) {

                        Text(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            text = stylePrice(
                                (data.price.toInt() * (data.quantity ?: "1").toInt()).toString()
                            ),
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .padding(bottom = 16.dp, end = 8.dp)
                        .align(Alignment.Bottom)
                ) {

                    Card(
                        border = BorderStroke(2.dp, Blue)

                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.wrapContentWidth().height(40.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            if (data.quantity?.toInt() == 1) {

                                IconButton(onClick = { onRemoveItemClicked.invoke(data.productId) }) {

                                    Icon(
                                        modifier = Modifier.size(14.dp),
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null
                                    )
                                }
                            } else {
                                IconButton(onClick = { onRemoveItemClicked.invoke(data.productId) }) {

                                    Icon(
                                        modifier = Modifier.size(14.dp),
                                        painter =  painterResource(id = R.drawable.ic_minus),
                                        contentDescription = null
                                    )
                                }
                            }

                            // size of product
                            if (isChangingNumber.first == data.productId && isChangingNumber.second) {

                                Text(
                                    text = "...",
                                    style = TextStyle(fontSize = 18.sp),
                                    modifier = Modifier.padding( bottom = 10.dp)
                                )
                            } else {
                                Text(
                                    text = data.quantity ?: "1",
                                    style = TextStyle(fontSize = 16.sp),
                                    modifier = Modifier.padding( bottom = 4.dp)
                                )

                            }

                            //add button
                            IconButton(onClick = { onAddItemClicked.invoke(data.productId) }) {

                                Icon(
                                    modifier = Modifier.size(14.dp),
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}