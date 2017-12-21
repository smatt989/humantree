import java.io.File

import com.example.app._
import org.scalatra._
import javax.servlet.ServletContext

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.util.ClassLoaderUtil
//import slick.driver.H2Driver.api._

class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext): Unit = {

    context.mount(new SlickApp(), "/*")

  }

  private[this] def isProduction(context: ServletContext) = {
    val envKey = context.environment
    envKey != null && envKey == JettyLauncher.PRODUCTION
  }

  private def closeDbConnection() {

    DBLauncher.cpds.close()
  }

  override def destroy(context: ServletContext) {
  	super.destroy(context)
  	closeDbConnection
  }
}
