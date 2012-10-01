package ru.sabstest;

import java.sql.Date;
import java.lang.Character;
import java.text.SimpleDateFormat;




public class PayDoc {
	public int num;
	public Date date;
	public String vidop;
	public float sum;
	public VidPlat vidpl;
	public Client plat;
	public Client pol;
	public int ocher;
	public String status;
	public String kbk;
	public String okato;
	public String osn;
	public String nalper;
	public String numdoc;
	public String datedoc;
	public String typepl;
	public String naznach;
	public Date datesp;
	public Date datepost;
	public int elnum;
	
	PayDoc()
	{	
		this.num = 0;
		this.date = new Date(0);
		this.vidop = "";
		this.sum = 0;				
		this.vidpl = VidPlat.EL;
		this.ocher = 6;
		this.status = "";
		this.kbk = "";
		this.okato = "";
		this.osn = "";
		this.nalper = "";
		this.numdoc = "";
		this.datedoc = "";
		this.typepl = "";		
		this.naznach = "Тест";
		this.datesp = new Date(0);
		this.datepost = new Date(0);	
		this.elnum = 0;
	}

	@Override
	public String toString()
	{
		return toStr(" ", false);
	}

	public String toStr(String razd, boolean addShift){
		String str = "";

		str = Integer.toString(num) + razd + new SimpleDateFormat("ddMMyyyy").format(date) + razd + vidop + razd + Float.toString(sum) + razd + vidpl.toString() + razd + 
		plat.bik + razd + plat.ks + razd + plat.ls + razd + plat.inn + razd + plat.kpp + razd + plat.name + razd + pol.bik + razd + pol.ks + razd + pol.ls + razd + pol.inn + razd + pol.kpp + razd 
		+ (addShift ? "+{ExtEnd}" : "") + pol.name + razd +
		Integer.toString(ocher) + razd + status;
		if(status != "" && status != null)
			str = str + razd + kbk + razd + okato + razd + osn + razd + nalper + razd + numdoc + razd + datedoc + razd + typepl;

		str = str + razd + naznach + razd + new SimpleDateFormat("ddMMyyyy").format(datesp) + razd + new SimpleDateFormat("ddMMyyyy").format(datepost);
		return str;	
	}

	public String toStrContr(String razd){

		String str = "";

		str = Integer.toString(num) + razd + new SimpleDateFormat("ddMMyyyy").format(date) + razd + vidop + razd + Float.toString(sum) + razd + vidpl.toString() + razd + 
		plat.bik + razd + plat.ks + razd + plat.ls + razd + pol.bik + razd + pol.ks + razd + pol.ls + razd + Integer.toString(ocher);

		return str;	
	}

	public static class Client {
		public String bik;
		public String ks;
		public String ls;
		public String inn;
		public String kpp;
		public String name;

		public Client(String bik, String ls) {
			this.bik = bik;	
			this.ks = "";
			this.ls = ls;
			this.inn = "";
			this.kpp = "";
			this.name = "";

		}
		public Client(String bik, String ks , String ls) {
			this.bik = bik;
			this.ks = ks;
			this.ls = ls;	
			this.inn = "";
			this.kpp = "";
			this.name = "";
		}
		public Client(String bik, String ks , String ls, String inn, String kpp, String name) {
			this.bik = bik;
			this.ks = ks;
			this.ls = ls;
			this.inn = inn;
			this.kpp = kpp;
			this.name = name;
		}
		public void contrrazr() {
			String contrls;

			if(bik.substring(6,9).equals("000") || bik.substring(6,9).equals("001") || bik.substring(6,9).equals("002")) {
				contrls = "0" + bik.substring(4,6) + ls.substring(0, 8) + "0" + ls.substring(9, 20);
			}
			else {
				contrls = bik.substring(6,9) + ls.substring(0, 8) + "0" + ls.substring(9, 20);
			}

			int contr = 0, k;

			for(k = 0; k < 23; k++) {
				switch (k % 3) {
				case 0: contr =  (Character.getNumericValue(contrls.charAt(k)) * 7) % 10 + contr ;
				break;
				case 1: contr =  (Character.getNumericValue(contrls.charAt(k)) * 1) % 10 + contr ;
				break;
				case 2: contr =  (Character.getNumericValue(contrls.charAt(k)) * 3) % 10 + contr ;
				break;
				}

			}

			contr = ((contr % 10) * 3) % 10;

			ls = ls.substring(0, 8) + Integer.toString(contr) + ls.substring(9, 20);

		}
	}


}
