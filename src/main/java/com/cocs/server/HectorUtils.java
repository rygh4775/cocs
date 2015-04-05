package com.cocs.server;

import java.nio.ByteBuffer;
import java.util.Arrays;

import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HSuperColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class it not thread safe.
 * According to Hector's JavaDoc a Mutator isn't thread safe, too.
 * Take a look at {@CassandraClient} for safe usage.
 */
public class HectorUtils<K,T> {

  public static final Logger LOG = LoggerFactory.getLogger(HectorUtils.class);
  
  public static<K> void insertColumn(Mutator<K> mutator, K key, String columnFamily, ByteBuffer columnName, ByteBuffer columnValue) {
    mutator.insert(key, columnFamily, createColumn(columnName, columnValue));
  }

  public static<K> void insertColumn(Mutator<K> mutator, K key, String columnFamily, String columnName, ByteBuffer columnValue) {
    mutator.insert(key, columnFamily, createColumn(columnName, columnValue));
  }
  
  public static<K> void insertColumn(Mutator<K> mutator, K key, String columnFamily, String columnName, String columnValue) {
    mutator.insert(key, columnFamily, createColumn(columnName, columnValue));
  }

  public static<K> HColumn<String, String> createColumn(String name, String value) {
    return HFactory.createColumn(name, value, StringSerializer.get(), StringSerializer.get());
  }
  
  public static<K> HColumn<ByteBuffer,ByteBuffer> createColumn(ByteBuffer name, ByteBuffer value) {
    return HFactory.createColumn(name, value, ByteBufferSerializer.get(), ByteBufferSerializer.get());
  }

  public static<K> HColumn<String,ByteBuffer> createColumn(String name, ByteBuffer value) {
    return HFactory.createColumn(name, value, StringSerializer.get(), ByteBufferSerializer.get());
  }

  public static<K> HColumn<Integer,ByteBuffer> createColumn(Integer name, ByteBuffer value) {
    return HFactory.createColumn(name, value, IntegerSerializer.get(), ByteBufferSerializer.get());
  }


  public static<K> void insertSubColumn(Mutator<K> mutator, K key, String columnFamily, String superColumnName, ByteBuffer columnName, ByteBuffer columnValue) {
    mutator.insert(key, columnFamily, createSuperColumn(superColumnName, columnName, columnValue));
  }

  public static<K> void insertSubColumn(Mutator<K> mutator, K key, String columnFamily, String superColumnName, String columnName, ByteBuffer columnValue) {
    mutator.insert(key, columnFamily, createSuperColumn(superColumnName, columnName, columnValue));
  }

  public static<K> void insertSubColumn(Mutator<K> mutator, K key, String columnFamily, String superColumnName, Integer columnName, ByteBuffer columnValue) {
    mutator.insert(key, columnFamily, createSuperColumn(superColumnName, columnName, columnValue));
  }


  public static<K> void deleteSubColumn(Mutator<K> mutator, K key, String columnFamily, String superColumnName, ByteBuffer columnName) {
    mutator.subDelete(key, columnFamily, superColumnName, columnName, StringSerializer.get(), ByteBufferSerializer.get());
  }


  public static<K> HSuperColumn<String,ByteBuffer,ByteBuffer> createSuperColumn(String superColumnName, ByteBuffer columnName, ByteBuffer columnValue) {
    return HFactory.createSuperColumn(superColumnName, Arrays.asList(createColumn(columnName, columnValue)), StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
  }

  public static<K> HSuperColumn<String,String,ByteBuffer> createSuperColumn(String superColumnName, String columnName, ByteBuffer columnValue) {
    return HFactory.createSuperColumn(superColumnName, Arrays.asList(createColumn(columnName, columnValue)), StringSerializer.get(), StringSerializer.get(), ByteBufferSerializer.get());
  }

  public static<K> HSuperColumn<String,Integer,ByteBuffer> createSuperColumn(String superColumnName, Integer columnName, ByteBuffer columnValue) {
    return HFactory.createSuperColumn(superColumnName, Arrays.asList(createColumn(columnName, columnValue)), StringSerializer.get(), IntegerSerializer.get(), ByteBufferSerializer.get());
  }

}