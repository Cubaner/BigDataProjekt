import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import org.apache.spark.sql.hive._

import org.apache.hadoop.hbase.{HBaseConfiguration, HTableDescriptor}
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.HTable;

import org.apache.hadoop.hbase.client.{HBaseAdmin, Result}
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.io.ImmutableBytesWritable


object CalculateTeamRating {
  def main(args: Array[String]) {

    val sc = new SparkContext(new SparkConf().setAppName("Calculate Team Rating"))
    
    val conf = HBaseConfiguration.create()
    val tableName = "teams"
    
    conf.set(TableInputFormat.INPUT_TABLE, tableName)

    val admin = new HBaseAdmin(conf)
    if (!admin.isTableAvailable(tableName)) {
      val tableDesc = new HTableDescriptor(tableName)
      admin.createTable(tableDesc)
    }

    val hBaseRDD = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result])
    println("Number of Records found : " + hBaseRDD.count())
    println("Was alles gefunden wurde: " + hBaseRDD)
    
    sc.stop()
    
    

    val threshold = args(1).toInt
    var timestamp = args(0).toString
    
    val wordCounts = hBaseRDD.map((_, 1)).reduceByKey(_ + _)
    
    System.out.println(wordCounts.collect().mkString(", "))
  
       
    //put data into table
    val myTable = new HTable(conf, tableName);
        wordCounts.collect().foreach(y => {
       val a = y.toString().split(",")(0).replace("(", "")
       val b = y.toString().split(",")(1).replace(")", "")
    	 //if (a != ""
      //var p = new Put();
      var p = new Put(new String(a + b + timestamp).getBytes()); //hashtag und timestamp?
	  p.add("team_family".getBytes(), "hashtag".getBytes(), 
						a.getBytes());
	  p.add("team_family".getBytes(), "counter".getBytes(), 
						b.getBytes());
		p.add("team_family".getBytes(), "timestamp".getBytes(), timestamp.getBytes());
	  myTable.put(p);
        } )
	myTable.flushCommits();
   
    
  }
}
