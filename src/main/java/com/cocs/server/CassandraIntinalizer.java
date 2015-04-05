package com.cocs.server;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.apache.cassandra.service.CassandraDaemon;
import org.apache.cassandra.utils.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;

import com.cocs.common.DefaultConstants;
import com.cocs.common.Env;
import com.cocs.security.PasswordEncryptionService;

public class CassandraIntinalizer extends DefaultResourceLoader implements DefaultConstants{
	
	public static final Logger logger = LoggerFactory.getLogger(CassandraIntinalizer.class);
	
	private CassandraDaemon cassandraDaemon = null; 
	
	public CassandraIntinalizer() {
		cassandraDaemon = new CassandraDaemon();
	}
	
	public void start() {
        System.setProperty("cassandra-foreground", "yes");
        
        cassandraDaemon.activate();
        
        try {
			createOrGetKeyspace();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException();
		}
	}
	
	private void createOrGetKeyspace() throws NoSuchAlgorithmException, InvalidKeySpecException {
		Cluster cluster = HFactory.getOrCreateCluster(Env.getProperty("cassandra.cluster.name"), new CassandraHostConfigurator(Env.getProperty("cassandra.url")));
		logger.debug("Cluster["+Env.getProperty("cassandra.cluster.name")+"] is conneted.");
		
		String keyspaceName = Env.getProperty("cassandra.keyspace.name");
		String columnFamilyName = Env.getProperty("cassandra.columnfamily.name");
		
		if(cluster.describeKeyspace(keyspaceName) == null) {
			ArrayList<ColumnFamilyDefinition> ColumnFamilyDefinitions = new ArrayList<ColumnFamilyDefinition>();
			ColumnFamilyDefinitions.add(HFactory.createColumnFamilyDefinition(keyspaceName, columnFamilyName, ComparatorType.UTF8TYPE));
			ColumnFamilyDefinitions.add(HFactory.createColumnFamilyDefinition(keyspaceName, columnFamilyName +"_count", ComparatorType.UTF8TYPE));
			KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(keyspaceName, ThriftKsDef.DEF_STRATEGY_CLASS, 1, ColumnFamilyDefinitions);
			cluster.addKeyspace(newKeyspace, true);
			logger.debug("Keyspace["+keyspaceName+"] is created.");
			logger.debug("ColumnFamily["+columnFamilyName+", " + columnFamilyName +"_count] is created.");
			
			byte[] generatedSalt = PasswordEncryptionService.generateSalt();
			byte[] encryptedPassword = PasswordEncryptionService.getEncryptedPassword(SYSTEM, generatedSalt);
			
			Keyspace keyspace = HFactory.createKeyspace(keyspaceName, cluster);
			Mutator<String> mutator = HFactory.createMutator(keyspace, StringSerializer.get());
			String rowKey = TimeUUIDUtils.getUniqueTimeUUIDinMillis().toString();
			mutator.insert(rowKey, columnFamilyName, HFactory.createStringColumn(ID, SYSTEM));
			mutator.insert(rowKey, columnFamilyName, HFactory.createStringColumn(SALT, Hex.bytesToHex(generatedSalt)));
			mutator.insert(rowKey, columnFamilyName, HFactory.createStringColumn(PASSWORD, Hex.bytesToHex(encryptedPassword)));
			mutator.insert(rowKey, columnFamilyName, HFactory.createStringColumn("oauthProvider", "default"));
			mutator.insert(rowKey, columnFamilyName, HFactory.createStringColumn("created", Long.toString(System.currentTimeMillis())));
			
			mutator.insert("default", columnFamilyName+"_count", HFactory.createColumn("count", new Long(1), StringSerializer.get(), LongSerializer.get()));
		} else {
			logger.debug("Keyspace["+keyspaceName+"] is already exist.");
		}
	}
	
	public void stop() {
		cassandraDaemon.deactivate();
	}
}
