package com.cocs.server.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import net.sf.json.JSONObject;

import org.apache.cassandra.utils.Hex;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.cocs.handler.ResponseRows;
import com.cocs.security.PasswordEncryptionService;
import com.cocs.server.User;
import com.cocs.utils.BeanUtil;

@Service
public class UsersDAO {
	private String columnFamilyName;
	private Keyspace keyspace;
	private final StringSerializer serializer = StringSerializer.get();
	
	@SuppressWarnings("unchecked")
	public void newUser(User user) throws Throwable {
		user.setCreated(Long.toString(System.currentTimeMillis()));
		if(StringUtils.isBlank(user.getOauthProvider())) {
			user.setOauthProvider("default");
		}
		
		if("default".equals(user.getOauthProvider())) {
			byte[] salt = PasswordEncryptionService.generateSalt();
			byte[] encryptedPassword = PasswordEncryptionService.getEncryptedPassword(user.getPassword(), salt);
			
			user.setSalt(Hex.bytesToHex(salt));
			user.setPassword(Hex.bytesToHex(encryptedPassword));
		}
		
		Mutator<String> mutator = HFactory.createMutator(keyspace, serializer);
		
		Map<String, String> userMap = JSONObject.fromObject(user);
		String rowKey = TimeUUIDUtils.getUniqueTimeUUIDinMillis().toString();
		for (Map.Entry<String, String> keyValue : userMap.entrySet()) {
			if("key".equals(keyValue.getKey())) continue;
			mutator.addInsertion(rowKey, columnFamilyName, HFactory.createStringColumn(keyValue.getKey(), keyValue.getValue()));
		}
		mutator.execute();
		
		incrementTotalCount(user.getOauthProvider());
	}

	public User getUser(String id, String oauthProvider) throws Throwable {
		if(StringUtils.isBlank(oauthProvider)) {
			oauthProvider = "default";
		}
		
		RangeSlicesQuery<String,String,String> rangeSlicesQuery = HFactory.createRangeSlicesQuery(keyspace, StringSerializer.get(), StringSerializer.get(), StringSerializer.get());
		rangeSlicesQuery.setColumnFamily(columnFamilyName);
		rangeSlicesQuery.setColumnNames(BeanUtil.fieldToArray(User.class));
		rangeSlicesQuery.addEqualsExpression("oauthProvider", oauthProvider);
		rangeSlicesQuery.addEqualsExpression("id", id);
		
		QueryResult<OrderedRows<String,String,String>> result = rangeSlicesQuery.execute();
		OrderedRows<String,String,String> orderedRows = result.get();
		if(orderedRows.getCount() < 1) {
			return null;
		}
		if(orderedRows.getCount() > 1) {
			throw new Exception("User is duplicated.");
		}
		Row<String, String, String> row = orderedRows.getList().get(0);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("key", row.getKey());
		List<HColumn<String,String>> columns = row.getColumnSlice().getColumns();
		for (HColumn<String, String> hColumn : columns) {
			jsonObject.put(hColumn.getName(), hColumn.getValue());
		}
		
		if(jsonObject.size() != 0) {
			return (User) JSONObject.toBean(jsonObject, User.class);
		}
		
		return null;
	}
	
