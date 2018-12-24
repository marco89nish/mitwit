package rs.mitwit

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rs.mitwit.arch.BasePresenter
import rs.mitwit.arch.Presenter
import rs.mitwit.arch.UseCase
import rs.mitwit.models.UserLoggedIn
import rs.mitwit.models.UserNeverLoggedIn
import rs.mitwit.user.GetLoginStateUseCase

interface LoadingScreenPresenter : Presenter {
}

interface LoadingScreenView {
    fun goToLoginScreen()
    fun goToTimelineScreen()
}

class LoadingScreenPresenterImpl(private val view: LoadingScreenView, private val getLoginState: GetLoginStateUseCase) : BasePresenter(), LoadingScreenPresenter {

    override fun onCreate() {
        super.onCreate()

        launch {

            val loginState = try {
                withContext(Dispatchers.Default) {
                    getLoginState(UseCase.NoParams)
                }
            } catch (e: Exception){
                Logger.log("Failed to get login state", e)
                UserNeverLoggedIn
            }

            when (loginState){
                is UserLoggedIn -> view.goToTimelineScreen()
                else -> view.goToLoginScreen()
            }
        }
    }

}