package ir.magiccodes.magicbazar.model.repository.comment

import ir.magiccodes.magicbazar.model.data.Comment


interface CommentRepository {

    suspend fun getAllComments(productId: String): List<Comment>
    suspend fun addNewComment(productId: String, text: String, IsSuccess: (String) -> Unit)
}