package ir.magiccodes.magicbazar.ui.features.product

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.navigation.getNavViewModel
import ir.dunijet.dunibazaar.util.NetworkChecker
import ir.magiccodes.magicbazar.R

import ir.magiccodes.magicbazar.model.data.Comment
import ir.magiccodes.magicbazar.model.data.Product
import ir.magiccodes.magicbazar.ui.theme.*
import ir.magiccodes.magicbazar.util.MyScreens
import ir.magiccodes.magicbazar.util.stylePrice
import okhttp3.internal.wait

@Preview(showBackground = true)
@Composable
fun ProductScreenPreview() {

    MainAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundMain
        ) {
            ProductScreen("")
        }
    }
}

@Composable
fun ProductScreen(productId: String) {
    val uiController = rememberSystemUiController()
    SideEffect { uiController.setStatusBarColor(Color.White) }

    val context = LocalContext.current
    val viewModel = getNavViewModel<ProductViewModel>()
    viewModel.loadData(productId, NetworkChecker(context).isInternetConnected)
    val navigation = getNavController()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 58.dp)
        ) {

            ProductToolBar(productName = "Details",
                badgeNumber = viewModel.badgeNumber.value,
                onBackClicked = { navigation.popBackStack() },
                onCartClicked = {

                    if (NetworkChecker(context).isInternetConnected) {
                        navigation.navigate(MyScreens.CartScreen.route)
                    } else {
                        Toast.makeText(
                            context,
                            "Please connect to Internet first..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )

            val comments = if (NetworkChecker(context).isInternetConnected) viewModel.comments.value else listOf()
            ProductItem(
                data = viewModel.thisProduct.value,
                comments = comments ,
                onCategoryClicked = {
                    navigation.navigate(MyScreens.CategoryScreen.route + "/" + it)
                },
                onAddNewComment = {
                    viewModel.addNewComment(productId, it) { payam ->
                        Toast.makeText(context, payam, Toast.LENGTH_SHORT).show()
                    }
                }
            )

        }

        AddToCart(
            viewModel.thisProduct.value.price,
            viewModel.isAddingProduct.value
        ) {

            if (NetworkChecker(context).isInternetConnected) {
                viewModel.addProductToCart(productId) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(context, "Connect to internet..", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

@Composable
fun ProductItem(
    data: Product,
    comments: List<Comment>,
    onCategoryClicked: (String) -> Unit,
    onAddNewComment: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        ProductDesign(data, onCategoryClicked)

        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier
                .padding(bottom = 14.dp)
        )

        ProductDetail(data, comments.size.toString())

        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier
                .padding(top = 14.dp, bottom = 4.dp)
        )

        ProductComments(comments, onAddNewComment)
    }
}

@Composable
fun CommentBody(comment: Comment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        elevation = 0.dp,
        border = BorderStroke(1.dp, Color.LightGray),
        shape = Shapes.large
    ) {

        Column(modifier = Modifier.padding(12.dp)) {
            Text(

                text = comment.userEmail,
                style = TextStyle(fontSize = 15.sp),
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = comment.text,
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }
}


@Composable
fun ProductComments(comments: List<Comment>, addNewComment: (String) -> Unit) {

    val showCommentDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (comments.isNotEmpty()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comments",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            TextButton(onClick = {
                if (NetworkChecker(context).isInternetConnected) {
                    showCommentDialog.value = true
                } else {
                    Toast.makeText(
                        context,
                        "Connect to internet to add comment",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
                Text(
                    text = "Add New Comment",
                    style = TextStyle(fontSize = 14.sp)
                )
            }
        }
        comments.forEach {
            CommentBody(it)
        }
    } else {
        TextButton(onClick = {
            if (NetworkChecker(context).isInternetConnected) {
                showCommentDialog.value = true
            } else {
                Toast.makeText(context, "Connect to internet to add comment", Toast.LENGTH_SHORT)
                    .show()
            }
        }) {
            Text(
                text = "Add New Comment",
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }

    if (showCommentDialog.value) {
        AddNewCommentDialog(
            OnDismiss = { showCommentDialog.value = false },
            OnPositiveClick = { addNewComment.invoke(it) })

    }
}

@Composable
fun AddNewCommentDialog(
    OnDismiss: () -> Unit,
    OnPositiveClick: (String) -> Unit
) {

    val context = LocalContext.current
    val userComment = remember { mutableStateOf("") }

    Dialog(onDismissRequest = OnDismiss) {

        Card(
            modifier = Modifier.fillMaxHeight(0.48f),
            elevation = 8.dp,
            shape = Shapes.medium
        ) {


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Write Your Comment",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // enter data =>
                MainTextField(
                    edtValue = userComment.value,
                    hint = "write something..."
                ) {
                    userComment.value = it
                }

                // Buttons =>
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextButton(onClick = { OnDismiss.invoke() }) {
                        Text(text = "Cancel")
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    TextButton(onClick = {

                        if (userComment.value.isNotEmpty() && userComment.value.isNotBlank()) {
                            if (NetworkChecker(context).isInternetConnected) {
                                OnPositiveClick(userComment.value)
                                OnDismiss.invoke()
                            } else {
                                Toast.makeText(
                                    context,
                                    "connect to internet first...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(context, "please write  first...", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }) {
                        Text(text = "Ok")
                    }
                }
            }
        }
    }
}

@Composable
fun MainTextField(edtValue: String, hint: String, onValueChanges: (String) -> Unit) {
    OutlinedTextField(
        label = { Text(text = hint) },
        value = edtValue,
        singleLine = false,
        maxLines = 2,
        onValueChange = onValueChanges,
        placeholder = { Text(text = "Write Something...") },
        modifier = Modifier.fillMaxWidth(0.9f),
        shape = Shapes.medium
    )
}

@Composable
fun ProductDetail(data: Product, commentNumber: String) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Image(
                    painter = painterResource(id = R.drawable.ic_details_comment),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )

                val commentText =
                    if (NetworkChecker(context).isInternetConnected) commentNumber + " Comments" else "No Internet"
                Text(
                    text = commentText,
                    modifier = Modifier.padding(start = 6.dp),
                    fontSize = 13.sp
                )

            }

            Row(
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_details_material),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )

                Text(
                    text = data.material,
                    modifier = Modifier.padding(start = 6.dp),
                    fontSize = 13.sp
                )

            }

            Row(
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_details_sold),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )

                Text(
                    text = data.soldItem + " Sold",
                    modifier = Modifier.padding(start = 6.dp),
                    fontSize = 13.sp
                )

            }

        }

        Surface(
            modifier = Modifier
                .clip(Shapes.large)
                .align(Alignment.Bottom),
            color = Blue
        ) {
            Text(
                text = data.tags,
                color = Color.White,
                modifier = Modifier.padding(6.dp),
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
            )
        }

    }

}

@Composable
fun ProductDesign(data: Product, onCategoryClicked: (String) -> Unit) {

    AsyncImage(
        model = data.imgUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(shape = Shapes.medium)
    )

    Text(
        modifier = Modifier.padding(top = 14.dp),
        text = data.name,
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    )

    Text(
        modifier = Modifier.padding(top = 4.dp),
        text = data.detailText,
        style = TextStyle(
            fontSize = 15.sp,
            textAlign = TextAlign.Justify
        )
    )

    TextButton(
        modifier = Modifier.padding(top = 10.dp),
        onClick = { onCategoryClicked.invoke(data.category) }) {
        Text(
            text = "#" + data.category,
            style = TextStyle(fontSize = 13.sp)
        )
    }
}

@Composable
fun ProductToolBar(
    productName: String,
    badgeNumber: Int,
    onBackClicked: () -> Unit,
    onCartClicked: () -> Unit
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
                text = productName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 24.dp),
                textAlign = TextAlign.Center
            )
        },
        actions = {

            IconButton(
                modifier = Modifier.padding(end = 6.dp),
                onClick = { onCartClicked.invoke() }
            ) {
                if (badgeNumber == 0) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                } else {
                    BadgedBox(badge = { Badge { Text(text = badgeNumber.toString()) } }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    }
                }
            }
        }

    )
}


