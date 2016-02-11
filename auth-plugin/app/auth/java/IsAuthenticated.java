package auth.java;

import auth.AuthenticationType;
import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@With(AuthenticatedAction.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAuthenticated {
    String priviledge() default "";
    AuthenticationType authentication() default AuthenticationType.Session;
}
