package com.cocs.server.cassandra;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cocs.common.Env;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ExecutionInfo;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.QueryTrace;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.TableMetadata;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml"})
public class DatastaxClientTest {
	private static final Logger LOG = Logger.getLogger(DatastaxClientTest.class);
	
	private static final String HOST = "localhost";
	private static final int PORT = 9160;
	
	private static final String UTF8 = "UTF8";
	private static final ConsistencyLevel CL = ConsistencyLevel.ONE;
	
	private String COLUMNFAMILY_NAME;
	private String KEYSPACE_NAME;
	
	private static Cluster cluster;
	private static Session session;	
	
	@BeforeClass
	public static void beforeClass(){
		cluster = Cluster.builder()
				 .addContactPoint("localhost")
				 .build();
		
		session = cluster.connect();
	}
	
	@AfterClass
	public static void afterClass(){
		cluster.close();
	}
	
	@Before
	public void before(){
		COLUMNFAMILY_NAME = Env.getProperty("cassandra.columnfamily.name");
		KEYSPACE_NAME = Env.getProperty("cassandra.keyspace.name");
	}
	
	@Test
	public void retrieveServerInfo(){
		Metadata metadata = cluster.getMetadata();
		LOG.info("[Cluster Name : " + metadata.getClusterName()+"]");
		for (Host host : metadata.getAllHosts()) {
			System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",host.getDatacenter(), host.getAddress(), host.getRack());
		}
	}
	
	@Test
	public void createKeyspace() {
		session.execute("CREATE KEYSPACE simplex WITH replication " + 
				 "= {'class':'SimpleStrategy', 'replication_factor':3};");
		
		List<KeyspaceMetadata> keyspaces = cluster.getMetadata().getKeyspaces();
		for (KeyspaceMetadata keyspaceMetadata : keyspaces) {
			LOG.info("[Keyspace Name : " + keyspaceMetadata.getName()+"]");
		}
	}
	
	@Test
	public void getCoulmfamily() {
		KeyspaceMetadata keyspaces = cluster.getMetadata().getKeyspace(KEYSPACE_NAME);
		System.out.println(keyspaces.exportAsString());
		TableMetadata table = keyspaces.getTable(COLUMNFAMILY_NAME);
		LOG.debug(table.exportAsString());
	}
	
	@Test
	public void createColumnfamily() {
		session.execute(
				 "CREATE TABLE simplex.songs (" +
				 "id uuid PRIMARY KEY," + 
				 "title text," + 
				 "album text," + 
				 "artist text," + 
				 "tags set<text>," + 
				 "data blob" + 
				 ");");

	}
	
	@Test
	public void dropColumnfamily() {
	}
	
	@Test
	public void InsertColumn() {
		session.execute(
				 "INSERT INTO simplex.songs (id, title, album, artist, tags) " +
				 "VALUES (" +
				 "756716f7-2e54-4715-9f00-91dcbea6cf50," +
				 "'La Petite Tonkinoise'," +
				 "'Bye Bye Blackbird'," +
				 "'Joséphine Baker'," +
				 "{'jazz', '2013'})" +
				 ";");
		
	}
	
	@Test
	public void InsertColumn2() {
		PreparedStatement statement = session.prepare(
				 "INSERT INTO simplex.songs " +
				 "(id, title, album, artist, tags) " +
				 "VALUES (?, ?, ?, ?, ?);");

		BoundStatement boundStatement = new BoundStatement(statement);
		Set<String> tags = new HashSet<String>();
		tags.add("k-pop");
		tags.add("2014");
		session.execute(boundStatement.bind(
		 UUID.fromString("756716f7-2e54-4715-9f00-91dcbea6cf52"),
		 "MONTTRE",
		 "hAHAHA",
		 "CHO HYUN SOO",
		 tags ) );

		
	}
	
	@Test
	public void traceInsert() {
		Statement insert = QueryBuilder.insertInto("simplex", "songs")
		 .value("id", UUID.randomUUID())
		 .value("title", "Golden Brown")
		 .value("album", "La Folie")
		 .value("artist", "The Stranglers")
		 .setConsistencyLevel(ConsistencyLevel.ONE).enableTracing();
		
		ResultSet results = session.execute(insert);
		ExecutionInfo executionInfo = results.getExecutionInfo();
		
		System.out.printf("Host (queried): %s\n", executionInfo.getQueriedHost().toString());
		for (Host host : executionInfo.getTriedHosts()) {
			System.out.printf("Host (tried): %s\n", host.toString());
		}
		QueryTrace queryTrace = executionInfo.getQueryTrace();
		System.out.printf("Trace id: %s\n\n", queryTrace.getTraceId());
		System.out.println("---------------------------------------+--------------+------------+--------------");
		for (QueryTrace.Event event : queryTrace.getEvents()) {
			System.out.printf("%38s | %12s | %10s | %12s\n",
					event.getDescription(), new Date(event.getTimestamp()),
					event.getSource(), event.getSourceElapsedMicros());
		}
		
		insert.disableTracing();
	}
	
	@Test
	public void querySchema() {
		ResultSet results = session.execute("SELECT * FROM simplex.songs " +
				 "WHERE id = 756716f7-2e54-4715-9f00-91dcbea6cf50;");
		
		System.out.println(String.format("%-30s\t%-20s\t%-20s\n%s", "title","album", "artist",
				 "-------------------------------+-----------------------+--------------------"));
		for (Row row : results) {
			System.out.println(String.format("%-30s\t%-20s\t%-20s",
					row.getString("title"), row.getString("album"), row.getString("artist")));
		}
	}
	
	@Test
	public void querySchema2() {
		Select query = QueryBuilder.select().all().from("simplex", "songs");
		ResultSet results = session.execute(query);
		
		System.out.println(String.format("%-30s\t%-20s\t%-20s\n%s", "title","album", "artist",
				 "-------------------------------+-----------------------+--------------------"));
		for (Row row : results) {
			System.out.println(String.format("%-30s\t%-20s\t%-20s",
					row.getString("title"), row.getString("album"), row.getString("artist")));
		}
		
		List<Row> rows = results.all();
		for (Row row : rows) {
		}
	}
	
	@Test
	public void traceQuery() {
		SimpleStatement simpleStatement = new SimpleStatement("SELECT * FROM simplex.songs;");
		simpleStatement.enableTracing();
		
		ExecutionInfo executionInfo = session.execute(simpleStatement).getExecutionInfo();
		System.out.printf("Host (queried): %s\n", executionInfo.getQueriedHost().toString());
		for (Host host : executionInfo.getTriedHosts()) {
			System.out.printf("Host (tried): %s\n", host.toString());
		}
		
		QueryTrace queryTrace = executionInfo.getQueryTrace();
		System.out.printf("Trace id: %s\n\n", queryTrace.getTraceId());
		System.out.printf("%-38s | %-12s | %-10s | %-12s\n", "activity", "timestamp", "source", "source_elapsed");
		System.out.println("---------------------------------------+--------------+------------+--------------");
		for (QueryTrace.Event event : queryTrace.getEvents()) {
			System.out.printf("%38s | %12s | %10s | %12s\n",
					event.getDescription(), new Date(event.getTimestamp()),
					event.getSource(), event.getSourceElapsedMicros());
		}
		
		simpleStatement.disableTracing();
	}
	
}
