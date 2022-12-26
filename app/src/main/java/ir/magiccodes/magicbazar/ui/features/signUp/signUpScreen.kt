package ir.magiccodes.magicbazar.ui.features.signUp

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.navigation.getNavViewModel
import ir.dunijet.dunibazaar.util.NetworkChecker
import ir.magiccodes.magicbazar.R
import ir.magiccodes.magicbazar.ui.theme.BackgroundMain
import ir.magiccodes.magicbazar.ui.theme.Blue
import ir.magiccodes.magicbazar.ui.theme.MainAppTheme
import ir.magiccodes.magicbazar.ui.theme.Shapes
import ir.magiccodes.magicbazar.util.MyScreens
import ir.magiccodes.magicbazar.util.VALUE_SUCCESS
import java.util.regex.Pattern

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    MainAppTheme {
        Surface(
            color = BackgroundMain,
            modifier = Modifier.fillMaxSize()
        ) {
            SingUpScreen()
        }
    }
}

@Composable
fun SingUpScreen() {
    val navigation = getNavController()
    val viewModel = getNavViewModel<SignUpViewModel>()
    val context = LocalContext.current
    val uiController = rememberSystemUiController()
    SideEffect { uiController.setStatusBarColor(Blue) }

    clearInputs(viewModel)

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(Blue)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 70.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            IconApp()
            Spacer(modifier = Modifier.height(30.dp))
            MainCartView(navigation, viewModel) {
                viewModel.signUpUsers{

                    if (it == VALUE_SUCCESS){
                        navigation.navigate(MyScreens.MainScreen.route){
                            popUpTo(MyScreens.IntroScreen.route){
                                inclusive = true
                            }
                        }
                    } else {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun IconApp() {
    Surface(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_icon_app),
            contentDescription = null,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
fun MainCartView(navigation: NavController, viewModel: SignUpViewModel, SignUpEvent: () -> Unit) {
    val name = viewModel.name.observeAsState("")
    val email = viewModel.email.observeAsState("")
    val password = viewModel.password.observeAsState("")
    val confirmPassword = viewModel.confirmPassword.observeAsState("")
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = 10.dp,
        shape = Shapes.medium
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Sign Up",
                modifier = Modifier.padding(top = 18.dp, bottom = 18.dp),
                style = TextStyle(
                    color = Blue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            MainTextField(
                edtValue = name.value,
                icon = R.drawable.ic_person,
                hint = "Your Full Name"
            ) { viewModel.name.value = it }
            MainTextField(
                edtValue = email.value,
                icon = R.drawable.ic_email,
                hint = "Email"
            ) { viewModel.email.value = it }
            PasswordTextField(
                edtValue = password.value,
                icon = R.drawable.ic_password,
                hint = "Password"
            ) { viewModel.password.value = it }
            PasswordTextField(
                edtValue = confirmPassword.value,
                icon = R.drawable.ic_password,
                hint = "Confirm Password"
            ) { viewModel.confirmPassword.value = it }

            Button(onClick =
            {
                if (name.value.isNotEmpty() && email.value.isNotEmpty() && password.value.isNotEmpty() && confirmPassword.value.isNotEmpty()) {
                    if (password.value == confirmPassword.value) {
                        if (password.value.length >= 8) {
                            if (Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                                if (NetworkChecker(context).isInternetConnected){
                                    SignUpEvent.invoke()
                                } else {
                                    Toast.makeText(context, "please connect to internet", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "email format is not true", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "password characters should be 8", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "passwords are not the same!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "please write all data", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.padding(top = 28.dp, bottom = 8.dp)
            ) {
                Text(text = "Register Account", modifier = Modifier.padding(8.dp))
            }

            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account?")
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = {
                    navigation.navigate(MyScreens.SignInScreen.route) {
                        popUpTo(MyScreens.SignUpScreen.route) {
                            inclusive = true
                        }
                    }
                }) { Text(text = "Log IN", color = Blue) }
            }
        }
    }
}

@Composable
fun MainTextField(
    edtValue: String, icon: Int, hint: String, onValueChanges: (String) -> Unit
) {

    OutlinedTextField(
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        label = { Text(text = hint) },
        value = edtValue,
        singleLine = true,
        onValueChange = onValueChanges,
        placeholder = { Text(text = hint) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 12.dp),
        shape = Shapes.medium,
        leadingIcon = { Icon(painterResource(icon), contentDescription = null) }
    )
}

@Composable
fun PasswordTextField(
    edtValue: String, icon: Int, hint: String, onValueChanges: (String) -> Unit
) {

    val passwordVisible = remember { mutableStateOf(false) }
    OutlinedTextField(
        label = { Text(text = hint) },
        value = edtValue,
        singleLine = true,
        onValueChange = onValueChanges,
        placeholder = { Text(text = hint) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 12.dp),
        shape = Shapes.medium,
        leadingIcon = { Icon(painterResource(icon), contentDescription = null) },
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible.value) painterResource(R.drawable.ic_invisible)
            else painterResource(R.drawable.ic_visible)

            Icon(painter = image,
                contentDescription = null,
                modifier = Modifier.clickable { passwordVisible.value = !passwordVisible.value })
        }
    )
}

fun clearInputs(viewModel: SignUpViewModel){
    viewModel.name.value =""
    viewModel.email.value =""
    viewModel.password.value =""
    viewModel.confirmPassword.value =""
}