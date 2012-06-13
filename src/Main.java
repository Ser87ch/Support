
import java.lang.System;

import ru.sabstest.*;



public class Main {
	enum Test {INIT, GEN, PERVVOD}

	public static void main(String[] args)
	{
		Settings.testProj = "C:\\sabstest\\";


		Test t = Test.INIT;
		switch(t)
		{


		case INIT:
		{
			Init.mkfolder();	
			System.out.println(DeltaDB.toStr());
			DeltaDB.createDBLog();
			DeltaDB.deleteDBLog();
			break;
		}
		case GEN:
		{			
			Init.load();
			Settings.readXML(Settings.fullfolder + "settings\\general.xml");
			Settings.GenDoc.readXML(Settings.fullfolder + "settings\\gendoc.xml");

			PayDocList pl = new PayDocList();
			pl.generate();
			//System.out.println(pl.toString());
			pl.createXML();
			break;
		}

		case PERVVOD:
		{			
			Init.load();
			Settings.readXML(Settings.fullfolder + "settings\\general.xml");
			Settings.GenDoc.readXML(Settings.fullfolder + "settings\\pervvod.xml");
			PayDocList pl = new PayDocList();
			pl.readXML(Settings.fullfolder + "input\\paydocs.xml");
			//System.out.println(pl.toString());
			System.out.println(pl.get(0).toStr("{ENTER}",true));
			break;
		}
		}

		Log.close();

		System.out.println();

	}

}
