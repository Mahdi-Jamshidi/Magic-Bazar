package ir.magiccodes.magicbazar.model.repository

object TokenInMemory {

    var userName: String? = null
        private set

    var token: String? = null
        private set

    fun refreshToken(userName: String?, token: String?) {
        this.userName = userName
        this.token = token
    }
}