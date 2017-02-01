import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import org.apache.spark.sql.hive._


object SparkWordCount {
  def main(args: Array[String]) {
    
    val sc = new SparkContext(new SparkConf().setAppName("Spark Count"))
    
    
    val hiveContext = new HiveContext(sc)
    //val df = sqlContext.table("default.tweets")
    
    import hiveContext.implicits._
    import hiveContext.sql
    hiveContext.sql("ADD JAR /home/cloudera/BigData/BigDataProjekt/bigdata-nfl-hive/target/bigdata-nfl-hive-1.0.0-SNAPSHOT.jar")
    val tokenized = hiveContext.sql("select * from tweets").show() //.collect().flatMap(_.split(" "))
    
    System.out.println(tokenized)
    
    val threshold = args(1).toInt

    // split each document into words
    //val tokenized = sc.textFile(args(0)).flatMap(_.split(" "))

    // count the occurrence of each word
    //val wordCounts = tokenized.map((_, 1)).reduceByKey(_ + _)

    // filter out words with less than threshold occurrences
    //val filtered = wordCounts.filter(_._2 >= threshold)

    // count characters
    //val charCounts = filtered.flatMap(_._1.toCharArray).map((_, 1)).reduceByKey(_ + _)

    //System.out.println(charCounts.collect().mkString(", "))
  }
}