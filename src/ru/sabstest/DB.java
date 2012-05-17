package ru.sabstest;

import java.io.Closeable;
import java.sql.*;

public class DB implements Closeable{
	private String server;
	private String db;
	private String user;
	private String pwd;
	private Connection con;
	public Statement st;

	DB(String server, String db, String user, String pwd)
	{
		this.server = server;
		this.db = db;
		this.user = user;
		this.pwd = pwd;
	}

	public void connect() throws Exception 
	{
		try {
			if(con != null)	if (con.isValid(10)) throw new SQLException("Connection have been already opened.");

			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();

			String URL = "jdbc:sqlserver://" + server + ";databaseName=" + db;

			con = DriverManager.getConnection(URL, user, pwd);


			if (con==null) System.exit(0);
			Log.msg("Подключение к " + server + " БД " + db + " выполнено.");
			st = con.createStatement();
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}

	public boolean isConnected() throws SQLException
	{		
		if(con != null)	return con.isValid(10);
		else return false;	

	}

	@Override
	public void close()
	{
		try {
			if(con != null)
			{
				if(con.isValid(10))

				{
					st.close();
					con.close();
					Log.msg("Подключение к " + server + " БД " + db + " закрыто.");
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}

}
