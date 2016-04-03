package models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl._

case class User(id: Long,
                email: String,
                password: String,
                fullname: String,
                isadmin: Boolean) extends KeyedEntity[Long]

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