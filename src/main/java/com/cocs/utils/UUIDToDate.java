package com.cocs.utils;

import java.util.Date;
import java.util.UUID;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;

public class UUIDToDate {
  // This method comes from Hector's TimeUUIDUtils class:
  // https://github.com/rantav/hector/blob/master/core/src/main/java/me/...
  static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;
  public static long getTimeFromUUID(UUID uuid) {
    return (uuid.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000;
  }

  public static void main(String[] args) {
	UUID uniqueTimeUUIDinMillis = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
	System.out.println(uniqueTimeUUIDinMillis);
    String uuidString = uniqueTimeUUIDinMillis.toString();
    UUID uuid = UUID.fromString(uuidString);
    long time = getTimeFromUUID(uuid);
    Date date = new Date(time);
    System.out.println(date);
  }
}