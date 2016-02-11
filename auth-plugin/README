Authentication Plugin
=====================

The Authentication plugin provides an easy way to use authentication for Dux (or any play applications)

To add this to your project add it as a dependency in your build.sbt, this plugin also requires the common project.

```
lazy val common = RootProject(file("../common"))
lazy val auth = RootProject(file("../auth-plugin"))

lazy val root = (project in file(".")).enablePlugins(PlayJava)
    .aggregate(auth, common)
    .dependsOn(auth % "test->test;compile->compile", common % "test->test;compile->compile")

```

Once included, simply use the IsAuthenticated annotation.

```
public class Application extends Controller {

    @IsAuthenticated
    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    @IsAuthenticated(AuthenticationType.ApiKey)
    public Result apiIndex() {
        return ok("Your new application is ready.");
    }
}
```

You will also need to specify the Authentication Service endpoint in the config under "dux.authentication.url" as well as the base Privilege required for the service as "dux.authentication.privilege"

```
dux.authentication.url = "http://127.0.0.1:9000"
dux.authentication.privilege = "AUTH_ADMIN"
```

By default it uses a session based authentication, but you can specify what type of authentication each request allows by passing in a value from the AuthenticationType enum.

To use API Keys rather than session, the API Key must be supplied as part of the header request under the header "DUX-API-KEY"

The auth plugin intercepts requests and makes sure that the user has the correct authentication details in order to access the endpoint they are trying to access.

As with common the auth plugin has various views specific to it, most notably a authenticated navbar that will display user information if logged in.
