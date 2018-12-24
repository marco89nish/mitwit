package rs.mitwit.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
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
    fun onSignUpClicked(
        username: String,
        email: String,
        password: String,
        confirmPass: String
    )
}

interface UserSignupView {
    fun startProgress()
    fun stopProgress()
    fun setErrorUsernameTaken()
    fun setErrorPasswordInvalid()
    fun setErrorNetworkFailed()
    fun clearErrors()
    fun gotoNextScreen()
    fun setErrorUsernameNotSet()
    fun setErrorEmailNotSet()
    fun setErrorPasswordNotSet()
    fun setErrorPasswordConfirmNotSet()
    fun setErrorPasswordConfirmNotMatch()
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

object EmailValidator {
    private val emailRegex: Regex = Regex.fromLiteral(
        "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    )

    fun isValid(email: String): Boolean {
        return email.contains('@') && email.contains('.')
        //return emailRegex.matches(email)
    }
}

class UserSignupPresenterImpl(
    val signupUser: SignUpUseCase,
    val view: UserSignupView
) : BasePresenter(),
    UserSignupPresenter {

    override fun onSignUpClicked(
        username: String,
        email: String,
        password: String,
        confirmPass: String
    ) {

        view.clearErrors()
        var verificationPassed = true
        if (username.isBlank()) {
            view.setErrorUsernameNotSet()
            verificationPassed = false
        }
        if (email.isBlank() || !EmailValidator.isValid(email)){
            view.setErrorEmailNotSet()
            verificationPassed = false
        }

        if (password.isBlank()){
            view.setErrorPasswordNotSet()
            verificationPassed = false
        }

        if (confirmPass.isBlank()){
            view.setErrorPasswordConfirmNotSet()
            verificationPassed = false
        }

        if (password != confirmPass){
            view.setErrorPasswordConfirmNotMatch()
            verificationPassed = false
        }


        if (!verificationPassed) return
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
                if (!isActive) return@launch
                view.setErrorNetworkFailed()
            }
            view.stopProgress()
        }
    }

}