# Bee Label Maker #

This is image annotation software built using scalatra with a connection to Postgres/H2 db with Slick and serving up a react/redux web front end.

## Initial Setup

https://github.com/smatt989/bee/wiki/Initial-Setup

## Build & Run ##

```sh
$ cd bee
$ ./sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.

For first time use, to create the db schema, run the following:
 ```sbt db-setup```

This command will:
1. Check to see if DB has already been initialized
2. If not, it will create the schema "BEE", and the basic migrations tables
3. Check which migration scripts have already been applied
4. Apply any unapplied migration scripts

## Development ##

For development you can have the web server recompile and restart after every code change by using the following code snippit:

```sh
$ cd bee
$ ./sbt
> ~;jetty:stop;jetty:start
```

To develop the front end separately from the backend, start the server the normal way, and separately run the front end on a different port:

```sh
$ cd bee/src/main/webapp/front-end
$ webpack-dev-server --host 0.0.0.0 --port 9000
```

There is a good reason to do this!  To have front end code changes show up immediately, run the backend the usual way (jetty:start) so that it does not restart when it detects a code change, and then run webpack-dev-server so that the front end DOES recompile when it detects a code change.  This will save lots of time.

Obviously, when doing this, will need to specify the domain for http requests from the front end (as opposed to when the front end is hosted on the same domain).  This is the difference between making a request to http://localhost:8080/tasks vs. /tasks.

For unfortunate reasons, when developing the front end separately from the backend, the html must come from a different place than if developing as one vertical tech stack.

When developing an integrated application (for production), the app html should be described under the default route ("/").  You can find this in /src/main/scala/com/example/app/Routes/AppRoutes.scala.

When developing separately, the app html should be its own html file.  You can find this in /src/main/webapp/front-end-dist/index.html.

In general, any changes made in index.html should eventually be copy-pasted into the default route.  Hopefully I'll find a fix for this soon because it is horrible.



