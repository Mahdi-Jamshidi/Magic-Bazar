package ir.magiccodes.magicbazar.ui.features.signUp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.magiccodes.magicbazar.model.repository.user.UserRepository
import ir.magiccodes.magicbazar.util.coroutineExceptionHandler
import kotlinx.coroutines.launch

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {

    val name = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val confirmPassword = MutableLiveData("")

    fun signUpUsers(LoggingEvent: (String)-> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = userRepository.signUp(name.value!!, email.value!!, password.value!!)
            LoggingEvent(result)
        }
    }
}