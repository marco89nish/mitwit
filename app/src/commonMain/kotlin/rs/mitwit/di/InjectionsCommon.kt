package rs.mitwit.di

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import rs.mitwit.LoadingScreenPresenter
import rs.mitwit.LoadingScreenPresenterImpl
import rs.mitwit.LoadingScreenView
import rs.mitwit.network.KtorNetworkService
import rs.mitwit.network.UserLoginService
import rs.mitwit.persistence.InMemoryUserLoginRepository
import rs.mitwit.persistence.TimelineRepository
import rs.mitwit.persistence.TimelineRepositoryImpl
import rs.mitwit.persistence.UserLoginRepository
import rs.mitwit.posts.*
import rs.mitwit.user.*


object Injector {

    private val userLoginRepository: UserLoginRepository by lazy {
        InMemoryUserLoginRepository
    }

    private val ktorNetworkService by lazy {
        KtorNetworkService("192.168.0.11", 8080, HttpClient { install(JsonFeature) })
    }

    private val userLoginService: UserLoginService by lazy { ktorNetworkService }


    private val loginUserUseCase: LoginUserUseCase by lazy { LoginUserUseCase(userLoginRepository, userLoginService) }
    private val getLoginState: GetLoginStateUseCase by lazy { GetLoginStateUseCase(userLoginRepository) }


    fun provideUserLoginPresenter(view: UserLoginView): UserLoginPresenter =
        UserLoginPresenterImpl(loginUserUseCase, getLoginState, view)


    private val signupUserUseCase: SignUpUseCase by lazy { SignUpUseCase(userLoginRepository, userLoginService) }

    fun provideSignupPresenter(view: UserSignupView): UserSignupPresenter =
        UserSignupPresenterImpl(signupUserUseCase, view)


    private val timelineRepository: TimelineRepository by lazy { TimelineRepositoryImpl(ktorNetworkService) }
    private val getTimelineUsecase: GetTimelineUsecase by lazy {
        GetTimelineUsecase(userLoginRepository, timelineRepository)
    }
    private val deletePostUsecase: DeletePostFromTimelineUsecase by lazy {
        DeletePostFromTimelineUsecase(userLoginRepository, timelineRepository)
    }

    private val postToTimelineUsecase: PostToTimelineUsecase by lazy {
        PostToTimelineUsecase(userLoginRepository, timelineRepository)
    }

    fun provideTimelinePresenter(view: TimelineView): TimelinePresenter =
        TimelinePresenterImpl(view, getTimelineUsecase, deletePostUsecase, postToTimelineUsecase)


    fun provideLoadingScreenPresenter(view: LoadingScreenView) : LoadingScreenPresenter =
            LoadingScreenPresenterImpl(view, getLoginState)
}
