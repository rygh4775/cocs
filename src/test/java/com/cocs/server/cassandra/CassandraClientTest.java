package com.cocs.server.cassandra;

import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.KeySlice;
import org.apache.cassandra.thrift.KsDef;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cocs.common.Env;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml"})
public class CassandraClientTest implements IClientTest{
	private static final Logger LOG = Logger.getLogger(CassandraClientTest.class);
	
	private static final String HOST = "localhost";
	private static final int PORT = 9160;
	
	private static final String UTF8 = "UTF8";
	private static final ConsistencyLevel CL = ConsistencyLevel.ONE;
	
	private String COLUMNFAMILY_NAME;
	private String KEYSPACE_NAME;

	
	private static TFramedTransport tr;
	private static Client client;	
	
	@BeforeClass
	public static void beforeClass(){
		
		tr = new TFramedTransport(new TSocket(HOST, PORT));
		TProtocol proto = new TBinaryProtocol(tr);
		client = new Cassandra.Client(proto);
		try {
			tr.open();
		} catch (TTransportException e) {
		}
	}
	
	@AfterClass
	public static void afterClass(){
		tr.close();
	}
	
	@Before
	public void before(){
		COLUMNFAMILY_NAME = Env.getProperty("cassandra.columnfamily.name");
		KEYSPACE_NAME = Env.getProperty("cassandra.keyspace.name");
	}
	
