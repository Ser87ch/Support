


import java.lang.System;


import ru.sabstest.*;




public class Main {
	enum Test {INIT, GEN, PERVVOD,DDB,RPACK,SPACK,CMP,CMPDELTA,O}

	public static void main(String[] args)
	{
		Settings.testProj = "C:\\sabstest\\";

		Test t = Test.O;
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
			//			DeltaDB.createDBLog();
			//			DeltaDB.createXML("vvod.xml");		
			DeltaDB.deleteDBLog();
			break;
		}
		case RPACK:
		{
			Init.load();
			Settings.readXML(Settings.fullfolder + "settings\\general.xml");
			//XML.validate("C:\\sabstest\\XMLschema\\output\\deltadb.xsd", "C:\\sabstest\\tests\\a000001\\output\\rpack.xml");
			Settings.GenRpack.readXML(Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\genrpack.xml");
			//Pack.createRpackError49();
			//Pack.createBpackError49();
			String s = Pack.getRPackName();
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

			String s = Pack.getSPackName();
			System.out.println(s);
			break;
		}	
		case CMP:
		{
			Init.load();
			Settings.readXML(Settings.fullfolder + "settings\\general.xml");
			System.out.println(Pack.compareSPack(Settings.fullfolder + "etalon\\spack.txt", Settings.fullfolder + "output\\spack.txt"));
			System.out.println(Pack.compareRPack(Settings.fullfolder + "etalon\\rpack.txt", Settings.fullfolder + "output\\rpack.txt"));
			break;
		}
		case CMPDELTA:
		{
			Init.load();
			Settings.readXML(Settings.fullfolder + "settings\\general.xml");
			System.out.println(DeltaDB.cmpDeltaDB("C:\\sabstest\\tests\\a000018\\etalon\\rpack.xml", "C:\\sabstest\\tests\\a000018\\output\\rpack.xml"));
			break;
		}				
		case O:
			Init.load();
			Settings.readXML(Settings.fullfolder + "settings\\general.xml");
			
			PayDoc.Client cl = new PayDoc.Client("044552989","40116810100000000037");
			cl.contrrazr();
			System.out.println(cl.ls);
			Pack.copyBPack("004");
		
			break;
		}

		Log.close();



	}

}
