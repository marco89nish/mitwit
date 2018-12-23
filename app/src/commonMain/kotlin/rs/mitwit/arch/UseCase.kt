package rs.mitwit.arch

abstract class UseCase<out Type, in Params> where Type : Any {

    abstract suspend operator fun invoke(params: Params): Type

    object NoParams
}