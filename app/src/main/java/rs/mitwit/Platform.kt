package rs.mitwit

actual object Platform {
    actual val name: String = "Android"
    actual fun getCurrentTimeMillis() = System.currentTimeMillis()
}