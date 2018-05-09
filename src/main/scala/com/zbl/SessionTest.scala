package com.zbl

import org.apache.spark.sql.SparkSession


case class Person(val name: String, val age: Long)

object SessionTest extends App {
  val warehouse = "file:///D://sparkwarehouse"

  val sparkSession = SparkSession.builder().appName("Spark SQL basic example").master("local[2]")
    .config("spark.sql.warehouse.dir", warehouse).getOrCreate()

  sparkSession.conf.set("spark.sql.shuffle.partitions", 6)
  sparkSession.conf.set("spark.executor.memory", "2g")


  val df = sparkSession.read.json("file:///C:\\Users\\zhangbl\\Desktop\\people.json")

  df.show()

  df.printSchema()

  df.select("name").show()

  import sparkSession.implicits._

  df.select($"name", $"age" + 1).show()

  df.createOrReplaceTempView("people")

  val sqlDF = sparkSession.sql("select * from people")

  sqlDF.show()

  val caseClassDS = Seq(Person("Andy",32)).toDS()

  caseClassDS.show()


  val peopleDS = sparkSession.read.json("file:///C:\\Users\\zhangbl\\Desktop\\people.json").as[Person]

  peopleDS.show()


}
