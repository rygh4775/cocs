package com.cocs.server;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cocs.common.Env;

import me.prettyprint.cassandra.model.BasicColumnDefinition;
import me.prettyprint.cassandra.model.BasicColumnFamilyDefinition;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftCfDef;
import me.prettyprint.cassandra.service.ThriftCluster;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ColumnIndexType;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;

public class CassandraIndexer {
	public static final Logger logger = LoggerFactory.getLogger(CassandraIndexer.class);
	private ThriftCluster cluster;
	private String keyspaceName;

	public CassandraIndexer(ThriftCluster cluster, String keyspaceName, String[] columnsNames) {
		this.cluster = cluster;
		this.keyspaceName = keyspaceName;
		
		for (String columnName : columnsNames) {
			index(columnName);
		}
	}
	
	public void index(String columnName) {
		KeyspaceDefinition keyspaceDefinition = cluster.describeKeyspace(keyspaceName);
		
		BasicColumnFamilyDefinition columnFamilyDefinition = null;
		
		List<ColumnFamilyDefinition> cfDefs = keyspaceDefinition.getCfDefs();
		for (ColumnFamilyDefinition cfdef : cfDefs) {
			String name = cfdef.getName();
			if(Env.getProperty("cassandra.columnfamily.name").equals(name)) {
				columnFamilyDefinition = new BasicColumnFamilyDefinition(cfdef);
			}
		}
	    
	    BasicColumnDefinition columnDefinition = new BasicColumnDefinition();
	    columnDefinition.setName(StringSerializer.get().toByteBuffer(columnName));
	    columnDefinition.setIndexName(columnName + "_index");
	    columnDefinition.setIndexType(ColumnIndexType.KEYS);
	    columnDefinition.setValidationClass(ComparatorType.UTF8TYPE.getClassName());
	    
	    columnFamilyDefinition.addColumnDefinition(columnDefinition);
	    
	    cluster.updateColumnFamily(new ThriftCfDef(columnFamilyDefinition));
	    
	    logger.debug("\"" + columnName + "\"" + " in " + columnFamilyDefinition.getName() + " is indexed.");
	}
}
