package com.nuonuo.tztax.job

import com.nuonuo.help.AnalyzerHelper
import com.nuonuo.tztax.utils.{Constants, SparkUtils}
import org.apache.logging.log4j.LogManager
import org.apache.spark.rdd.RDD

object CodeMatchJob extends App {
  System.setProperty("user.name", "tax")
  System.setProperty("HADOOP_USER_NAME", "tax")

  val logger = LogManager.getLogger(getClass.getName)

  logger.info("start code match process...")

  val sc = SparkUtils.initSpark("yarn","hwmc_check")

  val pRDD = sc.textFile(Constants.PRODUCT_PATH)

  val cRDD = matchCode(pRDD)

  cRDD.saveAsTextFile(Constants.PRODUCT_RESULT_PATH)

  def matchCode(pRDD: RDD[String]): RDD[String] = {
    pRDD.map(p => {
      val helper = AnalyzerHelper.getInstance()
      val result = helper.getProductCode(p, null, null, null)
      var taxCode = ""
      var taxName = ""
      if (null != result) {
        taxCode = result.getTaxCode
        taxName = result.getName
      } else if (null == taxCode || p.contains("金税盘") || p.contains("报税盘") || p.contains("税控盘")) {
        taxCode = "0000000"
        taxName = "other"
      }
      p + "\007" + taxCode
    })
  }
}
