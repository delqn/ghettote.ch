package controllers

import play.api.Logger
import play.api.Play.current
import play.api.libs.ws.WS
import play.api.mvc._
import models.User
import scala.util.matching.Regex
import com.restfb.DefaultFacebookClient
import scala.collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.libs.json.Json

object Facebook extends Controller {

  val config = play.api.Play.configuration
  val appId = config.getString("facebook.app_id").get
  val appSecret = config.getString("facebook.app_secret").get
  val baseRedirectUrl = config.getString("facebook.redirect_url").get
  val scope = config.getString("facebook.scope").get

  def redirectUrl(extension: Call) = {
    val withParameters = extension.toString
    val indexQuestionmark = withParameters.indexOf('?')
    val extensionString = withParameters.substring(0, indexQuestionmark)
    "https://www.facebook.com/dialog/oauth?client_id=" + appId +
      "&redirect_uri=" + baseRedirectUrl + extensionString +
      "&scope=" + scope
  }

  val loginRedirect = redirectUrl(controllers.routes.Facebook.login2(""))

  def login = Action {
    Redirect(loginRedirect)
  }

  def loginWithParams(ref: Option[String], code: Option[String]) = Action {
    Redirect(loginRedirect)
  }

  def canvasTrash(trash: Option[String]) = Action {
    request =>
      println(request.rawQueryString)
      Redirect(controllers.routes.Facebook.login)
  }

  def canvas = Action {
    request =>
      Redirect(controllers.routes.Facebook.login)
  }

  def login2(code: String) = Action {
    if (!code.isEmpty) {
      loginWithCode(code, loginRedirect)
    } else {
      Redirect(controllers.routes.Facebook.login)
    }
  }

  def loginWithCode(code: String, redirectUrl: String): Result = {
    doWithAccessToken(code, loginRedirect) {
      (accessToken, expires) =>
        val facebookClient = new DefaultFacebookClient(accessToken)
        val fbUser = facebookClient.fetchObject("me", classOf[com.restfb.types.User])
        val user = getOrCreateUser(fbUser)
        Redirect(controllers.routes.Application.index).withSession("connected" -> user.email)
    }
  }

  def doWithAccessToken(code: String,
                        redirectUrl: String,
                        currentAccessToken: Option[String] = null,
                        currentATExpiration: Option[String] = null)
    //TODO(delyan): return type?
    (accessTokenHandler: ((String, String) => Result)): Result = {
    if(currentAccessToken != null && currentAccessToken != None) {
      return accessTokenHandler(currentAccessToken.get, null)
    }
    val accessTokenUrl = "https://graph.facebook.com/oauth/access_token?" +
      "client_id=" + appId +
      "&client_secret=" + appSecret +
      "&code=" + code +
      "&redirect_uri=" + redirectUrl

    implicit val context = scala.concurrent.ExecutionContext.Implicits.global
    val resultFuture = WS.url(accessTokenUrl).get().map {
      response => (response.status, response.body)
    }

    val accessTokenResponse = Await.result(resultFuture, 5 seconds)
    val status = accessTokenResponse._1
    val accessTokenBody = accessTokenResponse._2

    if(status!=200) {
      val json = Json.parse(accessTokenBody)
      var error = (json \ "error" \ "message").asOpt[String].get
      Logger.warn(s"doWithAccesstoken got an error from Facebook -> " +
        s"status=$status  error=$error")
      return accessTokenHandler(null, null)
    }

    val regex = new Regex("access_token=(.*)&expires=(.*)")
    accessTokenBody match {
      case regex(accessToken, expires) => {
        Console.println(s"accessToken: $accessToken")
        Console.println(s"expries: $expires")
        accessTokenHandler(accessToken, expires)
      }
    }
  }

  def getOrCreateUser(fbUser: com.restfb.types.User): User = {
    val facebookUsername = fbUser.getUsername
    val user = User(fbUser.getEmail)
    if (user == null) {
      createFacebookUser(fbUser.getEmail, facebookUsername, Some(fbUser.getId.toLong), fbUser.getName);
    } else {
      user
    }
  }

