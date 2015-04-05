package com.cocs.service.naver;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.cocs.service.AbstractServiceTest;
import com.cocs.service.IServiceTest;

import facebook4j.Facebook;

public class UserTest extends AbstractServiceTest implements IServiceTest.UserTest{
	private static Facebook client;
	
	@BeforeClass
	public static void beforeClass() {
		client = getFacebookClient();
	}
	
	@Override
	@Test
	public void getInfo(){
		String accessToken = "AAAARk0bYo1Ds4wedsU4AaVsX6k0xAgxuzNVPTwlHcQ8xaWu7tWo20UBmn8DONwls8zJlH2IcSEwl76cBWQ7uLPdjcdAag/7woDUcmZlV4AE/Aob";
		
		HttpClient client = new HttpClient();
		
		HttpMethod method = new PostMethod("https://apis.naver.com/nidlogin/nid/getUserProfile.xml");
		method.setRequestHeader("Authorization", "Bearer " + accessToken);
		try {
			client.executeMethod(method);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(method.getResponseBodyAsString()));
            Document doc = builder.parse(is);
            NodeList list = doc.getElementsByTagName("email");
            System.out.println(list.item(0).getTextContent());
		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
		
		
	}

}
