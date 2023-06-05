package crystailx.finagle.sql

import com.twitter.finagle.mysql.{ Client, OK, Parameter, PreparedStatement, ResultSet, Row }
import com.twitter.util.Future

package object mysql {

  implicit class RichSqlBuilder(val client: Client) extends AnyVal {

    def prepare(statement: SQLStatement): PreparedStatement =
      client.prepare(statement.sql)

    def selectPrepared[T](statement: SQLStatement): (Row => T) => Future[Seq[T]] =
      client.prepare(statement.sql).select[T](statement.params: _*)

    def modifyPrepared(statement: SQLStatement): Future[OK] =
      client.prepare(statement.sql).modify(statement.params: _*)

    def readPrepared(statement: SQLStatement): Future[ResultSet] =
      client.prepare(statement.sql).read(statement.params: _*)

  }

  case class SQLStatement(sql: String, params: Seq[Parameter])

  implicit class Interpolator(sc: StringContext) {

    def sql(params: Any*): SQLStatement = {
      val statement = (sc.parts zip params).foldLeft("") {
        case (statement, (str, SQLStatement(sql, _))) =>
          s"$statement$str$sql"
        case (statement, (str, _)) =>
          s"$statement$str?"
      }
      val paramValues = params.flatMap {
        case SQLStatement(_, params) => params
        case value: Parameter        => Seq(value)
        case value                   => Seq(Parameter.of(value))
      }
      SQLStatement(statement.stripMargin, paramValues)
    }

    def sqlL(params: Any*): SQLStatement = {
      val stmt = (params zip sc.parts.tail).foldLeft(sc.parts.head) {
        case (statement, (SQLStatement(stmtPart, _), part)) =>
          s"$statement$stmtPart$part"
        case (statement, (_, part)) =>
          s"$statement?$part"
      }
      val paramValues = params.flatMap {
        case SQLStatement(_, params) => params
        case value: Parameter => Seq(value)
        case value => Seq(Parameter.of(value))
      }
      SQLStatement(stmt.stripMargin, paramValues)
    }
  }

}
