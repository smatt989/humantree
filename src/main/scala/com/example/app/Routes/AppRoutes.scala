package com.example.app.Routes

import com.example.app.{AuthenticationSupport, CookieStrategy, SlickRoutes}
import org.scalatra.Ok

trait AppRoutes extends SlickRoutes with AuthenticationSupport{


  get("/") {
    val authenticated = new CookieStrategy(this).checkAuthentication()

    <html>
      <head>
        <link rel='stylesheet' href='/front-end/dist/react-datepicker.min.css' />
        <link rel="stylesheet" href="/front-end/dist/main.css" />
        </head>
        <body>
          <div id="app"></div>
          <script>
            var CONFIG = new Object();
            CONFIG.auth = {authenticated.isDefined};
          </script>
          <script src="https://d3js.org/d3.v3.min.js"></script>
          <script src='https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.17.1/moment.min.js'></script>
          <script src="/front-end/dist/bundle.js"></script>
        </body>
      </html>
  }

  get("/google235447c74ae87b07.html ") {
    Ok{"google-site-verification: google235447c74ae87b07.html"}
  }

}
