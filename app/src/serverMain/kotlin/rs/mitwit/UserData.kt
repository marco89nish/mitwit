package rs.mitwit

import rs.mitwit.models.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

object UserData {
    private val usersByIdMap: MutableMap<UserId, UserInfo> = HashMap()
    private val usersByUsernameMap: MutableMap<String, UserInfo> = HashMap()
    private val tokens: MutableMap<Token, UserToken> = HashMap()
    private val userIdCounter = AtomicLong(1)

    init {
        val testUser = UserInfo("test", "a", "a@b.com", getNextFreeUserId())
        registerUser(testUser)
    }

    fun debugPrint() = println(usersByUsernameMap)
    fun checkCredentials(username: String, password: String) =
        usersByUsernameMap[username].let { it != null && it.password == password }

    fun isUsernameTaken(username: String) = usersByUsernameMap.containsKey(username)
    fun validateTokenAndGetUser(token: Token) = tokens[token]?.let { if(it.expiry.epochTime > System.currentTimeMillis()) it else null }
    fun getNextFreeUserId() = UserId(userIdCounter.getAndIncrement().toString())
    fun getUserInfo(username: String) = usersByUsernameMap[username]
    fun invalidateToken(token: String) = tokens.remove(Token(token))
    fun loginUser(userId: UserId): UserToken {
        val token = UserToken(
            userId,
            Token(UUID.randomUUID().toString()),
            Time(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7))
        )
        tokens[token.token] = token
        return token
    }

    fun registerUser(userInfo: UserInfo): UserToken {
        usersByIdMap[userInfo.userId] = userInfo
        usersByUsernameMap[userInfo.username] = userInfo
        return loginUser(userInfo.userId)
    }

}