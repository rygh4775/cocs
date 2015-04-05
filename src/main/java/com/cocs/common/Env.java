package com.cocs.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.cassandra.config.DatabaseDescriptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.SelfReferenceSupportPropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class Env extends SelfReferenceSupportPropertyPlaceholderConfigurer implements ApplicationContextAware{

	private static Env instance;
	private ApplicationContext applicationContext ;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Env.instance = this;
		Env.instance.applicationContext = applicationContext;
		try {
			this.mergeProperties();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Properties mergeProperties() throws IOException {
		Properties mergeProperties = super.mergeProperties();
		return mergeProperties;
	}

	@Override
	protected Properties reference(Properties properties) throws FileNotFoundException, IOException {
		Properties override = new Properties();
		override.put("cassandra.cluster.name", DatabaseDescriptor.getClusterName());
		override.put("cassandra.url", DatabaseDescriptor.getRpcAddress().getHostAddress()+":"+DatabaseDescriptor.getRpcPort());
		
		properties.putAll(override);

		for(String propName : properties.stringPropertyNames()){
			referenceParse(properties,propName);
		}

		return properties;
	}

	public static String getProperty(String key){
		return getProperty(key,null);
	}
	
	public static String getProperty(String key,String defaultValue){
		return properties.getProperty(key,defaultValue);
	}

	public static Properties getProperties(){
		return (Properties)properties.clone();
	}


	public static ApplicationContext getApplicationContext(){
		return Env.instance.applicationContext;
	}

	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}
	
	public static String getRepositoryUploadDirPath() {
		return getRepositoryUploadDirPath(null);
	}
	public static String getRepositoryUploadDirPath(String defaultValue) {
		return getProperty("repository.upload.dir", defaultValue);
	}

}
