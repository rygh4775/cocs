package com.cocs.mail;


public enum Template {
	
	SIGNUP("COCS 회원 가입 인증", "templates/signup.html"), 
	FORGOTPASSWORD("COCS 비밀번호 변경 인증", "templates/forgotpassword.html");
 
	private String subject;
	private String location;
 
	private Template(String subject, String location) {
		this.subject = subject;
		this.location = location;
	}
	
	public String getSubject(){
		return this.subject;
	}
	
	public String getLocation(){
		return this.location;
	}
 
}
