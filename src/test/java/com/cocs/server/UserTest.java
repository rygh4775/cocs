package com.cocs.server;

import static org.junit.Assert.fail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cocs.server.dao.UsersDAO;
import com.cocs.server.dao.UsersDAO.ResponseUsers;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml"})
public class UserTest {
	protected static final Log LOG = LogFactory.getLog(UserTest.class);
	
	@Autowired
	UsersDAO usersDAO;
	
	String id = "system";
	String password = "system";
	
	@Test
	public void testPassword() throws Throwable {
		if(usersDAO.equalsPassword(id, password)) {
			User user = usersDAO.getUser(id, null);
			LOG.debug("Encrypted Password : " + user.getPassword());
		} else {
			fail("Password is wrong.");
		}
	}

	@Test
	public void newUser() throws Throwable {
		User user = new User();
		user.setId("system");
		user.setPassword("system");
		user.setOauthProvider("default");
		usersDAO.newUser(user);
	}
	
	@Test
	public void newUsers() throws Throwable {
		for (int i = 0; i < 100; i++) {
			User user = new User();
			user.setId("user"+i);
			user.setOauthProvider("default");
			user.setPassword("1111");
			usersDAO.newUser(user);
		}
	}
	
	@Test
	public void getUser() throws Throwable {
		User user = usersDAO.getUser("user56", "default");
		if(user == null) {
			fail("User is not exists.");
 		}
		LOG.debug(user.toString());
		System.out.println(user.getCreated());
	}
	
	@Test
	public void deleteUser() throws Throwable {
		usersDAO.deleteUser("user56", "default");
	}
	
	@Test
	public void getAllUsersWithPaging() {
		String lastKey = "";
		int totalCount = 0;
		while(true) {
			ResponseUsers allUsers = usersDAO.getAllUsers(null, lastKey, 20);
			if(lastKey.equals(allUsers.getLastKey())) {
				break;
			}
			for (Object user : allUsers.getRows()) {
				System.out.println(((User)user).getId());
			}
			System.out.println("------next------");
			lastKey = allUsers.getLastKey();
			totalCount++;
		}
		
		System.out.println("Total page - " + totalCount);
	}
	
	@Test
	public void getTotalCount() {
		long totalCount = usersDAO.getTotalCount("default");
		System.out.println(totalCount);
	}
	
}
