import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.lang.Math;

public class PayDocList {
	private List<PayDoc> pdl;

	PayDocList() 
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();

			//получение папки сабс
			//ResultSet rs = db.st.executeQuery("select VALUE from dbo.XDM_CONF_SETTINGS where ID_PARAM = 1004");

			ResultSet rs = db.st.executeQuery("SELECT top 1 OPER_DATE as dt FROM XDM_OPERDAY_PROP WHERE VALUE = 0");
			rs.next();
			Settings.operdate = rs.getDate("dt");

			rs = db.st.executeQuery("select X.BIK as BIK , B.KSNP as KSNP from dbo.XDM_DEPARTMENT X inner join dbo.BNKSEEK B on B.NEWNUM = X.BIK");
			rs.next();
			Settings.bik = rs.getString("BIK");
			Settings.ks = rs.getString("KSNP");

			rs = db.st.executeQuery("select top 1 NUM_ACC from dbo.Account where rest = (select min(rest) from dbo.Account where substring(NUM_ACC,1,1) = '4' and substring(NUM_ACC,1,5) <> '40101')");
			rs.next();
			String ls = rs.getString("NUM_ACC");

			PayDoc.Client plat = new PayDoc.Client(Settings.bik, Settings.ks, ls);

			pdl = new ArrayList<>();

			ResultSet rsbik = db.st.executeQuery("select top " + Settings.GenDoc.numBIK + " NEWNUM, KSNP ksnp from dbo.BNKSEEK where substring(NEWNUM,1,4) = '" + Settings.bik.substring(0, 4) + "' and UER in ('2','3','4','5') and NEWNUM <> '" + Settings.bik + "'");

			while(rsbik.next()) {
				String bikpol = rsbik.getString("NEWNUM");
				String kspol = rsbik.getString("ksnp");
				String lspol = "40702810000000000005";

				PayDoc.Client pol = new PayDoc.Client(bikpol, kspol, lspol, "111111111111", "222222222", "ЗАО Тест");
				pol.contrrazr();

				for(int j = 0; j < Settings.GenDoc.numDoc; j++)
				{
					PayDoc pd = new PayDoc();
					pd.num = Settings.GenDoc.firstDoc + j;
					pd.date = Settings.operdate;
					pd.vidop = "01";
					pd.sum =  ((float) Math.round(new Random().nextFloat() * 10000))/ 100;				
					pd.vidpl = VidPlat.EL;
					pd.plat = plat;
					pd.pol = pol;
					pd.ocher = 6;
					pd.naznach = "Тест";
					pd.datesp = Settings.operdate;
					pd.datepost = Settings.operdate;
					
					pdl.add(pd);
				}
			}

			db.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public PayDoc get(int i)
	{
		return (PayDoc) pdl.get(i);
	}

	@Override
	public String toString()
	{
		String str = "";
		ListIterator <PayDoc> iter = pdl.listIterator();
		while(iter.hasNext())
		{
			str = str + iter.next().toString() + "\n";
		}

		return str;
	}
}

