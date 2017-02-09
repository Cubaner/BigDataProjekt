# Readme - Big Data Project 2017
## Analyzing NFL-Twitter data by using CDH 5.8

Before you get started with the application, you will first need to install CDH 5.8. Specifically, you will need Hadoop, Flume, Oozie, and Hive. The easiest way to get the core components is to use Cloudera Manager to set up your initial environment. You can download the vm image here: (http://www.cloudera.com/downloads/quickstart_vms/5-8.html) How to set up the configuration will be explained in detail within the following steps.

## 1. **Clone git project and build artifacts**
```
$ git clone https://github.com/Cubaner/BigDataProjekt.git
$ cd BigDataProjekt
$ mvn clean install
```

## 2. **Configure the Flume agent**

Create the HDFS directory hierarchy for the Flume sink.
Make sure that it will be accessible by the user running the Oozie workflow to start the application.  

$ hdfs dfs mkdir /user/cloudera/tweets
$ hadoop fs -chown -R cloudera:cloudera /user/cloudera
$ hadoop fs -chmod -R 770 /user/cloudera
$ sudo /etc/init.d/flume-ng-agent start

Create the Flume agent in Cloudera Manager Web UI
Configure the Agent and add your twitter key and token

- Go to the Flume Service page, click on the ‚Configuration‘ tab and select ‚View and Edit‘
- Select the Agent ‚Default‘
- Set the Agent Name property to ‚TwitterAgent’
- Copy the content of the TwitterAgents below.
- Afterwards click ‚Save Changes‘

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
TwitterAgent.sources.Twitter.keywords = nfl, nflran, gopacksgo, hawks, probowl, rannfl, superbowl, brady, super bowl, mattryan

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



$ cd /
$ /var/lib/flume-ng/plugins.d/twitter-streaming/lib/bigdata-nfl-dataimport-1.0.0-SNAPSHOT.jar
$ /usr/lib/flume-ng/plugins.d/twitter-streaming/lib/bigdata-nfl-dataimport-1.0.0-SNAPSHOT.jar


## 3. **Configure Oozie and create the relevant directory in HDFS**

Copy the dataprocessing-SNAPSHOT.jar into oozie-workflows/lib/ (@ ~ you have to insert your own directory path)

$ cp ~/BigDataProjekt/bigdata-nfl-dataprocessing/target/bigdata-nfl-dataprocessing-1.0.0-SNAPSHOT.jar
~/BigData/BigDataProjekt/bigdata-nfl-oozie/oozie-workflows/lib/


Copy des bigdata-nfl-oozie Ordners in HDFS

$ hdfs dfs -put ~/BigDataProjekt/bigdata-nfl-oozie /user/cloudera/bigdata-nfl-oozie


Create oozie-workflows directory in HDFS

$ hdfs dfs -mkdir /user/cloudera/oozie-workflows


Copy oozie-workflows (directory below with same name and necessary oozie data) into HDFS folder oozieworkflows

$ hdfs dfs  -copyFromLocal ~/BigDataProjekt/bigdata-nfl-oozie/oozie-workflows/oozie-workflows /user/cloudera/oozie-workflows/


## 4. **Configure Hive**

Add hive.jar to /usr/lib/hive/lib/bigdata-nfl-hive-1.0.0-SNAPSHOT.jar


$ CREATE EXTERNAL TABLE tweets (
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


## 5. **Configure HBASE**

Create table hashtag in HBase

... erweitern der zu erstellenden Tabelle?

$ create "hashtags", { NAME => "hashtag_family", VERSIONS => 3 }


## 6. **Set up the tomcat**

Download and unzip Tomcat 8
(https://tomcat.apache.org/download-80.cgi)

Copy the downloaded folder to /opt/


$ cp /home/cloudera/Downloads/apache-tomcat-8.5.11.tar.gz /opt/


Add the following wars to Tomcat directory in /opt/


$ cp ~/Bigdata/bigdata-nfl-hbase-interface/target/bigdata-nfl-hbase-Interface.war /opt/apache-tomcat-8.5.11/webapps/
$ cp ~/Bigdata/bigdata-nfl-...WO SOLL DIE SEIN?.../bigdata-nfl-presentation.war /opt/apache-tomcat-8.5.11/webapps/


Configure the Apache Port in the configuration file.  ????
Browse to:


$ cd /
$ cd/opt/apache-tomcat-8.0.32/conf/


Open the server.xml and add:


$ <Connector port="8081" protocol="HTTP/1.1"
	  				 connectionTimeout="20000"
             redirectPort="8443" />


Start the tomcat with sudo command

$ sudo bash /bin/catalina.sh start

Tomcat available by using following uri

$ http://localhost:8090/...


## 7. **Start the workflow coordinator**

Navigate to the project and enter the folder bigdata-nfl-oozie.
Afterwards run the following command in terminal and start the workflow coordinator job

oozie job -run -oozie http://quickstart.cloudera:11000/oozie -config job.properties
