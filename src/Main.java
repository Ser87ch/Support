import java.sql.*;

public class Main {


	public static void main(String[] args) {
		DB db = new DB("CHERNOBRIVENKO\\ATLANT:1259","sabs_zapd","robot","1");

		
		try {
			db.connect();
			//new comment
			ResultSet rs = db.st.executeQuery("select top 5 * from dbo.BNKSEEK");

			int x = rs.getMetaData().getColumnCount();

			while(rs.next()){
				for(int i=1; i<=x;i++){
					System.out.print(rs.getString(i) + "\t");
				}
				System.out.println();
			}
			db.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

}
