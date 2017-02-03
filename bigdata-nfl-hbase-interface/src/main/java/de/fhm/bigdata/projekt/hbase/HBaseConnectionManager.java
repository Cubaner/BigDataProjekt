package de.fhm.bigdata.projekt.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import com.google.protobuf.ServiceException;

import de.fhm.bigdata.projekt.hbase.model.*;

public class HBaseConnectionManager {

	private static Configuration config;
	
	private static String TABLE_TEST = "test2";
	private static String TABLE_TEAMS = "";
	private static String TABLE_HASHTAGS = "hashtags";

	private static final byte[] COLUMN_FAMILY_REPODATA = Bytes.toBytes("familiy_a");
	private static final byte[] COLUMN_REPODATA_NAME = Bytes.toBytes("column-1");
	
	private static final byte[] COLUMN_FAMILY = Bytes.toBytes("");
	private static final byte[] COLUMN_A = Bytes.toBytes("");
	
	private static final byte[] COLUMN_FAMILY_HASHTAGS = Bytes.toBytes("hashtag_family");
	private static final byte[] COLUMN_HASHTAGS_NAME = Bytes.toBytes("hashtag");
	private static final byte[] COLUMN_HASHTAGS_COUNTER = Bytes.toBytes("counter");
	
	public HBaseConnectionManager() {
        try {
            config = HBaseConfiguration.create();
            config.clear();
            config.set("hbase.zookeeper.quorum", "localhost");
            config.set("hbase.zookeeper.property.clientPort","2181");
            config.set("hbase.master", "localhost:60000");
            //HBaseConfiguration config = HBaseConfiguration.create();
            //config.set("hbase.zookeeper.quorum", "localhost");  // Here we are running zookeeper locally
            HBaseAdmin.checkHBaseAvailable(config);


            System.out.println("HBase is running!");
        //  createTable(config);    
            //creating a new table
        } catch (MasterNotRunningException e) {
            System.out.println("HBase is not running!");
        } catch (ZooKeeperConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public List<String> test() {
		

		List<String> resultList = new ArrayList<String>();
		ResultScanner rs = null;
		Result res = null;
		String hashtag = null;
		int counter;
		String result = null;
		try {
			HTable table = new HTable(config, TABLE_TEST);
			Scan scan = new Scan();
			scan.addColumn(COLUMN_FAMILY_REPODATA, COLUMN_REPODATA_NAME);
			rs = table.getScanner(scan);
			while ((res = rs.next()) != null) {

				byte[] rohHashtag = res.getValue(COLUMN_FAMILY_HASHTAGS, COLUMN_HASHTAGS_NAME);
				byte[] rohCounter = res.getValue(COLUMN_FAMILY_HASHTAGS, COLUMN_HASHTAGS_COUNTER);


				hashtag = Bytes.toString(rohHashtag);
				counter = Bytes.toInt(rohCounter);
				result = hashtag + ":" + counter;
				if (!resultList.contains(result)) {
					resultList.add(result);
				}
			}
		} catch (IOException e) {
			System.out.println("Exception occured in retrieving data");
		} finally {
			rs.close();
		}
		System.out.println(resultList.toString());
		return resultList;
	}
	
	public ArrayList<Hashtag> getTopHashtags() {
		ArrayList<Hashtag> resultList = new ArrayList<Hashtag>();
		ResultScanner rs = null;
		Result res = null;
		String hashtag = null;
		String counter;
		Hashtag result = null;
		try {
			HTable table = new HTable(config, TABLE_HASHTAGS);
			Scan scan = new Scan();
			scan.addColumn(COLUMN_FAMILY_REPODATA, COLUMN_REPODATA_NAME);
			rs = table.getScanner(scan);
			while ((res = rs.next()) != null) {

				byte[] rohHashtag = res.getValue(COLUMN_FAMILY_REPODATA, COLUMN_REPODATA_NAME);
				byte[] rohCounter = res.getValue(COLUMN_FAMILY_REPODATA, COLUMN_REPODATA_NAME);

				hashtag = Bytes.toString(rohHashtag);
				counter = Bytes.toString(rohCounter);
				result = new Hashtag (hashtag, counter);
				if (!resultList.contains(result)) {
					resultList.add(result);
				}
			}
		} catch (IOException e) {	
			System.out.println("Exception occured in retrieving data");
		} finally {
			rs.close();
		}
		return resultList;
	}

	
}