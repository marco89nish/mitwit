package rs.mitwit

expect object Platform {
    val name: String
    fun getCurrentTimeMillis() : Long
}

fun hello(): String = "Hello from ${Platform.name}"

class Proxy {
    fun proxyHello() = hello()
}