# Readme - Big Data Project 2017
## Analyzing NFL-Twitter data by using CDH 5.8

Before you get started with the application, you will first need to install CDH 5.8. Specifically, you will need Hadoop, Flume, Oozie, and Hive. The easiest way to get the core components is to use Cloudera Manager to set up your initial environment. You can download the vm image here: (http://www.cloudera.com/downloads/quickstart_vms/5-8.html) How to set up the configuration will be explained in detail within the following steps.

## 1. **Clone git project and build artifacts**

	$ git clone https://github.com/Cubaner/BigDataProjekt.git
	$ cd BigDataProjekt
	$ mvn clean install

## 2. **Configure the Flume agent**

	- Go to the Flume Service page, click on the ‚Configuration‘ tab and select ‚View and Edit‘
	- Select the Agent ‚Default‘
	- Set the Agent Name property to ‚TwitterAgent’
	- Copy the content of the TwitterAgents below.
	- Afterwards click ‚Save Changes‘

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
	</pre>

	<pre>
	$ cd /
	$ /var/lib/flume-ng/plugins.d/twitter-streaming/lib/bigdata-nfl-dataimport-1.0.0-SNAPSHOT.jar
	$ /usr/lib/flume-ng/plugins.d/twitter-streaming/lib/bigdata-nfl-dataimport-1.0.0-SNAPSHOT.jar
	</pre>

## 3. **Configure Oozie and create the relevant directory in HDFS**

	Copy the dataprocessing-SNAPSHOT.jar into oozie-workflows/lib/ (@ ~ you have to insert your own directory path)
	<pre>
	$ cp ~/BigDataProjekt/bigdata-nfl-dataprocessing/target/bigdata-nfl-dataprocessing-1.0.0-SNAPSHOT.jar ~/BigData/BigDataProjekt/bigdata-nfl-oozie/oozie-workflows/lib/
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

	Add hive.jar to /usr/lib/hive/lib/bigdata-nfl-hive-1.0.0-SNAPSHOT.jar

	<pre>
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
	</pre>

## 5. **Configure HBASE**

	Create table hashtag in HBase

  ... erweitern der zu erstellenden Tabelle?
	<pre>
	$ create "hashtags", { NAME => "hashtag_family", VERSIONS => 3 }
	</pre>

## 6. **Set up the tomcat**

	Download and unzip Tomcat 8
	(https://tomcat.apache.org/download-80.cgi)

	Copy the downloaded folder to /opt/
	<pre>
	$ cp /home/cloudera/Downloads/apache-tomcat-8.5.11.tar.gz /opt/
	</pre>

	Add the following wars to Tomcat directory in /opt/
	<pre>
	$ cp ~/Bigdata/bigdata-nfl-hbase-interface/target/bigdata-nfl-hbase-Interface.war /opt/apache-tomcat-8.5.11/webapps/
	$ cp ~/Bigdata/bigdata-nfl-...WO SOLL DIE SEIN?.../bigdata-nfl-presentation.war /opt/apache-tomcat-8.5.11/webapps/
	</pre>

	Configure the Apache Port in the configuration file.  ????
	Browse to:

	<pre>
	$ cd /
	$ cd/opt/apache-tomcat-8.0.32/conf/
	</pre>

	Open the server.xml and add:

	<pre>
	$ <Connector port="8090" protocol="HTTP/1.1"
	  					 connectionTimeout="20000"
               redirectPort="8443" />
	</pre>


	Start the tomcat with sudo command
	<pre>
	$ sudo bash /bin/catalina.sh start
	</pre>

	Tomcat available by using following uri
	<pre>
	$ http://localhost:8090/...
	</pre>

## 7. **Start the workflow coordinator**

	Navigate to the project and enter the folder bigdata-nfl-oozie.
	Afterwards run the following command in terminal and start the workflow coordinator job
	<pre>
	oozie job -run -oozie http://quickstart.cloudera:11000/oozie -config job.properties
	</pre>
