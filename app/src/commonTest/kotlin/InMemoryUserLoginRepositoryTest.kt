import rs.mitwit.Platform
import rs.mitwit.persistence.InMemoryUserLoginRepository
import rs.mitwit.models.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class InMemoryUserLoginRepositoryTest {

    val repo = InMemoryUserLoginRepository


    @Test
    fun `new repo should have logged out state`() {
        val loginState : UserLoginState = repo.getLoginState()

        assertTrue(loginState is UserNeverLoggedIn)
    }

    @Test
    fun `when new user logs in, repo should have logged in state`() {
        val userId = UserId("user1")
        val token = UserToken(userId, Token("token"), Time(Platform.getCurrentTimeMillis()+1000000))
        repo.loginUser(token)//todo

        val loginState = repo.getLoginState()

        assertTrue(loginState is UserLoggedIn)
        assertEquals( userId, loginState.user)
    }

    @Test
    fun `when user's login expires, state should be logged out`() {
        val userId = UserId("user1")
        val token = UserToken(userId, Token("token"), Time(Platform.getCurrentTimeMillis()-1000))
        repo.loginUser(token)

        val loginState = repo.getLoginState()
        assertTrue(loginState is UserLoggedOut)
        assertEquals(userId, loginState.user)
    }


    @Test
    fun `when user logouts, state should be logged out`() {
        val userId = UserId("user1")
        val token = UserToken(userId, Token("token"), Time(Platform.getCurrentTimeMillis()+1000000))
        repo.loginUser(token)

        repo.logoutUser()

        val loginState = repo.getLoginState()
        assertTrue(loginState is UserLoggedOut)
        assertEquals(userId, loginState.user)
    }




}