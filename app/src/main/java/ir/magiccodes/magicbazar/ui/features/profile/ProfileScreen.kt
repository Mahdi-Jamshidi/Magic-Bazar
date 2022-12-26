package ir.magiccodes.magicbazar.ui.features.profile

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.ContentAlpha.medium
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.navigation.getNavViewModel
import ir.magiccodes.magicbazar.R
import ir.magiccodes.magicbazar.ui.features.product.MainTextField
import ir.magiccodes.magicbazar.ui.theme.Blue
import ir.magiccodes.magicbazar.ui.theme.Shapes
import ir.magiccodes.magicbazar.util.MyScreens
import ir.magiccodes.magicbazar.util.styleTime

@Composable
fun ProfileScreen() {

    val context = LocalContext.current
    val navigation = getNavController()

    val viewModel = getNavViewModel<ProfileViewModel>()
    viewModel.loadUserData()

    Box {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProfileToolbar(){
                navigation.popBackStack()
            }

            MainAnimation()

            Spacer(modifier = Modifier.padding(top = 6.dp))

            ShowDataSection("Email Address", viewModel.email.value, null)
            ShowDataSection("Address", viewModel.address.value){ viewModel.showLocationDialog.value = true}
            ShowDataSection("Postal Code", viewModel.postalCode.value){ viewModel.showLocationDialog.value = true}
            ShowDataSection("Login Time", styleTime(viewModel.loginTime.value.toLong()), null)

            Button(onClick = {
                Toast.makeText(context, "Hope to see you again", Toast.LENGTH_SHORT).show()
                viewModel.signOut()

                navigation.navigate(MyScreens.MainScreen.route){
                    popUpTo(MyScreens.MainScreen.route){
                        inclusive = true
                    }
                    navigation.popBackStack()
                    navigation.popBackStack()
                }
            },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 36.dp)
            ) {
                Text(text = "Sign Out")
            }
        }
        
        if (viewModel.showLocationDialog.value){
            
            AddUserLocationDataDialog(
                showSaveLocation = false,
                onDismiss = { viewModel.showLocationDialog.value = false },
                onSubmitClicked = {address, postalCode, _->
                    viewModel.saveUserLocation(address, postalCode)
                }
            )
            
        }
    }
}

@Composable
fun AddUserLocationDataDialog(
    showSaveLocation: Boolean,
    onDismiss: () -> Unit,
    onSubmitClicked: (String, String, Boolean) -> Unit
) {

    val context = LocalContext.current
    val checkedState = remember { mutableStateOf(true) }
    val userAddress = remember { mutableStateOf("") }
    val userPostalCode = remember { mutableStateOf("") }
    val fraction = if (showSaveLocation) 0.695f else 0.625f

    Dialog(onDismissRequest = onDismiss) {

        Card(
            modifier = Modifier.fillMaxHeight(fraction),
            elevation = 8.dp,
            shape = Shapes.medium
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {

                Text(
                    text = "Add Location Data",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))

                MainTextField(userAddress.value, "your address...") {
                    userAddress.value = it
                }

                MainTextField(userPostalCode.value, "your postal code...") {
                    userPostalCode.value = it
                }

                if (showSaveLocation) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, start = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = checkedState.value,
                            onCheckedChange = { checkedState.value = it },
                        )

                        Text(text = "Save To Profile")

                    }

                }


                // Buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextButton(onClick = onDismiss) {
                        Text(text = "Cancel")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(onClick = {

                        if (
                            (userAddress.value.isNotEmpty() || userAddress.value.isNotBlank()) &&
                            (userPostalCode.value.isNotEmpty() || userPostalCode.value.isNotBlank())
                        ) {
                            onSubmitClicked(
                                userAddress.value,
                                userPostalCode.value,
                                checkedState.value
                            )
                            onDismiss.invoke()
                        } else {
                            Toast.makeText(context, "please write first...", Toast.LENGTH_SHORT)
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
fun ShowDataSection(
    subject: String,
    textToShow: String,
    OnLocationClicked: (() -> Unit)?
) {

    Column(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .clickable { OnLocationClicked?.invoke() },
        horizontalAlignment = Alignment.Start
    ) {


        Text(
            text = subject,
            style = TextStyle(fontSize = 18.sp, color = Blue, fontWeight = FontWeight.Bold)
        )

        Text(
            text = textToShow,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(top = 2.dp)
        )

        Divider(color = Blue, thickness = 0.5.dp, modifier = Modifier.padding(top = 16.dp))

    }

}

@Composable
fun ProfileToolbar(onBackClick: () -> Unit) {

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { onBackClick.invoke() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack, contentDescription = null
                )
            }
        },
        elevation = 2.dp,
        backgroundColor = Color.White,
        modifier = Modifier.fillMaxSize(),
        title = {
            Text(
                text = "My Profile",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 58.dp)

            )
        }
    )
}

@Composable
fun MainAnimation() {

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes (R.raw.profile_anim)
    )

    LottieAnimation(
        modifier = Modifier
            .size(270.dp)
            .padding(top = 36.dp, bottom = 16.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}