	public ResponseUsers getAllUsers(String oauthProvider, String fisrtKey, int rowCount) {
		RangeSlicesQuery<String,String,String> rangeSlicesQuery = HFactory.createRangeSlicesQuery(keyspace, StringSerializer.get(), StringSerializer.get(), StringSerializer.get());
		rangeSlicesQuery.setColumnFamily(columnFamilyName);
		rangeSlicesQuery.setColumnNames(BeanUtil.fieldToArray(User.class));
		
		if(StringUtils.isNotBlank(oauthProvider)) {
			rangeSlicesQuery.addEqualsExpression("oauthProvider", oauthProvider);
		}
		
		rangeSlicesQuery.setRange(null, null, false, BeanUtil.getFieldCount(User.class));
		if(StringUtils.isNotBlank(fisrtKey)) {
			rowCount ++;
		}
		rangeSlicesQuery.setRowCount(rowCount);
		rangeSlicesQuery.setKeys(fisrtKey, null);
		
		QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
        OrderedRows<String, String, String> rows = result.get();
        Iterator<Row<String, String, String>> rowsIterator = rows.iterator();
        
        ArrayList<User> users = new ArrayList<User>();
        while (rowsIterator.hasNext()) {
        	JSONObject user = new JSONObject();
            Row<String, String, String> row = rowsIterator.next();
            String key = row.getKey();
            List<HColumn<String, String>> columns = row.getColumnSlice().getColumns();
            if(key.equals(fisrtKey) || columns.size() == 0) {
            	continue;
            }
            
            user.put("key", key);
			for (HColumn<String, String> hColumn : columns) {
				user.put(hColumn.getName(), hColumn.getValue());
			}
			
			users.add((User) JSONObject.toBean(user, User.class));
        }
        
        if(rows.getCount() < 1) {
        	return new ResponseUsers(users, null);
        }
        ResponseUsers responseUsers = new ResponseUsers(users, rows.peekLast().getKey());
        responseUsers.setTotalCount(getTotalCount(oauthProvider));
        return responseUsers;
	}

	@SuppressWarnings("unchecked")
	public void updateUser(User user, String oauthProvider) throws Throwable {
		user.setModified(Long.toString(System.currentTimeMillis()));
		if(StringUtils.isBlank(oauthProvider)) {
			oauthProvider = "default";
		}
		
		RangeSlicesQuery<String,String,String> rangeSlicesQuery = HFactory.createRangeSlicesQuery(keyspace, StringSerializer.get(), StringSerializer.get(), StringSerializer.get());
		rangeSlicesQuery.setColumnFamily(columnFamilyName);
		rangeSlicesQuery.setColumnNames("id");
		rangeSlicesQuery.addEqualsExpression("oauthProvider", oauthProvider);
		rangeSlicesQuery.addEqualsExpression("id", user.getId());
		
		QueryResult<OrderedRows<String,String,String>> result = rangeSlicesQuery.execute();
		OrderedRows<String,String,String> orderedRows = result.get();
		if(orderedRows.getCount() < 1) {
			throw new Exception("User is not exists.");
		}
		if(orderedRows.getCount() > 1) {
			throw new Exception("User is duplicated.");
		}
		Row<String, String, String> row = orderedRows.getList().get(0);
		
		Mutator<String> mutator = HFactory.createMutator(keyspace, serializer);
		Map<String, String> userMap = JSONObject.fromObject(user);
		for (Map.Entry<String, String> keyValue : userMap.entrySet()) {
			if("key".equals(keyValue.getKey())) continue;
			mutator.addInsertion(row.getKey(), columnFamilyName, HFactory.createStringColumn(keyValue.getKey(), keyValue.getValue()));
		}
		mutator.execute();
	}
	
	public void changePassword(String userId, String newPassword) throws Throwable {
		User user = getUser(userId, "default");
		byte[] encryptedPassword = PasswordEncryptionService.getEncryptedPassword(newPassword, Hex.hexToBytes(user.getSalt()));
		user.setPassword(Hex.bytesToHex(encryptedPassword));
		updateUser(user, "default");
	}
	
	public boolean equalsPassword(String id, String password) throws Throwable {
		RangeSlicesQuery<String,String,String> rangeSlicesQuery = HFactory.createRangeSlicesQuery(keyspace, StringSerializer.get(), StringSerializer.get(), StringSerializer.get());
		rangeSlicesQuery.setColumnFamily(columnFamilyName);
		rangeSlicesQuery.addEqualsExpression("oauthProvider", "default");
		rangeSlicesQuery.addEqualsExpression("id", id);
		rangeSlicesQuery.setRange("password", "salt", false, 2);
		
		QueryResult<OrderedRows<String,String,String>> result = rangeSlicesQuery.execute();
		OrderedRows<String,String,String> orderedRows = result.get();
		if(orderedRows.getCount() < 1) {
			return false;
		}
		if(orderedRows.getCount() > 1) {
			throw new Exception("User is duplicated.");
		}
		Row<String, String, String> row = orderedRows.getList().get(0);
		
		List<HColumn<String,String>> columns = row.getColumnSlice().getColumns();
		
		String salt = null;
		String encryptedPassword = null;
		for (HColumn<String, String> hColumn : columns) {
			if("password".equals(hColumn.getName())) {
				encryptedPassword = hColumn.getValue();
			} else if("salt".equals(hColumn.getName())) {
				salt = hColumn.getValue();
			}
		}
		
		if(StringUtils.isBlank(salt)) {
			return false;
		}
		
		return PasswordEncryptionService.authenticate(password, Hex.hexToBytes(encryptedPassword), Hex.hexToBytes(salt));
	}
	
