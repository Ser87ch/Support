

import java.lang.System;
import ru.sabstest.Init;
import ru.sabstest.Log;
import ru.sabstest.PayDocList;
import ru.sabstest.Settings;




public class Main {
	enum Test {INIT, GEN, PERVVOD}

	public static void main(String[] args)
	{
		Settings.testProj = "G:\\sabstest\\";


		Test t = Test.GEN;
		switch(t)
		{


		case INIT:
		{
			Init.mkfolder();			
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
			PayDocList pl = new PayDocList();
			pl.readXML(Settings.fullfolder + "input\\paydocs.xml");
			System.out.println(pl.toString());
			break;
		}
		}

		Log.close();

		System.out.println();

	}

}
