
import java.lang.System;

public class Main {


	public static void main(String[] args) {
		PayDoc pd = new PayDoc();
		pd.plat = new PayDoc.Client("040305000", "40102810000000010001");
		
		pd.plat.contrrazr();
		
		System.out.println(pd.plat.ls);
	}

}
