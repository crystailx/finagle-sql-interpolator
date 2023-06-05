package crystailx.finagle

package object sql {
  case class SQLStatement[P[_]](sql: String, params: Seq[P[_]])
}
