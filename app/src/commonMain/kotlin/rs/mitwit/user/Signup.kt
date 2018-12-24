package rs.mitwit.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rs.mitwit.Logger
import rs.mitwit.persistence.UserLoginRepository
import rs.mitwit.arch.BasePresenter
import rs.mitwit.arch.Presenter
import rs.mitwit.arch.UseCase
import rs.mitwit.models.*
import rs.mitwit.network.UserLoginService

interface UserSignupPresenter : Presenter {
    fun onSignUpClicked(username: String, email: String, password: String)
}

interface UserSignupView {
    fun startProgress()
    fun stopProgress()
    fun setErrorUsernameTaken()
    fun setErrorPasswordInvalid()
    fun setErrorNetworkFailed()
    fun clearErrors()
    fun gotoNextScreen()
}

class SignUpUseCase(private val repository: UserLoginRepository, private val networkService: UserLoginService) :
    UseCase<SignupResult, UserSignupRequest>() {

    override suspend operator fun invoke(params: UserSignupRequest): SignupResult {
        val result = networkService.signupUser(params).getResult()

        when (result) {
            is SignupResultSuccess -> {
                repository.loginUser(result.token)
            }
        }

        return result
    }
}

class UserSignupPresenterImpl(
    val signupUser: SignUpUseCase,
    val view: UserSignupView
) : BasePresenter(),
    UserSignupPresenter {

    override fun onSignUpClicked(username: String, email: String, password: String) {
        view.clearErrors()
        view.startProgress()
        launch {
            try {
                val result = withContext(Dispatchers.Default) {
                    signupUser(UserSignupRequest(username, email, password))
                }

                when (result) {
                    is SignupResultPasswordInvalid -> view.setErrorPasswordInvalid()
                    is SignupResultUsernameTaken -> view.setErrorUsernameTaken()
                    is SignupResultSuccess -> view.gotoNextScreen()
                }

            } catch (e: Exception) {
                Logger.log("Signup failed", e)
                view.setErrorNetworkFailed()
            }
            view.stopProgress()
        }
    }

}