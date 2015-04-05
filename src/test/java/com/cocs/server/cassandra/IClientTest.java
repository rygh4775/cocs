package com.cocs.server.cassandra;


public interface IClientTest {
	
	public void retrieveServerInfo() ;
	
	public void addKeyspace() ;
	public void dropKeyspace() ;
	public void addKeyspaceWithColumnFamily() ;
	
	public void addCoulmnFamily() ;
	public void dropColumnFamily() ;
	
	public void insertColumn() ;
	public void insertMultipleColumns() ;
	public void deleteColumn() ;
	public void deleteMultipleColumns() ;
	public void getColumn() ;
	public void getMultipleColumns() ;
	public void getAllColumns() ;
	
	
	public void getMultipleRows() ;
}
