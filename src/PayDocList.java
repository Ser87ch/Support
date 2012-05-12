import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class PayDocList {
	private List<PayDoc> pdl;

	PayDocList() {
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();
			
			ResultSet rs = db.st.executeQuery("select VALUE from dbo.XDM_CONF_SETTINGS where ID_PARAM = 1004");
		
			pdl = new ArrayList<>();

			for(int i = 0; i < Settings.GenDoc.numBIK; i++) {
				

				for(int j = 0; j < Settings.GenDoc.numberDoc; j++)	{

				}
			}
			
			db.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	PayDoc get(int i){
		return (PayDoc) pdl.get(i);
	}
}
