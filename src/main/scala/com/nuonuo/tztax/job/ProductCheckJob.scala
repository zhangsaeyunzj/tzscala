package com.nuonuo.tztax.job

import com.nuonuo.tztax.utils.Constants
import org.apache.logging.log4j.LogManager
import org.apache.spark.sql.SparkSession

import scala.collection.mutable

object ProductCheckJob {

  case class IndustryCode(hyDm: String, code: String)

  case class CompanyCode(djxh: String, code: String)

  def main(args: Array[String]): Unit = {
    //  System.setProperty("user.name", "tax")
    //  System.setProperty("HADOOP_USER_NAME", "tax")

    val logger = LogManager.getLogger(getClass.getName)

    val sparkSession = SparkSession.builder().appName("Product Check")
      .config("spark.broadcast.blockSize", "4096")
      //              .master("local[*]")
      //    .config("spark.ui.view.acls.groups", "hive").config("spark.modify.acls.groups", "hive")
      .enableHiveSupport().getOrCreate()

    import sparkSession.implicits._
    import sparkSession.sql

    val sc = sparkSession.sparkContext

    val hyMap = mutable.HashMap[String, List[String]]()

    val companyCode = sql("select djxh,code from test.qy_code")
    val industryCode = sql("select * from test.hy_code")

    val industryCodes = industryCode.map(x => IndustryCode(x.getAs("hy_dm"), x.getAs("code")))
      .map(x => (x.hyDm, x.code))
      .rdd
      .reduceByKey((x1, x2) => {
        x1 + "_" + x2
      }).map(x => (x._1, x._2.split("_").toList))

    industryCodes.collect().foreach(x => {
      hyMap += x._1 -> x._2
    })

    val map = sc.broadcast(hyMap)

    val companyCodes = companyCode.map(x => CompanyCode(x.getAs("djxh"), x.getAs("code")))
      .map(x => (x.djxh, x.code))
      .rdd
      .reduceByKey((x1, x2) => {
        x1 + "_" + x2
      })
      .map(x => (x._1, x._2.split("_").toArray))

    val result = companyCodes.mapValues(m => {
      var startTime = System.currentTimeMillis()
      val hyMap = map.value
      val hyKeyList = hyMap.keySet.toList
      var endTime = System.currentTimeMillis()
      println("hymap加载耗时：" + (endTime - startTime))
      val rowNum = m.length
      val colNum = hyKeyList.length

      startTime = System.currentTimeMillis()
      val matrix = Array.ofDim[Int](rowNum, colNum)
      for (i <- 0 until rowNum) {
        for (j <- 0 until colNum) {
          matrix(i)(j) = if (hyMap.get(hyKeyList(j)).get.contains(m(i))) 1 else 0
        }
      }
      endTime = System.currentTimeMillis()
      println("矩阵生成耗时：" + (endTime - startTime))
      startTime = System.currentTimeMillis()
      var max = 0
      var index = 0
      for (y <- 0 until colNum) {
        var sum = 0
        for (x <- 0 until rowNum) {
          sum += matrix(x)(y)
        }
        if (sum > max) {
          index = y
          max = sum
        }
      }
      hyKeyList(index)
      endTime = System.currentTimeMillis()
      println("矩阵处理耗时：" + (endTime - startTime))
    })

    result.saveAsTextFile(Constants.COMPANY_INDUSTRY_CHECK_RESULT)

    sparkSession.stop()
  }
}
