package ir.magiccodes.magicbazar.model.repository.user

interface UserRepository {

    //online
    suspend fun signUp(name: String, username: String, password: String): String
    suspend fun signIn(username: String, password: String): String


    //offline
    fun signOut()
    fun loadToken()

    fun saveToken(newToken: String)
    fun getToken(): String?

    fun saveUserName(username: String)
    fun getUserName(): String?

    fun saveUserLocation(address: String, postalCode: String)
    fun getUserLocation(): Pair<String, String>

    fun saveUserLoginTime()
    fun getUserLoginTime(): String
}