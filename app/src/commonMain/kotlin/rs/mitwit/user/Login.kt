package rs.mitwit.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rs.mitwit.persistence.UserLoginRepository
import rs.mitwit.arch.BasePresenter
import rs.mitwit.arch.Presenter
import rs.mitwit.arch.UseCase
import rs.mitwit.models.*
import rs.mitwit.network.UserLoginService

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
}

class UserLoginPresenterImpl(
    val loginUser: LoginUserUseCase,
    val getLoginState: GetLoginStateUseCase,
    val view: UserLoginView
) : BasePresenter(),
    UserLoginPresenter {

    //todo: make base presenter class
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

            } catch (e: Throwable) {
                println(e)
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
        val token = repository.getUserToken()
        repository.logoutUser()
        if (token != null) networkService.logoutUser(token)
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