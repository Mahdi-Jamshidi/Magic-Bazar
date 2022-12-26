package ir.magiccodes.magicbazar.ui.features.signIn

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.magiccodes.magicbazar.model.repository.user.UserRepository
import ir.magiccodes.magicbazar.util.coroutineExceptionHandler
import kotlinx.coroutines.launch

class SignInViewModel( private val userRepository: UserRepository) : ViewModel(){

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    fun signInUsers(LoggingEvent : (String)-> Unit){

        viewModelScope.launch(coroutineExceptionHandler) {
            val result = userRepository.signIn(email.value!! , password.value!!)
            LoggingEvent(result)
        }
    }
}