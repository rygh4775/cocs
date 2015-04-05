package org.springframework.beans.factory.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class SelfReferenceSupportPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	protected static Properties properties = null;
	@Override
	protected Properties mergeProperties() throws IOException {
		if (properties != null){
			return properties;
		}

		properties =  super.mergeProperties();

		return reference(properties);

	}
	protected Properties reference(Properties properties) throws FileNotFoundException, IOException{

		for(String propName : properties.stringPropertyNames()){
			referenceParse(properties,propName);
		}

		for(String key : properties.stringPropertyNames()){
			if(key.startsWith("@set.")){
				System.setProperty(StringUtils.substringAfter(key,"@set."), properties.getProperty(key));
			}
		}
		return properties;
	}
	protected String referenceParse(Properties properties ,String propName){
		String value = null;
		if(properties.containsKey(propName)){
			value = properties.getProperty(propName);
			if(StringUtils.isNotBlank(value)){
				String[] selfRefs = StringUtils.substringsBetween(value, "${","}");
				if(selfRefs!=null){
					for(String self:selfRefs){
						value =  StringUtils.replace(value,"${"+self+"}", referenceParse(properties,self)) ;
					}
					properties.put(propName, value);
				}else{
					return value;
				}
			}
		}else{
			value = System.getProperty(propName);
		}

		return value;
	}




}
