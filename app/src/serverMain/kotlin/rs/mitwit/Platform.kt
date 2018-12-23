package rs.mitwit

actual object Platform {
    actual val name = "JVM"
    actual fun getCurrentTimeMillis() = System.currentTimeMillis()
}

