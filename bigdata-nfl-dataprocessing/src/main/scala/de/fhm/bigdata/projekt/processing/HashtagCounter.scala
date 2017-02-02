import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import org.apache.spark.sql.hive._

import org.apache.hadoop.hbase.{HBaseConfiguration, HTableDescriptor}
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.HTable;

object HashtagCounter {
  def main(args: Array[String]) {

    val sc = new SparkContext(new SparkConf().setAppName("Spark Count"))


    val hiveContext = new HiveContext(sc)
    //val df = sqlContext.table("default.tweets")

    import hiveContext.implicits._
    import hiveContext.sql
    hiveContext.sql("ADD JAR /home/cloudera/BigData/BigDataProjekt/bigdata-nfl-hive/target/bigdata-nfl-hive-1.0.0-SNAPSHOT.jar")
    
    val trimmed_tweets = hiveContext.sql("select entities.hashtags from tweets").map(row => row.getList(0).toString()).filter(_.nonEmpty).flatMap(_.split(","))
    
   val trimmed_tweets2 = trimmed_tweets.map { x => x.replace("[","") }
   val trimmed_tweets3 = trimmed_tweets2.map { x => x.replace("]","") }
    
    trimmed_tweets3.map(t => "Tweettext: " + t).collect().foreach(println)
    val wordCounts = trimmed_tweets3.map((_, 1)).reduceByKey(_ + _)
    
    val filtered = wordCounts.filter(_._2 >= 3)
    
    //Die Map durchgehend in HBASE speichern
    
    System.out.println(filtered.collect().mkString(", "))
  
    val conf = HBaseConfiguration.create()
    val tableName = "hashtags"
       
    //put data into table
    val myTable = new HTable(conf, tableName);
        filtered.collect().foreach(y => {
       val a = y.toString().split(",")(0)
       val b = y.toString().split(",")(1)
    	 //if (a != ""
      //var p = new Put();
      var p = new Put(new String(a + b).getBytes()); //hashtag und timestamp?
	  p.add("hashtag_family".getBytes(), "hashtag".getBytes(), 
						a.getBytes());
	  p.add("hashtag_family".getBytes(), "counter".getBytes(), 
						b.getBytes());
		p.add("hashtag_family".getBytes(), "timestamp".getBytes(), new String(
						"value ").getBytes());
	  myTable.put(p);
        } )
	myTable.flushCommits();
   
    
  }
}
