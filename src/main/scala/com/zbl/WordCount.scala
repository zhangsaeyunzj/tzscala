package com.zbl

import com.nuonuo.tztax.utils.SparkUtils
import org.apache.spark._
import org.elasticsearch.spark._

class Item(num: String, code: String, l: Double, f: Int) extends Serializable {
  val taxNum = num
  val taxCode = code
  val levy = l
  val flag = f
}

case class Result(taxNum: String, taxCode: String, levy: Double)

object WordCount extends App {

  System.setProperty("user.name", "root");
  System.setProperty("HADOOP_USER_NAME", "root");
  val path = "hdfs://hb:9000/user/zbl/jxbd.txt"

  val conf = new SparkConf()
    .setAppName("SparkDemo")
//    .setMaster("spark://hm:7077")
          .setMaster("local[2]")
    .set("spark.executor.memory", "512m")
    .set("spark.driver.memory", "256m")
    .set("spark.cores.max", "4")
    .set("spark.driver.host", "172.30.4.10")
    .set("es.nodes", "192.168.210.240")
//    .setJars(List("target/scala-2.11/tzscala_2.11-0.1.jar"))

  val sc = new SparkContext(conf)
  val lines = sc.textFile(path)
  lines.collect().foreach(println)
  val is = lines.map(l => {
    val s = l.split("\t")
    val i: Item = new Item(s(0), s(1), s(2).trim().toDouble, s(3).trim().toInt)
    (s(0) + "_" + s(1), i)
  })

  //按照税号+税收分类编码reduce
  val iss = is.reduceByKey((item1, item2) => {
    val l1 = if (item1.flag == 0) -item1.levy else item1.levy
    val l2 = if (item2.flag == 0) -item2.levy else item2.levy
    new Item(item1.taxNum, item1.taxCode, l1 + l2, 2)
  })

  val res = iss.map(s => {
    val taxNum = s._2.taxNum
    val taxCode = s._2.taxCode
    val levy = s._2.levy
    Result(taxNum, taxCode, levy)
  })

  res.collect().foreach(println)
//  res.saveToEs("spark/docs")

  //  iss.saveAsTextFile("hdfs://hm:9000/user/zbl/result")
  //persistence to hdfs

  //TODO 在按照税号reduce即可

  //persistence to hdfs
}
