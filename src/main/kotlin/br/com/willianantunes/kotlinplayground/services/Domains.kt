package br.com.willianantunes.kotlinplayground.services

class Posts : ArrayList<Post>()
data class Post(
    var body: String = "",
    var id: Long = 0
)

class Comments : ArrayList<Comment>()
data class Comment(
    var body: String = "",
    var id: Long = 0,
    var postId: Long = 0
)
