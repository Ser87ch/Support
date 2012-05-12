import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.System;

public class PayDocList {
	private List<PayDoc> pdl;

	PayDocList() {
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();
			
			//получение папки сабс
			//ResultSet rs = db.st.executeQuery("select VALUE from dbo.XDM_CONF_SETTINGS where ID_PARAM = 1004");
		
			ResultSet rs = db.st.executeQuery("SELECT top 1 OPER_DATE as dt FROM XDM_OPERDAY_PROP WHERE VALUE = 0");
			rs.next();
			Date date = rs.getDate("dt");
			System.out.println(date.toString());
//			pdl = new ArrayList<>();
//
//			for(int i = 0; i < Settings.GenDoc.numBIK; i++) {
//				
//
//				for(int j = 0; j < Settings.GenDoc.numberDoc; j++)	{
//
//				}
//			}
			
			db.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	PayDoc get(int i){
		return (PayDoc) pdl.get(i);
	}
}
