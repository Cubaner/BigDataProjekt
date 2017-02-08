## 1. **Clone git project**

<pre>
$ git clone https://github.com/Cubaner/BigDataProjekt.git
</pre>

## 2. **Start the Flume agent**

    Create the HDFS directory hierarchy for the Flume sink. Make sure that it will be  accessible by the user running the Oozie workflow.  
    
     <pre>
    $ hadoop fs -mkdir /user/flume/tweets
    $ hadoop fs -chown -R flume:flume /user/flume
    $ hadoop fs -chmod -R 770 /user/flume
    $ sudo /etc/init.d/flume-ng-agent start
     </pre>
    
    If using Cloudera Manager, start Flume agent from Cloudera Manager Web UI.

### 3. **Adjust the start time of the Oozie coordinator workflow in job.properties**

Navigieren zum Github-Ordner
<pre>
$ cd BigDataProjekt/
</pre>

Erstellen der notwendigen, lokalen Ordner
<pre>
$ mkdir /bigdata-nfl-oozie/oozie-workflows/lib
</pre>

Kopieren der dataprocessing-SNAPSHOT.jar des processing Projektes fuer den Spark Job in "bigdata-nfl-oozie/oozie-workflows/lib/"
<pre>
$ cp BigDataProjekt/bigdata-nfl-dataprocessing/target/bigdata-nfl-dataprocessing-1.0.0-SNAPSHOT.jar ~/BigData/BigDataProjekt/bigdata-nfl-oozie/oozie-workflows/lib/
</pre>


Kopieren der hive-site.xml in den oozie-workflows Ordner
<pre>
$ sudo cp /etc/hive/conf/hive-site.xml ~/BigDataProjekt/bigdata-nfl-oozie/
$ sudo chown cloudera:cloudera ~/BigDataProjekt/bigdata-nfl-oozie/hive-site.xml
</pre>


Kopieren des bigdata-nfl-oozie Ordners in HDFS
<pre>
$ hadoop fs -put ~/BigDataProjekt/bigdata-nfl-oozie /user/cloudera/bigdata-nfl-oozie
</pre>

Erstellen des oozie-workflows Verzeichnisses im HDFS
<pre>
hdfs dfs -mkdir /user/cloudera/oozie-workflows
</pre>


### Kopieren des oozie-workflows (unterordner von oozie-workflows) in HDFS Order oozieworkflows

hdfs dfs  -copyFromLocal ~/BigDataProjekt/bigdata-nfl-oozie/oozie-workflows /user/cloudera/oozie-workflows/



### Lokale Oozie-Dateien in /user/cloudera/oozie-workflows/oozie-workflows/ kopieren

hdfs dfs -copyFromLocal ~/BigDataProjekt/bigdata-nfl-oozie/hive-action.xml /user/cloudera/oozie-workflows/oozie-workflows/

hdfs dfs -copyFromLocal ~/BigDataProjekt/bigdata-nfl-oozie/hive-site.xml/user/cloudera/oozie-workflows/oozie-workflows/

hdfs dfs -copyFromLocal ~/BigDataProjekt/bigdata-nfl-oozie/job.properties /user/cloudera/oozie-workflows/oozie-workflows/

hdfs dfs -copyFromLocal ~/BigDataProjekt/bigdata-nfl-oozie/coord-app.xml /user/cloudera/oozie-workflows/oozie-workflows/

hdfs dfs -copyFromLocal ~/BigDataProjekt/bigdata-nfl-oozie/add_partition.q /user/cloudera/oozie-workflows/oozie-workflows/

### 4. **Start the Oozie coordinator workflow**
    
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


## 2. **Configure Hive**

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

## 3. **Added Spark Resources**


## 4. **Configure HBASE**

<pre>
$ create "hashtags", { NAME => "hashtag_family", VERSIONS => 3 }
</pre>

## 5. **Set up the tomcat**

Download Tomcat 8 and added the following wars to Tomcat
(https://tomcat.apache.org/download-80.cgi)

<pre>
--bigdata-nfl-hbase-Interface.war
--bigdata-nfl-presentation.war
</pre>


## 6. **Set up Ozzie and started the workflow**



