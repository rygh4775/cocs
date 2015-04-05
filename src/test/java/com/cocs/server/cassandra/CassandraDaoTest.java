package com.cocs.server.cassandra;

import me.prettyprint.cassandra.dao.SimpleCassandraDao;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml"})
public class CassandraDaoTest {
	
	private static final Logger LOG = Logger.getLogger(CassandraDaoTest.class);
	
	@Autowired SimpleCassandraDao dao;
	@Test
	public void getColumn(){
		String key = "system";
		String name = "passwords";
		String value = dao.get(key, "salt");
		if(value != null) {
			System.out.println("I am here");
		}
		LOG.info("[name: " + name + ", value: " + value + "]");
	}
}
