package com.cocs.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Vector;

import com.cocs.server.User;

public class BeanUtil {
	public static <T> String[] fieldToArray(Class<T> clazz) {
		Field[] declaredFields = clazz.getDeclaredFields();
		
		Vector<String> vector = new Vector<String>();
		
        for(Field field:declaredFields){
//        	if(!isTransientField(field)) {
        		vector.add(field.getName());
//        	}
        }
    return vector.toArray(new String[vector.size()]);
	}
	
	private static boolean isTransientField( Field field ) {
	      return (field.getModifiers() & Modifier.TRANSIENT) == Modifier.TRANSIENT;
	   }
	
	public static <T> int getFieldCount(Class<T> clazz) {
		return clazz.getDeclaredFields().length;
	}
	
	public static void main(String[] args) {
		String[] names = BeanUtil.fieldToArray(User.class);
		for (String name : names) {
			System.out.println(names.toString());
		}
		
		System.out.println(BeanUtil.getFieldCount(User.class));
	}
}
