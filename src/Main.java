import java.lang.System;

public class Main {
	enum Test {INIT, GEN, PERVVOD}

	public static void main(String[] args)
	{
		Settings.testProj = "G:\\sabstest\\";


		Test t = Test.PERVVOD;
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
		}

		case PERVVOD:
		{			
			Init.load();
			Settings.readXML(Settings.fullfolder + "settings\\general.xml");
			PayDocList pl = new PayDocList();
			pl.readXML(Settings.fullfolder + "input\\paydocs.xml");
			System.out.println(pl.toString());
		}
		}

		Log.close();

		System.out.println();

	}

}
