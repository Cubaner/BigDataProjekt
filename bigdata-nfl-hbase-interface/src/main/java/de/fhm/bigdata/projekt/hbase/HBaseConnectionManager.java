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
	
	private static String TABLE_TEAMS = "teams";
	private static String COLUMN_TEAM = "team";
	private static String COLUMN_RANK = "rank";

	public HBaseConnectionManager() {
		conf = HBaseConfiguration.create();
	}


	public List<String> getRepositories() {
		

		List<String> resultList = new ArrayList<String>();
		ResultScanner rs = null;
		Result res = null;
		String sOwner = null;
		String sName = null;
		String s = null;
		try {
			HTable table = new HTable(conf, TABLE_TEAMS);
			Scan scan = new Scan();
			//scan.addColumn(BYTE_COLUMN_FAMILY_REPODATA, BYTE_COLUMN_REPODATA_NAME);
			rs = table.getScanner(scan);
			while ((res = rs.next()) != null) {

				//byte[] repoName = res.getValue(BYTE_COLUMN_FAMILY_REPODATA, BYTE_COLUMN_REPODATA_NAME);

				//sName = Bytes.toString(repoName);
				s = sOwner + "/" + sName;
				if (!resultList.contains(s)) {
					resultList.add(s);
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
