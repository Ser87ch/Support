import java.lang.AutoCloseable;
import java.sql.*;

public class DB implements AutoCloseable{
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

		if(con != null)	if (con.isValid(10)) throw new SQLException("Connection have been already opened.");
		
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
		
		String URL = "jdbc:sqlserver://" + server + ";databaseName=" + db;

		con = DriverManager.getConnection(URL, user, pwd);

		if(con!=null) System.out.println("Connection Successful !\n");
//		if (con==null) System.exit(0);

		st = con.createStatement();

	}

	public boolean isConnected() throws SQLException
	{		
		if(con != null)	return con.isValid(10);
		else return false;	

	}

	@Override
	public void close() throws SQLException
	{
		if(con != null)
		{
			if(con.isValid(10))

			{
				st.close();
				con.close();
			}
		}
	}

}
