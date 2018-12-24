package rs.mitwit

expect object Platform {
    val name: String
    fun getCurrentTimeMillis() : Long
}

expect object Logger {
    fun log(message: String, throwable: Throwable? = null)
}