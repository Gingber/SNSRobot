package spader.dao;
import java.sql.*;
public class DAO {
	public Connection con;
	protected DbConnection dbcon;
	
	public DAO(){
		dbcon=new DbConnection();
		con=dbcon.getCon();
	}

}
