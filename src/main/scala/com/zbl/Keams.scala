package com.zbl

import com.zbl.WordCount.conf
import org.apache.spark.{SparkConf, SparkContext}

import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors

object Keams {
  def main(args: Array[String]): Unit = {
    System.setProperty("user.name", "root");
    System.setProperty("HADOOP_USER_NAME", "root");
    val path = "hdfs://hb:9000/user/zbl/kmeans_data.txt"

    val conf = new SparkConf()
      .setAppName("k-means Demo")
      //    .setMaster("spark://hm:7077")
      .setMaster("local[2]")
      .set("spark.executor.memory", "512m")
      .set("spark.driver.memory", "256m")
      .set("spark.cores.max", "4")
      .set("spark.driver.host", "172.30.4.10")
//      .set("es.nodes", "192.168.210.240")
    //    .setJars(List("target/scala-2.11/tzscala_2.11-0.1.jar"))


    val sc = new SparkContext(conf)
    // Load and parse the data
//    val data = sc.textFile("data/mllib/kmeans_data.txt")
    val data = sc.textFile(path)
    val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()

    // Cluster the data into two classes using KMeans
    val numClusters = 2
    val numIterations = 20
    val clusters = KMeans.train(parsedData, numClusters, numIterations)

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    val WSSSE = clusters.computeCost(parsedData)
    println(s"Within Set Sum of Squared Errors = $WSSSE")

    // Save and load model
//    clusters.save(sc, "target/org/apache/spark/KMeansExample/KMeansModel")
//    val sameModel = KMeansModel.load(sc, "target/org/apache/spark/KMeansExample/KMeansModel")
  }
}
