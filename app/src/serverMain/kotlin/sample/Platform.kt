package sample

actual object Platform {
    actual val name: String
        get() = "JVM"
}

actual class Sample {
    actual fun checkMe() = 43
}