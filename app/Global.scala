import play.api._
//import play.api.mvc._
import play.api.db.DB
import org.squeryl._
import org.squeryl.adapters.PostgreSqlAdapter
import play.api.Logger
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    SessionFactory.concreteFactory = Some(() => Session.create(DB.getConnection()(app), new PostgreSqlAdapter))
  }
}
