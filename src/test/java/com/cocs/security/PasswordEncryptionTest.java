package com.cocs.security;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;

import org.apache.cassandra.utils.Hex;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cocs.common.Env;
import com.cocs.security.PasswordEncryptionService;
import com.cocs.server.HectorUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml"})
public class PasswordEncryptionTest {
	private static final Logger LOG = Logger.getLogger(PasswordEncryptionTest.class);
	
	private String URL;
	private String KEYSPACE_NAME;
	private String CLUSTER_NAME;
	private String COLUMNFAMILY_NAME;
	
	private static final String UTF8 = "UTF8";
	@Before
	public void before(){
		URL = Env.getProperty("cassandra.url");
		CLUSTER_NAME = Env.getProperty("cassandra.cluster.name");
		COLUMNFAMILY_NAME = Env.getProperty("cassandra.columnfamily.name");
		KEYSPACE_NAME = Env.getProperty("cassandra.keyspace.name");
	}
	
	@Test
	public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] generatedSalt = PasswordEncryptionService.generateSalt();
		System.out.println(generatedSalt);
		byte[] encryptedPassword = PasswordEncryptionService.getEncryptedPassword("1111", generatedSalt);
		System.out.println(encryptedPassword);
		
		assertTrue(PasswordEncryptionService.authenticate("1111", encryptedPassword, generatedSalt));
	}
	
	@Test
	public void insertEncryptedPassword() throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException{
		String ROW_KEY = "system";
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		
		byte[] generatedSalt = PasswordEncryptionService.generateSalt();
		byte[] encryptedPassword = PasswordEncryptionService.getEncryptedPassword("1111", generatedSalt);
		String hexSalt = Hex.bytesToHex(generatedSalt);
		String hexPassword = Hex.bytesToHex(encryptedPassword);
		
		Mutator<String> mutator = HFactory.createMutator(keyspace, new StringSerializer());
		HectorUtils.insertColumn(mutator, ROW_KEY, COLUMNFAMILY_NAME, "salt", hexSalt);
		HectorUtils.insertColumn(mutator, ROW_KEY, COLUMNFAMILY_NAME, "password", hexPassword);
		HectorUtils.insertColumn(mutator, ROW_KEY, COLUMNFAMILY_NAME, "test", "I am test");
		
		assertTrue(PasswordEncryptionService.authenticate("1111", Hex.hexToBytes(hexPassword), Hex.hexToBytes(hexSalt)));
	}
	
	@Test
	public void isCorretPassword() throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException{
		Cluster cluster = HFactory.getOrCreateCluster(CLUSTER_NAME, new CassandraHostConfigurator(URL));
		Keyspace keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		
		SliceQuery<String,String,String> slicequery = HFactory.createSliceQuery(keyspace, StringSerializer.get(), StringSerializer.get(), StringSerializer.get());
		slicequery.setColumnFamily(COLUMNFAMILY_NAME).setKey("system").setColumnNames("salt", "test", "password");
		
		QueryResult<ColumnSlice<String,String>> result = slicequery.execute();
		ColumnSlice<String, String> columnSlice = result.get();
		List<HColumn<String,String>> columns = columnSlice.getColumns();
		
		String salt = null;
		String password = null;
		for (HColumn<String, String> column : columns) {
			if(column.getName().equals("salt")) {
				salt = column.getValue();
			} else if (column.getName().equals("password")) {
				password = column.getValue();
			}
		}
		
		assertTrue(PasswordEncryptionService.authenticate("1111", Hex.hexToBytes(password), Hex.hexToBytes(salt)));
	}
	
	private static byte[] byteBufferToByte(ByteBuffer buffer) throws UnsupportedEncodingException {
		byte[] bytes = new byte[buffer.limit() - buffer.position()];
		buffer.get(bytes);
		return bytes;
	}
	
	private static String byteBufferToString(ByteBuffer buffer) throws UnsupportedEncodingException {
		byte[] bytes = new byte[buffer.limit() - buffer.position()];
		buffer.get(bytes);
		return new String(bytes, UTF8);
	}
	
	public static String toHexString(byte[] array) {
		return DatatypeConverter.printHexBinary(array);
	}

	public static byte[] toByteArray(String s) {
		return DatatypeConverter.parseHexBinary(s);
	}
}
