package rs.mitwit

actual object Platform {
    actual val name: String = "iOS"
    actual fun getCurrentTimeMillis(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

actual object Logger {
    actual fun log(message: String, throwable: Throwable?) {
        //tood
    }
}