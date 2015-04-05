package com.cocs.server.cassandra;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import me.prettyprint.cassandra.model.BasicColumnDefinition;
import me.prettyprint.cassandra.model.BasicColumnFamilyDefinition;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.cassandra.service.ThriftCfDef;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ColumnIndexType;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cocs.common.DefaultConstants;
import com.cocs.common.Env;
import com.cocs.server.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml"})
public class HectorClientTest implements IClientTest, DefaultConstants{
	private static final Logger LOG = Logger.getLogger(CassandraClientTest.class);
	
	private String URL;
	private String KEYSPACE_NAME;
	private String CLUSTER_NAME;
	private String COLUMNFAMILY_NAME;
	
	@Before
	public void before(){
		URL = Env.getProperty("cassandra.url");
		CLUSTER_NAME = Env.getProperty("cassandra.cluster.name");
		COLUMNFAMILY_NAME = Env.getProperty("cassandra.columnfamily.name");
		KEYSPACE_NAME = Env.getProperty("cassandra.keyspace.name");
	}
	
	@Override
	@Test
	public void retrieveServerInfo(){
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		LOG.info("[Cluster Name : " + cluster.getName()+"]");
		List<KeyspaceDefinition> keyspaces = cluster.describeKeyspaces();
		for (KeyspaceDefinition keyspace : keyspaces) {
			LOG.info("[Keyspace Name : " + keyspace.getName()+"]");
		}
	}
	
