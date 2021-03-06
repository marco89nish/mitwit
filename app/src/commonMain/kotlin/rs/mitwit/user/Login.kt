package rs.mitwit.user

import kotlinx.coroutines.*
import rs.mitwit.Logger
import rs.mitwit.arch.BasePresenter
import rs.mitwit.arch.Presenter
import rs.mitwit.arch.UseCase
import rs.mitwit.models.*
import rs.mitwit.network.UserLoginService
import rs.mitwit.persistence.UserLoginRepository

interface UserLoginPresenter : Presenter {
    fun onSignInClicked(username: String, password: String)
}

interface UserLoginView {
    fun setUsername(username: String)
    fun startProgress()
    fun stopProgress()
    fun setErrorBadCredentials()
    fun setErrorNetworkFailed()
    fun clearErrors()
    fun gotoNextScreen()
    fun setErrorUsernameNotSet()
    fun setErrorPasswordNotSet()

}

class UserLoginPresenterImpl(
    val loginUser: LoginUserUseCase,
    val getLoginState: GetLoginStateUseCase,
    val view: UserLoginView
) : BasePresenter(),
    UserLoginPresenter {

    override fun onCreate() {
        super.onCreate()

        launch {
            val loginState = withContext(Dispatchers.Default) {
                getLoginState(UseCase.NoParams)
            }

            when (loginState) {
                is UserLoggedIn -> {
                    view.setUsername(getUsername(loginState.user))
                }
                is UserLoggedOut -> {
                    view.setUsername(getUsername(loginState.user))
                }
                is UserNeverLoggedIn -> {
                }
            }
        }
    }

    private suspend fun getUsername(user: UserId): String {
        return "" //todo
    }

    override fun onSignInClicked(username: String, password: String) {
        view.clearErrors()
        var verificationPassed = true
        if (username.isBlank()) {
            view.setErrorUsernameNotSet()
            verificationPassed = false
        }

        if (password.isBlank()) {
            view.setErrorPasswordNotSet()
            verificationPassed = false
        }
        if (!verificationPassed) return
        view.startProgress()
        launch {
            try {
                val result = withContext(Dispatchers.Default) {
                    loginUser(UserLoginRequest(username, password))
                }

                if (result.success) {
                    println("Yay, logged in!")
                    view.gotoNextScreen()
                } else {
                    view.setErrorBadCredentials()
                }

            } catch (e: Exception) {
                Logger.log("Sign in failed", e)
                if (!isActive) return@launch
                view.setErrorNetworkFailed()
            }
            view.stopProgress()
        }
    }

}

class GetLoginStateUseCase(private val repository: UserLoginRepository) : UseCase<UserLoginState, UseCase.NoParams>() {
    override suspend fun invoke(params: NoParams): UserLoginState = repository.getLoginState()
}

class LogoutUseCase(private val repository: UserLoginRepository, private val networkService: UserLoginService) :
    UseCase<Unit, UseCase.NoParams>() {
    override suspend fun invoke(params: NoParams): Unit {
        val userToken = repository.getUserToken()
        repository.logoutUser()
        if (userToken != null)
            GlobalScope.launch(Dispatchers.Default) { networkService.logoutUser(userToken.token) }
    }
}

class LoginUserUseCase(private val repository: UserLoginRepository, private val networkService: UserLoginService) :
    UseCase<LoginRequestResult, UserLoginRequest>() {

    override suspend operator fun invoke(params: UserLoginRequest): LoginRequestResult {
        val result = networkService.loginUser(params)
        if (result.success && result.token != null) {
            repository.loginUser(result.token)
        }
        return result
    }
}