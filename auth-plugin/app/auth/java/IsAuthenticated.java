package auth.java;

import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import auth.AuthenticationType;

@With(AuthenticatedAction.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAuthenticated {
    AuthenticationType value() default AuthenticationType.Session;
}
