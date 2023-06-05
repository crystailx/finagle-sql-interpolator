package crystailx.finagle.sql.postgresql

import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class InterpolatorSpec extends AnyFlatSpec with Matchers with MockFactory {
  it must "convert to prepared statement" in {
    val num1 = 99
    val num2 = -77
    val num3 = 0
    val sql1 = sql"SELECT * FROM aoa_send_history WHERE 1 = $num1"
    val sql2 = sql"SELECT dataTime FROM ($sql1) as history WHERE 1 = $num2"
    val sql3 = sql"SELECT * FROM aoa_send_history WHERE $num3 IN ($sql2)"
    println(sql1)
    println(sql2)
    println(sql3)
    sql1.sql mustBe "SELECT * FROM aoa_send_history WHERE 1 = ?"
    sql1.params must contain theSameElementsInOrderAs Seq(num1)
    sql2.sql mustBe "SELECT dataTime FROM (SELECT * FROM aoa_send_history WHERE 1 = ?) as history WHERE 1 = ?"
    sql2.params must contain theSameElementsInOrderAs Seq(num1, num2)
    sql3.sql mustBe "SELECT * FROM aoa_send_history WHERE ? IN (SELECT dataTime FROM (SELECT * FROM aoa_send_history WHERE 1 = ?) as history WHERE 1 = ?"
    sql3.params must contain theSameElementsInOrderAs Seq(num3, num1, num2)
  }
}
