package controllers

import play.api.mvc.Action
import play.api.mvc.Controller

import models.{Database, User}

object Application extends Controller {

  def index = Action {
    request =>
      val session = request.session
      session.get("connected").map { email =>
        val user = new User(0, email, "ppp", "Delyan Raychev", false) // TODO: get the user from DB
        Ok(views.html.index(Some(user), "Hello, " + user.fullname + "!"))
      }.getOrElse {
        Ok(views.html.index(None, "Not logged in"))
      }
  }

  def api = Action {
    System.out.println("&&&&&&&&&&&&&")
    val u = new User(0, "delyan@raychev.org", "ppp", "Delyan Raychev", true)
    User.create(u)
    System.out.println(u)
    Ok("{}").as("application/json")
  }

  def translit = Action {
    request => Ok(views.html.translit("hey"))
  }
}
