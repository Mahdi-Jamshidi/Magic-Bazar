package ir.magiccodes.magicbazar.model.data

data class LogInResponse(
    val expiresAt :Int,
    val message :String,
    val success : Boolean,
    val token: String
)
