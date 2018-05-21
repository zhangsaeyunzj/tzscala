package com.nuonuo.tztax.job

import org.apache.spark.sql.SparkSession

import scala.collection.mutable

/**
  * Created by songfang on 2018/5/6.
  */
object HyCheck {

  case class HyCode(hyDm: String, code: String)

  case class QyCode(djxh: String, code: String)

  case class HyCheck(djxh: String, hy_dm: String, num: Int)

  val spark = SparkSession.builder()
    .appName("hy check analyse")
    .master("local[*]")
    .enableHiveSupport()
    .getOrCreate()

  import spark.implicits._
  import spark.sql

  def main(args: Array[String]): Unit = {
    val qyCode = sql("select djxh,code from test.qy_code")
    val hyCode = sql("select * from test.hy_code")

    val map = mutable.HashMap[String, List[String]]()
    val hyBm = hyCode.map(x => HyCode(
      x.getAs("hy_dm")
      , x.getAs("code")
    )).map(x => (x.hyDm, x.code)).rdd
      .groupByKey()
      .collect()
      .map(x => {
        map += x._1 -> x._2.toList
      }).reverse.take(1)

    val list = hyBm.toList
    val hyMap = list(0)
    //    println(hyMap)

    qyCode.map(x => QyCode(
      x.getAs("djxh"),
      x.getAs("code"))
    ).map(x => (x.djxh, x.code)).rdd
      //      .filter(_._1 == "10113300000046366793")
      .groupByKey()
      .mapValues(x => {
        var tmp = 0
        var list: List[(String, Int)] = Nil
        val rownum = x.toList.length
        val column = hyMap.keySet.toList.length
        val matrix: Array[Array[Double]] = Array.ofDim[Double](rownum, column)
        for (i <- 0 until rownum) {
          for (j <- 0 until column) {
            matrix(i)(j) = if (hyMap.get(hyMap.keySet.toList(j)).get.contains(x.toList(i))) 1 else 0
          }
        }
        for (j <- 0 until column) {
          for (i <- 0 until rownum) {
            tmp += matrix(i)(j).toInt
          }
          list = list :+ (hyMap.keySet.toList(j).toString, tmp)
          tmp = 0
        }
        list
      }).flatMapValues(x => {
      for (i <- 0 until x.length) yield x(i)
    }).filter(x => x._2._2.toInt != 0)
      .map(x => HyCheck(x._1, x._2._1, x._2._2.toInt))
      .toDF()
      .createOrReplaceTempView("hy_check1")

    sql("drop table if exists test.hy_check1")
    sql(
      """
        |create table test.hy_check1
        |as select * from hy_check1
      """.stripMargin)
  }
}
