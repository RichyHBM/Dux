Common
======

This is a common project containing base views, static files and common functionality

To include in a project, add this project as a dependency to your project:

```
lazy val common = RootProject(file("../common"))
lazy val root = (project in file("."))
    .enablePlugins(PlayJava)
    .aggregate(common)
    .dependsOn(common)
```

Add the routes of this common project to your project routes:

```
# Routes
# This file defines all application routes (Higher priority routes first)
# ~~~~

# Endpoints provided by plugins
->      /                           common.Routes

# Home page
GET     /                           controllers.Application.index()

# Map static resources from the /public folder to the /assets URL path
GET     /assets/*file               controllers.Assets.versioned(path="/public", file: Asset)
```

And delete the public folder

This also contains some useful test utilities, to use them make sure you have added the dependency in tests as well:

```
lazy val common = RootProject(file("../common"))
lazy val root = (project in file("."))
    .enablePlugins(PlayJava)
    .aggregate(common)
    .dependsOn(common % "test->test;compile->compile")
```

The test utilites require PhantomJS to be installed on your computer for better integration testing.

This project also includes a number of views that can be used.

Firstly, change the main.scala.html in your project to be a wrapper around the common main view

```
@(response: common.models.BasicViewResponse)(content: Html)

@common.views.html.main(response) {
    @common.views.html.navbar(response) {

    }

    @common.views.html.alert()

    @content
}
```

And include any common html code to your project in here.