	public void deleteUser(String id, String oauthProvider) throws Throwable {
		User user = getUser(id, oauthProvider);
		if(user != null) {
			Mutator<String> mutator = HFactory.createMutator(keyspace, StringSerializer.get());
			mutator.addDeletion(user.getKey(), columnFamilyName, null, StringSerializer.get());
			mutator.execute();
			
			decrementTotalCount(oauthProvider);
		}
	}
	
	public boolean exists(String id, String oauthProvider) throws Throwable{
		User user = getUser(id, oauthProvider);
		return (user == null) ? false : true;
	}
	
	public void setColumnFamilyName(String columnFamilyName) {
		this.columnFamilyName = columnFamilyName;
	}

	public void setKeyspace(Keyspace keyspace) {
		this.keyspace = keyspace;
	}
	
	public long getTotalCount(String oauthProvider) {
		String[] keys = {oauthProvider};
		if(StringUtils.isBlank(oauthProvider)) {
			keys = new String[]{"default", "facebook", "twitter"};
		}
		
		MultigetSliceQuery<String,String,Long> MultigetSliceQuery = HFactory.createMultigetSliceQuery(keyspace, StringSerializer.get(), StringSerializer.get(), LongSerializer.get());
		MultigetSliceQuery.setColumnFamily(columnFamilyName+"_count").setKeys(keys).setColumnNames("count");
		QueryResult<Rows<String,String,Long>> execute = MultigetSliceQuery.execute();
		Rows<String, String, Long> rows = execute.get();
		
		long totalCount = 0;
		for (String key : keys) {
			Row<String, String, Long> row = rows.getByKey(key);
			ColumnSlice<String, Long> columnSlice = row.getColumnSlice();
			HColumn<String, Long> columnByName = columnSlice.getColumnByName("count");
			if(columnByName!=null) {
				totalCount += columnByName.getValue();
			}
		}
		return totalCount;
	}
	
	private synchronized void incrementTotalCount(String oauthProvier) {
		long totalCount = getTotalCount(oauthProvier);
		totalCount++;
		
		Mutator<String> mutator = HFactory.createMutator(keyspace, StringSerializer.get());
		mutator.insert(oauthProvier, columnFamilyName+"_count", HFactory.createColumn("count", totalCount, StringSerializer.get(), LongSerializer.get()));
		mutator.execute();
	}
	
	private synchronized void decrementTotalCount(String oauthProvier) {
		long totalCount = getTotalCount(oauthProvier);
		totalCount--;
		
		Mutator<String> mutator = HFactory.createMutator(keyspace, StringSerializer.get());
		mutator.insert(oauthProvier, columnFamilyName+"_count", HFactory.createColumn("count", totalCount, StringSerializer.get(), LongSerializer.get()));
		mutator.execute();
	}
	
	public class ResponseUsers extends LinkedHashMap<String, Object> implements ResponseRows<String, Object>{

		private static final long serialVersionUID = -4226440375227896678L;

		public ResponseUsers(ArrayList<User> list, String lastKey) {
			super();
			put("rows", list);
			put("lastKey", lastKey);
			put("totalCount", list.size());
		}

		@Override
		public Collection getRows(){
			return (Collection<User>)get("rows");
		}

		public String getLastKey(){
			return (String)get("lastKey");
		}
		
		public void setTotalCount(long count){
			put("totalCount",count);
		}

		@SuppressWarnings("unchecked")
		@Override
		public long getTotalCount(){
			if (containsKey("totalCount")){
				return Long.parseLong(String.valueOf(get("totalCount")));
			} else {
				return ((List<Object>)get("rows")).size();
			}
		}

	}
}
