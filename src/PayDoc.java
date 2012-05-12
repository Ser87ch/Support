import java.sql.Date;
import java.lang.Character;

enum VidPlat { POCHT, TEL, EL, NO }

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

	static class Client {
		public String bik;
		public String ks;
		public String ls;
		public String inn;
		public String kpp;
		public String name;

		Client(String bik, String ls) {
			this.bik = bik;			
			this.ls = ls;
			
		}
		Client(String bik, String ks , String ls, String inn, String kpp, String name) {
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
