package rs.mitwit

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.event.Level

fun Application.main() {
    install(ContentNegotiation) {
        gson {
            // Configure Gson here
        }
    }
    install(CallLogging) {
        level = Level.DEBUG
    }
    install(DefaultHeaders)

    routing {
        loginRoute()
        signupRoute()
        logoutRoute()
        getTimelineRoute()
        postRoute()
        deletePostRoute()
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080) { main() }.start(wait = true)
}



