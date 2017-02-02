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

import de.fhm.bigdata.projekt.hbase.model.*;

public class HBaseConnectionManager {

	private static Configuration conf;
	
	private static String TABLE_TEST = "test2";
	private static String TABLE_TEAMS = "";
	private static String TABLE_HASHTAGS = "";

	private static final byte[] COLUMN_FAMILY_REPODATA = Bytes.toBytes("familiy_a");
	private static final byte[] COLUMN_REPODATA_NAME = Bytes.toBytes("column-1");
	
	private static final byte[] COLUMN_FAMILY = Bytes.toBytes("");
	private static final byte[] COLUMN_A = Bytes.toBytes("");
	
	public HBaseConnectionManager() {
		conf = HBaseConfiguration.create();
	}


	public List<String> getTopHasttags() {
		

		List<String> resultList = new ArrayList<String>();
		ResultScanner rs = null;
		Result res = null;
		String hashtag = null;
		int counter;
		String result = null;
		try {
			HTable table = new HTable(conf, TABLE_TEST);
			Scan scan = new Scan();
			scan.addColumn(COLUMN_FAMILY_REPODATA, COLUMN_REPODATA_NAME);
			rs = table.getScanner(scan);
			while ((res = rs.next()) != null) {

				byte[] rohHashtag = res.getValue(COLUMN_FAMILY_REPODATA, COLUMN_REPODATA_NAME);
				byte[] rohCounter = res.getValue(COLUMN_FAMILY_REPODATA, COLUMN_REPODATA_NAME);


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

	
}
