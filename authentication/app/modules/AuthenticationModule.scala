package modules

import com.google.inject.AbstractModule
import implementors.AuthenticationCache
import interfaces.IAuthenticationCache

case class AuthenticationModule() extends AbstractModule {
    override def configure() {
        bind(classOf[IAuthenticationCache]).to(classOf[AuthenticationCache])
    }
}
