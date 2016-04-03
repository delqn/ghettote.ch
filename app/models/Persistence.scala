package models

import play.api.db._
import play.api.Play.current

import org.squeryl.PrimitiveTypeMode._
import org.squeryl._
import org.squeryl.Schema

case class User(id: Long,
                email: String,
                password: String,
                fullname: String,
                isadmin: Boolean) extends KeyedEntity[Long]

object Database extends Schema {
  val usersTable = table[User]("users")

  on(usersTable) {
    u => declare { u.id is(autoIncremented("users_id_seq")) }
  }
}

object User {

  // returns existing user by email address, or null if not exists
  def apply(email: String): User = {
    inTransaction {
      findByEmailQ(email).headOption.getOrElse(null)
    }
  }

  def allQ: Query[User] = from(Database.usersTable) {
    user => select(user)
  }

  def findByEmailQ(email: String): Query[User] = from(allQ) {
    user => where(user.email === email).select(user)
  }

  def findAll(): List[User] = {
    inTransaction {
      allQ.toList
    }
  }

  def create(user: User) {
    inTransaction {
      Database.usersTable.insert(user)
    }
  }
}
