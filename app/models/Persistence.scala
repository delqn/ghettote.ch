package models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema

object Database extends Schema {
  val usersTable = table[User]("users")

  on(usersTable) {
    u => declare { u.id is(autoIncremented("users_id_seq")) }
  }
}