@Composable
fun AddToCart(
    price: String,
    isAddingProduct: Boolean,
    onCartClicked: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val fraction = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 0.15f else 0.09f

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
                    .padding(16.dp)
                    .size(182.dp, 40.dp),
                onClick = { onCartClicked.invoke() }
            ) {

                if (isAddingProduct) {
                    DotsTyping()
                } else {
                    Text(
                        text = "Add To Cart",
                        modifier = Modifier.padding(2.dp),
                        color = Color.White,
                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clip(Shapes.large),
                color = PriceBackground
            ) {
                Text(
                    text = stylePrice(price),
                    modifier = Modifier.padding(
                        top = 6.dp,
                        bottom = 6.dp,
                        start = 8.dp,
                        end = 8.dp
                    ),
                    color = Color.Black,
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}

@Composable
fun DotsTyping() {

    val dotSize = 8.dp
    val delayUnit = 350
    val maxOffset = 10f

    @Composable
    fun Dot(
        offset: Float
    ) = Spacer(
        Modifier
            .size(dotSize)
            .offset(y = -offset.dp)
            .background(
                color = Color.White,
                shape = CircleShape
            )
            .padding(start = 8.dp, end = 8.dp)
    )

    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateOffsetWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                0f at delay with LinearEasing
                maxOffset at delay + delayUnit with LinearEasing
                0f at delay + delayUnit * 2
            }
        )
    )

    val offset1 by animateOffsetWithDelay(0)
    val offset2 by animateOffsetWithDelay(delayUnit)
    val offset3 by animateOffsetWithDelay(delayUnit * 2)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = maxOffset.dp)
    ) {
        val spaceSize = 2.dp

        Dot(offset1)
        Spacer(Modifier.width(spaceSize))
        Dot(offset2)
        Spacer(Modifier.width(spaceSize))
        Dot(offset3)
    }
}


