package ir.magiccodes.magicbazar.ui.features.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ir.magiccodes.magicbazar.model.repository.user.UserRepository

class ProfileViewModel(
private val userRepository: UserRepository
): ViewModel() {

    val email = mutableStateOf("")
    val address = mutableStateOf("")
    val postalCode = mutableStateOf("")
    val loginTime = mutableStateOf("")

    val showLocationDialog = mutableStateOf(false)

    fun loadUserData(){

        email.value = userRepository.getUserName()!!
        loginTime.value = userRepository.getUserLoginTime()
        val location = userRepository.getUserLocation()
        address.value = location.first
        postalCode.value = location.second
    }

    fun signOut(){
        userRepository.signOut()
    }

    fun saveUserLocation(address: String, postalCode: String){
        userRepository.saveUserLocation(address, postalCode)
    }

}