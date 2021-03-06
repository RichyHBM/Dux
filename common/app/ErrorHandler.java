import common.LoggedException;
import common.utilities.StringUtils;
import play.Configuration;
import play.Environment;
import play.Logger;
import play.api.OptionalSourceMapper;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Provider;

import static play.mvc.Results.*;

public class ErrorHandler extends DefaultHttpErrorHandler {

    @Inject
    public ErrorHandler(Configuration configuration,
                        Environment environment,
                        OptionalSourceMapper sourceMapper,
                        Provider<Router> routes) {
        super(configuration, environment, sourceMapper, routes);
    }

    public Promise<Result> onClientError(RequestHeader request, int statusCode, String message) {

        if(statusCode == play.mvc.Http.Status.NOT_FOUND) {
            // Implementation of 'GlobalSettings.onHandlerNotFound'

            if(StringUtils.isNotEmpty(request.uri())) {

                // If the uri isn't lower case, redirect
                if( !request.uri().equals( request.uri().toLowerCase() ) ) {
                    Logger.debug("Attempting lowercase redirect for: " + request.uri());
                    return Promise.<Result>pure(redirect(request.uri().toLowerCase()));
                }

                // If the uri ends with /, redirect without
                if( request.uri().endsWith("/") ) {
                    Logger.debug("Attempting backslash removal redirect for: " + request.uri());
                    return Promise.<Result>pure(redirect(request.uri().substring(0, request.uri().length() - 1)));
                }
            }
        }

        if(request.method() == "POST") {
            return Promise.<Result>pure(badRequest(message));
        }

        return super.onClientError(request, statusCode, message);
    }

    public Promise<Result> onServerError(RequestHeader request, Throwable exception) {

        //Get the first cause of exception
        Throwable t = exception.getCause();
        Throwable rootException = exception;

        while (t != null) {
            rootException = t;
            t = rootException.getCause();
        }

        if(rootException instanceof LoggedException) {
            LoggedException loggedException = (LoggedException)rootException;
            if(StringUtils.isNotEmpty(loggedException.debugMessage())) {
                Logger.error(loggedException.debugMessage());
            }
        }

        if(request.method() == "POST") {
            return Promise.<Result>pure(internalServerError(rootException.getMessage()));
        }
        // Implementation of 'GlobalSettings.onError'
        return super.onServerError(request, exception);
    }
}
