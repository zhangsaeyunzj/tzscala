package com.zbl

import com.nuonuo.tztax.utils.{Constants, SparkUtils}

object Test extends App {
  val sc = SparkUtils.initSpark("yarn","Broadcast Test")

  val broadcaster = sc.broadcast(Array(1,2,3))

  val pRDD = sc.textFile(Constants.PRODUCT_PATH)

  pRDD.map(x=>{
    val array = broadcaster.value
    println(array)
  })
}
