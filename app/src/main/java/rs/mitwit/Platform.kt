package rs.mitwit

import android.util.Log

actual object Platform {
    actual val name: String = "Android"
    actual fun getCurrentTimeMillis() = System.currentTimeMillis()
}

actual object Logger {
    actual fun log(message: String, throwable: Throwable?) {
        if (throwable != null)
            Log.d("Logger", message, throwable)
        else
            Log.d("Logger", message)
    }
}