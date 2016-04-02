package controllers

import play.api.GlobalSettings
import play.api.mvc.Action
import play.api.mvc.Controller

import models.User

object Application extends Controller {

  def index = Action {
    request =>
      val session = request.session
      session.get("connected").map { email =>
        val user = User(email)
        Ok(views.html.index(Some(user), "Hello, " + user.fullname + "!"))
      }.getOrElse {
        Ok(views.html.index(None, "Not logged in"))
      }
  }

  def api = Action {
    Ok("{}").as("application/json")
  }

  def translit = Action {
    request => Ok(views.html.translit("hey"))
  }
}
