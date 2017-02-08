## 1. **Clone git project**

<pre>
$ git clone https://github.com/Cubaner/BigDataProjekt.git
cd Bigdata projekt
mvn clean install
</pre>

## 2. **Configure the Flume agent**

Create the HDFS directory hierarchy for the Flume sink. Make sure that it will be accessible by the user running the Oozie workflow.  

<pre>
$ hdfs dfs mkdir /user/cloudera/tweets
$ hadoop fs -chown -R flume:flume /user/flume
$ hadoop fs -chmod -R 770 /user/flume
$ sudo /etc/init.d/flume-ng-agent start
</pre>

If using Cloudera Manager, start Flume agent from Cloudera Manager Web UI.

Configure the Agent and add your twitter Key and Token

<pre>
AgentName = Twitter Agent

TwitterAgent.sources = Twitter
TwitterAgent.channels = MemChannel
TwitterAgent.sinks = HDFS

TwitterAgent.sources.Twitter.type = de.fhm.bigdata.projekt.dataimport.TwitterSource
TwitterAgent.sources.Twitter.channels = MemChannel
TwitterAgent.sources.Twitter.consumerKey = <--->
TwitterAgent.sources.Twitter.consumerSecret = <---> #specific user Tokens 
TwitterAgent.sources.Twitter.accessToken = <--->
TwitterAgent.sources.Twitter.accessTokenSecret = <--->
TwitterAgent.sources.Twitter.keywords = nfl, nflran, gopacksgo, hawks, probowl, rannfl, superbowl

TwitterAgent.sinks.HDFS.channel = MemChannel
TwitterAgent.sinks.HDFS.type = hdfs
TwitterAgent.sinks.HDFS.hdfs.path = hdfs://quickstart.cloudera:8020/user/cloudera/tweets/%Y/%m/%d/%H/
TwitterAgent.sinks.HDFS.hdfs.fileType = DataStream
TwitterAgent.sinks.HDFS.hdfs.writeFormat = Text
TwitterAgent.sinks.HDFS.hdfs.batchSize = 100
TwitterAgent.sinks.HDFS.hdfs.rollSize = 10000
TwitterAgent.sinks.HDFS.hdfs.rollCount = 10000
TwitterAgent.sinks.HDFS.hdfs.rollInterval = 600

TwitterAgent.channels.MemChannel.type = memory
TwitterAgent.channels.MemChannel.capacity = 10000
TwitterAgent.channels.MemChannel.transactionCapacity = 100
</pre>

Add bigdata-nfl-dataimport-1.0.0-SNAPSHOT.jar to

<pre>
/var/lib/flume-ng/plugins.d/twitter-streaming/lib/bigdata-nfl-dataimport-1.0.0-SNAPSHOT.jar
/usr/lib/flume-ng/plugins.d/twitter-streaming/lib/bigdata-nfl-dataimport-1.0.0-SNAPSHOT.jar
</pre>

## 3. **Configure Oozie and create the relevant directory in HDFS**

Copy the dataprocessing-SNAPSHOT.jar (@ ~ you have to insert your own directory path)
<pre>
$ cp BigDataProjekt/bigdata-nfl-dataprocessing/target/bigdata-nfl-dataprocessing-1.0.0-SNAPSHOT.jar ~/BigData/BigDataProjekt/bigdata-nfl-oozie/oozie-workflows/lib/
</pre>

Copy des bigdata-nfl-oozie Ordners in HDFS
<pre>
$ hdfs dfs -put ~/BigDataProjekt/bigdata-nfl-oozie /user/cloudera/bigdata-nfl-oozie
</pre>

Create oozie-workflows directory in HDFS
<pre>
$ hdfs dfs -mkdir /user/cloudera/oozie-workflows
</pre>

Copy oozie-workflows (directory below with same name and necessary oozie data) into HDFS folder oozieworkflows
<pre>
$ hdfs dfs  -copyFromLocal ~/BigDataProjekt/bigdata-nfl-oozie/oozie-workflows/oozie-workflows /user/cloudera/oozie-workflows/
</pre>


## 4. **Configure Hive**

Add Jar to /usr/lib/hive/lib/bigdata-nfl-hive-1.0.0-SNAPSHOT.jar

$ <pre>
CREATE EXTERNAL TABLE tweets (
  id BIGINT,
  created_at STRING,
  source STRING,
  favorited BOOLEAN,
  retweeted_status STRUCT<
    text:STRING,
    user:STRUCT<screen_name:STRING,name:STRING>,
    retweet_count:INT>,
  entities STRUCT<
    urls:ARRAY<STRUCT<expanded_url:STRING>>,
    user_mentions:ARRAY<STRUCT<screen_name:STRING,name:STRING>>,
    hashtags:ARRAY<STRUCT<text:STRING>>>,
  entities.hashtags STRING,
  text STRING,
  user STRUCT<
    screen_name:STRING,
    name:STRING,
    friends_count:INT,
    followers_count:INT,
    statuses_count:INT,
    verified:BOOLEAN,
    utc_offset:INT,
    time_zone:STRING>,
  in_reply_to_screen_name STRING
) 
PARTITIONED BY (datehour INT)
ROW FORMAT SERDE 'de.fhm.bigdata.projekt.hive.JSONSerDe'
LOCATION '/user/cloudera/tweets';
</pre>

## 5. **Added Spark Resources**




## 6. **Configure HBASE**

<pre>
$ create "hashtags", { NAME => "hashtag_family", VERSIONS => 3 }
</pre>

## 7. **Set up the tomcat**

Download Tomcat 8 and added the following wars to Tomcat
(https://tomcat.apache.org/download-80.cgi)

<pre>
--bigdata-nfl-hbase-Interface.war
--bigdata-nfl-presentation.war
</pre>


## 8. **Start the workflow coordinator**

<pre>
oozie job -run -oozie http://quickstart.cloudera:11000/oozie -config oozie-workflows/job.properties
</pre>




