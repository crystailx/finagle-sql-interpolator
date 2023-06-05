package crystailx.finagle.sql

import com.twitter.finagle.postgresql.Types.WireValue
import com.twitter.finagle.postgresql.types.{ PgType, ValueWrites }
import com.twitter.finagle.postgresql._
import com.twitter.util.Future

import java.nio.charset.Charset
import scala.language.implicitConversions

package object postgresql {
  type SQLStatement = crystailx.finagle.sql.SQLStatement[Parameter]

  object syntax {
    implicit def parameterOf[T: ValueWrites](value: T): Parameter[T] = Parameter(value)
  }

  implicit class RichSqlBuilder(val client: Client) extends AnyVal {

    def prepare(statement: SQLStatement): PreparedStatement =
      client.prepare(statement.sql)

    def selectPrepared[T](statement: SQLStatement): (Row => T) => Future[Iterable[T]] =
      client.prepare(statement.sql).select[T](statement.params)

    def modifyPrepared(statement: SQLStatement): Future[Response.Command] =
      client.prepare(statement.sql).modify(statement.params)

    def readPrepared(statement: SQLStatement): Future[ResultSet] =
      client.prepare(statement.sql).read(statement.params)

  }

  implicit val emptyValueWrites: ValueWrites[Option[Nothing]] = new ValueWrites[Option[Nothing]] {

    override def writes(tpe: PgType, value: Option[Nothing], charset: Charset): Types.WireValue =
      WireValue.Null

    override def accepts(tpe: PgType): Boolean = true
  }

  implicit class Interpolator(sc: StringContext) {

    def sql(params: Any*): SQLStatement = {
      val statement = (sc.parts zip params).foldLeft("") {
        case (statement, (str, SQLStatement(sql, _))) =>
          s"$statement$str$sql"
        case (statement, (str, _)) =>
          s"$statement$str?"
      }
      val paramValues: Seq[Parameter[_]] = params.flatMap {
        case statement: SQLStatement => statement.params
        case value: Parameter[_]     => Seq(value)
        case _                       => Seq(Parameter(Option.empty[Nothing]))
      }
      SQLStatement(statement.stripMargin, paramValues)
    }
  }

}
