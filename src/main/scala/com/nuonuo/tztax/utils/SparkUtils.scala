package com.nuonuo.tztax.utils

import org.apache.spark.{SparkConf, SparkContext}

class SparkUtils private {
  def initSparkContent(conf: SparkConf): SparkContext = {
    new SparkContext(conf)
  }
}

object SparkUtils {
  val instance = new SparkUtils

  def initSparkEs(master: String, appName: String, nodes: String, port: String): SparkContext = {
    val conf = new SparkConf().setMaster(master).setAppName(appName)
    conf.set("es.nodes", nodes)
    conf.set("es.port", port)
    instance.initSparkContent(conf)
  }

  def initSpark(conf: SparkConf): SparkContext = {
    instance.initSparkContent(conf)
  }

  def initSpark(master: String, appName: String): SparkContext = {
    val conf = new SparkConf().setMaster(master).setAppName(appName)
    instance.initSparkContent(conf)
  }
}