	@Override
	@Test
	public void addKeyspace(){
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));

		KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(KEYSPACE_NAME);
		cluster.addKeyspace(newKeyspace);
	}
	
	@Override
	@Test
	public void dropKeyspace(){
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));

		cluster.dropKeyspace(KEYSPACE_NAME);
	}
	
	@Override
	@Test
	public void addKeyspaceWithColumnFamily() {
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		
		ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(KEYSPACE_NAME, COLUMNFAMILY_NAME, ComparatorType.UTF8TYPE);

		KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(KEYSPACE_NAME, ThriftKsDef.DEF_STRATEGY_CLASS, 1, Arrays.asList(cfDef));
		
		cluster.addKeyspace(newKeyspace, true);
	}
	
	@Override
	@Test
	public void dropColumnFamily() {
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		cluster.dropColumnFamily(KEYSPACE_NAME, COLUMNFAMILY_NAME);
	}
	
	@Override
	@Test
	public void addCoulmnFamily() {
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		
		ColumnFamilyDefinition newColumnFamily = HFactory.createColumnFamilyDefinition(KEYSPACE_NAME, COLUMNFAMILY_NAME);
		// Add the schema to the cluster.
		// "true" as the second param means that Hector will block until all nodes see the change.
		cluster.addColumnFamily(newColumnFamily);
		
	}
	
	@Override
	@Test
	public void insertColumn(){
		String ROW_KEY = "system";
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		
		Mutator<String> mutator = HFactory.createMutator(keyspace, new StringSerializer());
		mutator.insert(ROW_KEY, COLUMNFAMILY_NAME, HFactory.createStringColumn("password", "1111"));
		mutator.insert(ROW_KEY, COLUMNFAMILY_NAME, HFactory.createStringColumn("active", "false"));
		
	}
	
	@Override
	@Test
	public void insertMultipleColumns() {
		String ROW_KEY = "rygh4775@nate.com";
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		
		Mutator<String> mutator = HFactory.createMutator(keyspace, new StringSerializer());
		mutator.addInsertion(ROW_KEY, COLUMNFAMILY_NAME, HFactory.createStringColumn("password", "1111111"));
		mutator.addInsertion(ROW_KEY, COLUMNFAMILY_NAME, HFactory.createStringColumn("google", "13212e82fhf292fh82fjd"));
		mutator.addInsertion(ROW_KEY, COLUMNFAMILY_NAME, HFactory.createStringColumn("dropbox", "asflkasflkf229292f9"));
		mutator.execute();
	}
	
	@Test
	@Ignore
	public void insertColumnWithTemplate() {
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		
		ColumnFamilyTemplate<String, String> template = getTemplate(keyspace, COLUMNFAMILY_NAME);
		
		// <String, String> correspond to key and Column name.
		ColumnFamilyUpdater<String, String> updater = template.createUpdater("Hkey3");
		updater.setString("domain", "www.datastax.com");
		updater.setString("clientName", "user1");
//		updater.setLong("time", System.currentTimeMillis());
		
		template.update(updater);
	}
	
	@Override
	@Test
	public void getColumn() {
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		String ROW_KEY = "system";
		String COLUMN_NAME = "password";
		
		ColumnQuery<String, String, String> columnquery = HFactory.createStringColumnQuery(keyspace);
		columnquery.setColumnFamily(COLUMNFAMILY_NAME).setKey(ROW_KEY).setName(COLUMN_NAME);
		QueryResult<HColumn<String, String>> result = columnquery.execute();
		
		HColumn<String, String> column = result.get();
		LOG.info("[column key: " + ROW_KEY + "]");
		LOG.info("[name: " + column.getName() + ", value: " + column.getValue() + "]");
	}

	@Override
	@Test
	public void getMultipleColumns(){
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		SliceQuery<String, String, String> slicequery = HFactory.createSliceQuery(keyspace, StringSerializer.get(), StringSerializer.get(), StringSerializer.get());
		slicequery.setColumnFamily(COLUMNFAMILY_NAME).setKey("system").setColumnNames("email", "name");
		QueryResult<ColumnSlice<String, String>> result = slicequery.execute();
		ColumnSlice<String, String> columnSlice = result.get();
		List<HColumn<String,String>> columns = columnSlice.getColumns();
		System.out.println(columns.contains("email"));
		JSONObject jsonObject = new JSONObject();
		for (HColumn<String, String> column : columns) {
			jsonObject.put(column.getName(), column.getValue());
			LOG.info("[name: " + column.getName() + ", value: " + column.getValue() + "]");
		}
	}
	
	@Override
	@Test
	public void getAllColumns(){
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		
		// Iterates over all columns for the row identified by key
		SliceQuery<String, String, String> query = HFactory.createSliceQuery(keyspace, StringSerializer.get(),
		    StringSerializer.get(), StringSerializer.get()).setKey("rygh4775@nate.com").setColumnFamily(COLUMNFAMILY_NAME);
		ColumnSliceIterator<String, String, String> iterator =
		    new ColumnSliceIterator<String, String, String>(query, null, "\uFFFF", false);
		
		JSONObject jsonObject = new JSONObject();
		while (iterator.hasNext()) {
			HColumn<String, String> column = iterator.next();
			jsonObject.put(column.getName(), column.getValue());
			LOG.debug("[name: " + column.getName() + ", value: " + column.getValue() + "]");
		}
	}
	
	@Test
	@Ignore
	public void getColumnWithTemplate() {
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		
		ColumnFamilyTemplate<String, String> template = getTemplate(keyspace, COLUMNFAMILY_NAME);
		
		ColumnFamilyResult<String, String> result = template.queryColumns("Hkey");
		String columnName = "domain";
		LOG.info("[column key: " + result.getKey() + "]");
		LOG.info("[name: " + columnName + ", value: " + result.getString(columnName) + "]");
	}
	
	@Override
	@Test
	public void deleteColumn() {
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		
		Mutator<String> mutator = HFactory.createMutator(keyspace, new StringSerializer());
		mutator.delete(SYSTEM, COLUMNFAMILY_NAME, "test", new StringSerializer());
	}
	
	@Override
	@Test
	public void deleteMultipleColumns() {
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		
		Mutator<String> mutator = HFactory.createMutator(keyspace, new StringSerializer());
		mutator.addDeletion(SYSTEM, COLUMNFAMILY_NAME, DROPBOX_ACCESS_TOKEN, new StringSerializer());
		mutator.addDeletion(SYSTEM, COLUMNFAMILY_NAME, DROPBOX_USER_ID, new StringSerializer());
		mutator.addDeletion(SYSTEM, COLUMNFAMILY_NAME, GOOGLE_REFRESH_TOKEN, new StringSerializer());
		mutator.execute();
	}
	
	@Test
	@Ignore
	public void deleteColumnWithTemplate() {
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		
		ColumnFamilyTemplate<String, String> template = getTemplate(keyspace, COLUMNFAMILY_NAME);
		template.deleteColumn("Hkey", "time");
	}
	
	@Override
	@Test
	public void getMultipleRows() {
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		
		RangeSlicesQuery<String, String, String> query = HFactory.createRangeSlicesQuery(keyspace, StringSerializer.get(), StringSerializer.get(), StringSerializer.get()
				).setColumnFamily(COLUMNFAMILY_NAME)
	            .setRange(null, null, false, 10);
		
		QueryResult<OrderedRows<String, String, String>> result = query.execute();
        OrderedRows<String, String, String> rows = result.get();
        Iterator<Row<String, String, String>> rowsIterator = rows.iterator();
        
        LOG.info("[column family: " +COLUMNFAMILY_NAME+"]");
        while (rowsIterator.hasNext()) {
            Row<String, String, String> row = rowsIterator.next();
            LOG.debug("[row key: " + row.getKey()+"]");
          }
	}
		
	@Test
	public void deleteRow() {
		String ROW_KEY = "rygh4775@test.com";
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		Mutator<String> mutator = HFactory.createMutator(keyspace, new StringSerializer());
		mutator.addDeletion(ROW_KEY, COLUMNFAMILY_NAME, null, StringSerializer.get());
		mutator.execute();
	}
	
	private ColumnFamilyTemplate<String, String> getTemplate(Keyspace keyspaceName, String columnfamilyName) {
		ColumnFamilyTemplate<String, String> template =
                new ThriftColumnFamilyTemplate<String, String>(keyspaceName,
                                                               columnfamilyName,
                                                               StringSerializer.get(),
                                                               StringSerializer.get());
		return template;
	}
	
	@Test
	public void indexCoulmn() {
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		
		KeyspaceDefinition fromCluster = cluster.describeKeyspace(KEYSPACE_NAME);

	    ColumnFamilyDefinition cfDef = fromCluster.getCfDefs().get(0);


	    BasicColumnFamilyDefinition columnFamilyDefinition = new BasicColumnFamilyDefinition(cfDef);
	    
	    BasicColumnDefinition columnDefinition = new BasicColumnDefinition();
	    columnDefinition.setName(StringSerializer.get().toByteBuffer("oauthProvider"));
	    columnDefinition.setIndexName("oauthProvider_index");
	    columnDefinition.setIndexType(ColumnIndexType.KEYS);
	    columnDefinition.setValidationClass(ComparatorType.UTF8TYPE.getClassName());
	    columnFamilyDefinition.addColumnDefinition(columnDefinition);
	    

	    cluster.updateColumnFamily(new ThriftCfDef(columnFamilyDefinition));
	}
}
