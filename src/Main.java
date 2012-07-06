


import java.lang.System;
import ru.sabstest.*;




public class Main {
	enum Test {INIT, GEN, PERVVOD,DDB,RPACK,SPACK}

	public static void main(String[] args)
	{
		Settings.testProj = "C:\\sabstest\\";
		
		Test t = Test.SPACK;
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
			Settings.GenDoc.readXML(Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\pervvod.xml");
			PayDocList pl = new PayDocList();
			pl.readXML(Settings.fullfolder + "input\\" + Settings.pervfolder + "\\paydocs.xml");
			//System.out.println(pl.toString());
			System.out.println(pl.get(0).toStr("{ENTER}",true));
			break;
		}
		case DDB:
		{
			Init.load();
			Settings.readXML(Settings.fullfolder + "settings\\general.xml");
			DeltaDB.readXMLSettings(Settings.fullfolder + "settings\\deltadb.xml");
			DeltaDB.createXML("vvod.xml");			
			break;
		}
		case RPACK:
		{
			Init.load();
			Settings.readXML(Settings.fullfolder + "settings\\general.xml");
			//XML.validate("C:\\sabstest\\XMLschema\\output\\deltadb.xsd", "C:\\sabstest\\tests\\a000001\\output\\rpack.xml");
			Settings.GenRpack.readXML(Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\genrpack.xml");
			Pack.createRpack();
		
			break;
		}
		case SPACK:
		{
			Init.load();
			Settings.readXML(Settings.fullfolder + "settings\\general.xml");			
			Settings.GenSpack.readXML(Settings.fullfolder + "settings\\" + Settings.obrfolder + "\\genspack.xml");
			
			PayDocList pl = new PayDocList();
			pl.generateS();
			pl.createSpack();
		
			break;
		}		
		}
		
		Log.close();

		System.out.println();

	}

}