	@Override
	@Test
	public void retrieveServerInfo(){
		try {
			LOG.info("[Cluster Name : " + client.describe_cluster_name()+"]");
			Iterator<KsDef> iterator = client.describe_keyspaces().iterator();
			while (iterator.hasNext()) {
				KsDef ksDef = (KsDef) iterator.next();
				LOG.info("[Keyspace Name : " + ksDef.getName()+"]");
			}
		} catch (TException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Override
	@Test
	@Ignore
	public void addKeyspace() {
		// TODO Auto-generated method stub
	}
	
	@Override
	@Test
	public void addKeyspaceWithColumnFamily() {
		String strategy_class = "org.apache.cassandra.locator.SimpleStrategy";
		
		ArrayList<CfDef> cfdefs = new ArrayList<CfDef>();
		
		CfDef cfdef1 = new CfDef(KEYSPACE_NAME, COLUMNFAMILY_NAME);
		cfdefs.add(cfdef1);
		
		KsDef ksdef = new KsDef(KEYSPACE_NAME, strategy_class, cfdefs);
		Map<String, String> map = new HashMap<String, String>();
		map.put("replication_factor", "1");
		ksdef.strategy_options = map;
		try {
			client.system_add_keyspace(ksdef);
		} catch (TException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Override
	@Test
	public void dropColumnFamily() {
		try {
			client.set_keyspace(KEYSPACE_NAME);
			client.system_drop_column_family(COLUMNFAMILY_NAME);
		} catch (TException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Override
	@Test
	public void addCoulmnFamily() {
		CfDef cfdef2 = new CfDef(KEYSPACE_NAME, COLUMNFAMILY_NAME);
		
		try {
			client.set_keyspace(KEYSPACE_NAME);
			client.system_add_column_family(cfdef2);
		} catch (TException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Override
	@Test
	public void insertColumn() {
		try {
			client.set_keyspace(KEYSPACE_NAME);
	
			String ROW_KEY = "rygh4775@gmail.com";
			byte[] rowKey = ROW_KEY.getBytes(UTF8);
			
			ColumnParent columnParent = new ColumnParent(COLUMNFAMILY_NAME);
			
			//insert column
			Column nameColumn = createColumn("password".getBytes(UTF8), "12345".getBytes(UTF8));
			client.insert(ByteBuffer.wrap(rowKey), columnParent, nameColumn, CL);
			
		} catch (TException | UnsupportedEncodingException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Override
	@Test
	public void insertMultipleColumns() {
		try {
			client.set_keyspace(KEYSPACE_NAME);
	
			String ROW_KEY = "rygh4775@nate.com";
			byte[] rowKey = ROW_KEY.getBytes(UTF8);
			
			ColumnParent columnParent = new ColumnParent(COLUMNFAMILY_NAME);
			
			//insert multi column
			Column nameColumn = createColumn("password".getBytes(UTF8), "111111".getBytes(UTF8));
			client.insert(ByteBuffer.wrap(rowKey), columnParent, nameColumn, CL);
			
			Column pwColumn = createColumn("google".getBytes(UTF8), "129djkdji2huhsfg29dadd82ud92id84h".getBytes(UTF8));
			client.insert(ByteBuffer.wrap(rowKey), columnParent, pwColumn, CL);
			
			Column facebookColumn = createColumn("dropbox".getBytes(UTF8), "129djkdji2huhsfg29dadd82ud92id84h".getBytes(UTF8));
			client.insert(ByteBuffer.wrap(rowKey), columnParent, facebookColumn, CL);
			
			Column GoogleColumn = createColumn("skydrive".getBytes(UTF8), "129djkdji2huhsfgsdd29dadd82ud92id84h".getBytes(UTF8));
			client.insert(ByteBuffer.wrap(rowKey), columnParent, GoogleColumn, CL);
		} catch (TException | UnsupportedEncodingException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Override
	@Test
	@Ignore
	public void deleteColumn() {
		// TODO Auto-generated method stub
	}
	
	@Override
	@Test
	@Ignore
	public void deleteMultipleColumns() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	@Test
	public void getColumn() {
		try {
			client.set_keyspace(KEYSPACE_NAME);
			String ROW_KEY = "rygh4775@gmail.com";
			byte[] rowKey = ROW_KEY.getBytes(UTF8);
			
			String COLUMN_KEY = "password";
			byte[] columnKey = COLUMN_KEY.getBytes(UTF8);
			
			ColumnPath path = new ColumnPath();
			path.column_family = COLUMNFAMILY_NAME;
			path.column = ByteBuffer.wrap(columnKey);
			
			ColumnOrSuperColumn cosc = client.get(ByteBuffer.wrap(rowKey), path, CL);
			Column column = cosc.column;
			LOG.info("[name: " + byteBufferToString(column.name) + ", value: " + byteBufferToString(column.value) + "]");
		} catch (TException | UnsupportedEncodingException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Override
	@Test
	@Ignore
	public void getMultipleColumns() {
		
	}
	
	@Override
	@Test
	public void getAllColumns() {
		try {
			client.set_keyspace(KEYSPACE_NAME);
			String ROW_KEY = "rygh4775@nate.com";
			byte[] rowKey = ROW_KEY.getBytes(UTF8);
			
			ColumnParent parent = new ColumnParent(COLUMNFAMILY_NAME);
			
			SlicePredicate predicate = new SlicePredicate();
			SliceRange sliceRange = new SliceRange();
			sliceRange.setStart(new byte[0]);
			sliceRange.setFinish(new byte[0]);
			predicate.setSlice_range(sliceRange);
			
			int num = client.get_count(ByteBuffer.wrap(rowKey), parent, predicate, CL);
			LOG.info("[Total Count : " + num + "]");
			
			List<ColumnOrSuperColumn> results = client.get_slice(ByteBuffer.wrap(rowKey), parent, predicate, CL);
			for (ColumnOrSuperColumn result : results) {
				Column column = result.column;
				LOG.info("[name: " + byteBufferToString(column.name) + " value: " + byteBufferToString(column.value) + "]");
			}
		} catch (TException | UnsupportedEncodingException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Override
	@Test
	public void getMultipleRows() {
		try {
			client.set_keyspace(KEYSPACE_NAME);
			SlicePredicate predicate = new SlicePredicate();
			predicate.addToColumn_names(ByteBuffer.wrap("password".getBytes(UTF8)));
			
			ColumnParent parent = new ColumnParent(COLUMNFAMILY_NAME);
			
			KeyRange keyRange = new KeyRange();
			keyRange.start_key = ByteBuffer.wrap(new byte[0]);
			keyRange.end_key = ByteBuffer.wrap(new byte[0]);
			
			List<KeySlice> results = client.get_range_slices(parent, predicate, keyRange, CL);
			
			 LOG.info("[column family: " +COLUMNFAMILY_NAME+"]");
			for (KeySlice keySlice : results) {
				LOG.debug("[row key: " + new String(keySlice.getKey())+"]");
				List<ColumnOrSuperColumn> columns = keySlice.getColumns();
				for (ColumnOrSuperColumn columnOrSuperColumn : columns) {
					System.out.println(columnOrSuperColumn.getColumn().name);
				}
	//			List<ColumnOrSuperColumn> cosc = keySlice.getColumns();
			}
		} catch (TException | UnsupportedEncodingException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Override
	@Test
	public void dropKeyspace(){
		try {
			client.system_drop_keyspace(KEYSPACE_NAME);
		} catch (TException e) {
			e.printStackTrace();
			fail();
		}
	}

	private static String byteBufferToString(ByteBuffer buffer) throws UnsupportedEncodingException {
		byte[] bytes = new byte[buffer.limit() - buffer.position()];
		buffer.get(bytes);
		return new String(bytes, UTF8);
	}

	private Column createColumn(byte[] name, byte[] value) {	// key, value
		Column column = new Column();
		column.setName(name);
		column.setValue(value);
		column.setTimestamp(System.currentTimeMillis());
		return column;
	}
}
