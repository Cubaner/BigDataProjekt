# 1. **Start the Flume agent**

    Create the HDFS directory hierarchy for the Flume sink. Make sure that it will be  accessible by the user running the Oozie workflow.  
    
     <pre>
    $ hadoop fs -mkdir /user/flume/tweets
    $ hadoop fs -chown -R flume:flume /user/flume
    $ hadoop fs -chmod -R 770 /user/flume
    $ sudo /etc/init.d/flume-ng-agent start
     </pre>
    
    If using Cloudera Manager, start Flume agent from Cloudera Manager Web UI.

## 2. **Adjust the start time of the Oozie coordinator workflow in job.properties**

You will need to modify the `job.properties` file, and change the `jobStart`, `jobEnd`, and `initialDataset` parameters. The start and end times are in UTC, because the version of Oozie packaged in CDH4 does not yet support custom timezones for workflows. The initial dataset should be set to something before the actual start time of your job in your local time zone. Additionally, the `tzOffset` parameter should be set to the difference between the server's timezone and UTC. By default, it is set to -8, which is correct for US Pacific Time.

## 3. **Start the Oozie coordinator workflow**
    
    <pre>$ oozie job -oozie http://&lt;oozie-host&gt;:11000/oozie -config oozie-workflows/job.properties -run</pre>
=======
### **Configure THE FLUME AGENT**


Create a HDFS Folder /user/cloudera/tweets and give flume user specific rights

<pre>
$ hdfs dfs mkdir /user/cloudera/tweets
</pre>

Configure the agent the Agent and add your twitter Key and Token

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

Added bigdata-nfl-dataimport-1.0.0-SNAPSHOT.jar to

<pre>
/var/lib/flume-ng/plugins.d/twitter-streaming/lib/bigdata-nfl-dataimport-1.0.0-SNAPSHOT.jar
/usr/lib/flume-ng/plugins.d/twitter-streaming/lib/bigdata-nfl-dataimport-1.0.0-SNAPSHOT.jar
</pre>


### 2. **Configure Hive**

Add Jar to /usr/lib/hive/lib/bigdata-nfl-hive-1.0.0-SNAPSHOT.jar

<pre>
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

### 3. **Added Spark Resources**


### 4. **Configure HBASE**

<pre>
create "hashtags", { NAME => "hashtag_family", VERSIONS => 3 }
</pre>

### 5. **Set up the tomcat**

Download Tomcat 8 and added the following wars to Tomcat

<pre>
--bigdata-nfl-hbase-Interface.war
--bigdata-nfl-presentation.war
</pre>


### 6. **Set up Ozzie and started the workflow**

>>>>>>> 2388360edbc7daf06161cf4870ce2f8d0bccc78f