  def createFacebookUser(email: String, facebookUsername: String, facebookId: Option[Long], fullName: String): User = {
    User.create(User(0, email, fullName, facebookUsername, facebookId))
    User(email)
  }

  val facebookFriendsRedirect = redirectUrl(controllers.routes.Facebook.listFacebookFriends2(""))

  def listFacebookFriends = Action {
    Redirect(facebookFriendsRedirect)
  }

  def listFacebookFriends2(code: String) = Action {
    implicit request =>
      request.session.get("connected").map { email =>
        val user = User(email)
        val currentAccessToken = request.session.get("accessToken")
        val tokenExpiration = request.session.get("tokenExpiration")
        doWithAccessToken(code, facebookFriendsRedirect, currentAccessToken, tokenExpiration) {
          (accessToken, expires) =>
            if(accessToken == null) {
              Logger.warn(s"listFacebookFriends2 got a $accessToken accessToken; " +
                s"Redirecting to $facebookFriendsRedirect")
              Redirect(facebookFriendsRedirect)
            } else {
              val facebookClient = new DefaultFacebookClient(accessToken)
              val myFriends = facebookClient.fetchConnection("me/friends", classOf[com.restfb.types.User]).getData
              val users = (for(x <- User.findAll()) yield x.facebookid.get).toSet
              val activeFriends = myFriends.filter(friend => users.contains(friend.getId.toLong)).toSeq
              Ok(views.html.listFriends(null, activeFriends)).withSession(
                request.session + ("accessToken", accessToken))
            }
        }
      }.getOrElse {
        Redirect(controllers.routes.Application.login)
      }
  }

  val facebookCheckinsRedirect = redirectUrl(controllers.routes.Facebook.listMyCheckins2(""))

  def listMyCheckins = Action {
    Redirect(facebookCheckinsRedirect)
  }

  def listMyCheckins2(code: String) = Action {
    implicit request =>
      request.session.get("connected").map { email =>
        val user = User(email)
        doWithAccessToken(code, facebookCheckinsRedirect) {
          (accessToken, expires) =>
            if(accessToken == null) {
              Logger.warn(s"listMyCheckins2 got a $accessToken accessToken; " +
                s"Redirecting to $facebookCheckinsRedirect")
              Redirect(facebookCheckinsRedirect)
            } else {
              val facebookClient = new DefaultFacebookClient(accessToken)
              val fbid = user.facebookid.get
              val myCheckins = facebookClient.fetchConnection(s"$fbid/checkins", classOf[com.restfb.types.Checkin]).getData
              Ok(views.html.listMyCheckins(user, myCheckins))
            }
        }
      }.getOrElse {
        Redirect(controllers.routes.Application.login)
      }
  }

  val facebookPhotosRedirect = redirectUrl(controllers.routes.Facebook.listMyPhotos2(""))

  def listMyPhotos = Action {
    Redirect(facebookPhotosRedirect)
  }

  def listMyPhotos2(code: String) = Action {
    implicit request =>
      request.session.get("connected").map { email =>
        val user = User(email)
        doWithAccessToken(code, facebookPhotosRedirect) {
          (accessToken, expires) =>
            if(accessToken == null) {
              Logger.warn(s"listMyPhotos2 got a $accessToken accessToken; " +
                s"Redirecting to $facebookPhotosRedirect")
              Redirect(facebookPhotosRedirect)
            } else {
              val facebookClient = new DefaultFacebookClient(accessToken)
              val fbid = user.facebookid.get
              val myPhotos = facebookClient.fetchConnection(s"$fbid/photos/uploaded?limit=999", classOf[com.restfb.types.Photo]).getData
              Ok(views.html.listMyPhotos(user, myPhotos))
            }
        }
      }.getOrElse {
        Redirect(controllers.routes.Application.login)
      }
  }

}
