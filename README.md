1. **Start the Flume agent**

    Create the HDFS directory hierarchy for the Flume sink. Make sure that it will be accessible by the user running the Oozie workflow.  
    
    <pre>
    $ hadoop fs -mkdir /user/flume/tweets
    $ hadoop fs -chown -R flume:flume /user/flume
    $ hadoop fs -chmod -R 770 /user/flume
    $ sudo /etc/init.d/flume-ng-agent start
    </pre>
    
    If using Cloudera Manager, start Flume agent from Cloudera Manager Web UI.

2. **Adjust the start time of the Oozie coordinator workflow in job.properties**

    You will need to modify the `job.properties` file, and change the `jobStart`, `jobEnd`, and `initialDataset` parameters. The start and end times are in UTC, because the version of Oozie packaged in CDH4 does not yet support custom timezones for workflows. The initial dataset should be set to something before the actual start time of your job in your local time zone. Additionally, the `tzOffset` parameter should be set to the difference between the server's timezone and UTC. By default, it is set to -8, which is correct for US Pacific Time.

3. **Start the Oozie coordinator workflow**
    
    <pre>$ oozie job -oozie http://&lt;oozie-host&gt;:11000/oozie -config oozie-workflows/job.properties -run</pre>

