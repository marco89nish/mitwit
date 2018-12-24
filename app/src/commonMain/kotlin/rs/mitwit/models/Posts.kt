package rs.mitwit.models

data class NewPost(val title: String, val content: String)
data class Post(val id: String, val title: String, val content: String, val time: Time)
data class Timeline(val posts: List<Post>)