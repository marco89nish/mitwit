package rs.mitwit

import io.netty.util.internal.logging.Slf4JLoggerFactory

actual object Platform {
    actual val name = "JVM"
    actual fun getCurrentTimeMillis() = System.currentTimeMillis()
}

actual object Logger {
    private val logger by lazy { Slf4JLoggerFactory.getInstance("Logger") }

    actual fun log(message: String, throwable: Throwable?) {
        if (throwable != null)
            logger.debug(message, throwable)
        else
            logger.debug(message)
    }
}