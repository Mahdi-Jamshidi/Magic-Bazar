package ir.magiccodes.magicbazar.model.repository.user

import android.content.SharedPreferences
import com.google.gson.JsonObject
import ir.magiccodes.magicbazar.model.net.ApiService
import ir.magiccodes.magicbazar.model.repository.TokenInMemory
import ir.magiccodes.magicbazar.util.VALUE_SUCCESS

class UserRepositoryImpl(
    val apiService: ApiService,
    val sharedPref: SharedPreferences
) : UserRepository {

    override suspend fun signUp(name: String, username: String, password: String): String {
        val jsonObject = JsonObject().apply {
            addProperty("name", name)
            addProperty("email", username)
            addProperty("password", password)
        }

        val result = apiService.signUp(jsonObject)
        if (result.success) {
            TokenInMemory.refreshToken(username, result.token)
            saveToken(result.token)
            saveUserName(username)
            saveUserLoginTime()
            return VALUE_SUCCESS
        } else {
            return result.message
        }
    }

    override suspend fun signIn(username: String, password: String): String {

        val jsonObject = JsonObject().apply {
            addProperty("email", username)
            addProperty("password", password)
        }
        val result = apiService.signIn(jsonObject)
        if (result.success) {
            TokenInMemory.refreshToken(username, result.token)
            saveToken(result.token)
            saveUserName(username)
            saveUserLoginTime()
            return VALUE_SUCCESS
        } else {
            return result.message
        }
    }

    override fun signOut() {
        TokenInMemory.refreshToken(null, null)
        sharedPref.edit().clear().apply()
    }

    override fun loadToken() {
        // load token in SharedPreferences to memory cash
        TokenInMemory.refreshToken(getUserName(), getToken())
    }

    override fun saveToken(newToken: String) {
        sharedPref.edit().putString("token", newToken).apply()
    }

    override fun getToken(): String? {
        return sharedPref.getString("token", null)
    }

    override fun saveUserName(username: String) {
        sharedPref.edit().putString("username", username).apply()
    }

    override fun getUserName(): String? {
        return sharedPref.getString("username", null)
    }

    override fun saveUserLocation(address: String, postalCode: String) {
        sharedPref.edit().putString("address", address).apply()
        sharedPref.edit().putString("postalCode", postalCode).apply()
    }

    override fun getUserLocation(): Pair<String, String> {
        val address = sharedPref.getString("address", "click to add")!!
        val postalCode = sharedPref.getString("postalCode", "click to add")!!

        return Pair(address, postalCode)
    }

    override fun saveUserLoginTime() {
        val now = System.currentTimeMillis()
        sharedPref.edit().putString("login_time", now.toString()).apply()
    }

    override fun getUserLoginTime(): String {
        return sharedPref.getString("login_time","0")!!
    }
}