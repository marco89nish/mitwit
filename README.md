# mitwit

Kotlin multiplatfom twiter-lite app (very lite, though). Uses MVP/Clean to share as much codebase as possible between Android, iOS and Server. (Need a bit of help for iOS UI part)

Tech used: Ktor for networking and server, Coroutines for async, Anko for dialogs, Kotlin 1.3 for language goodies(inline classes).

## How to use
Open in Android Studio or (prefferably) InteliJ IDEA. Import project from gradle if IDE doesn't do it automatically. Check "Delegate IDE build/run action to gradle" box in Settings/Build, Execution, Deployment/Build Tools/Gradle/Runner. Create local.properties file with `sdk.dir` property set. You should be able to build app from there. Binaries will be available in the releases section. 

You can run server with gradle task `:app:runServer`. You'll need to configure Android application to target server's IP address in Login activity by clicking on settings icon. If registering bothers you, you can use test account - username:`test`, password:`a`. Persistance is not implemented at the moment, so restarting the server will lose registered users and their posts, while (fully) restarting app will lose the login token. 

## TODO
 * iOS UI
 * Persistance
 * Caching
 * Better DI
