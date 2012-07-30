package ru.sabstest;

import java.io.File;
import java.text.DecimalFormat;


public class Init {
	static boolean isDefault = true;
	static String copyfolder = "";

	public static void mkfolder()
	{
		try {		

			File dir = new File(Settings.testProj + "tests\\");
			
			String[] children = dir.list();
			if (children == null || children.length == 0) {
				Settings.folder = "a000001";
			} else {
				String filename = children[children.length - 1];
				if (filename.substring(1, 6).equals("999999"))
				{
					char s = (char)(filename.charAt(0) +  1);
					Settings.folder = Character.toString(s) + "000001";
				} else {
					int i = Integer.parseInt(filename.substring(1, 7)) + 1;
					DecimalFormat myFormat = new java.text.DecimalFormat("000000");
					Settings.folder = filename.substring(0, 1) + myFormat.format(new Integer(i));
				}
			}
			(new File(Settings.testProj + "tests\\" + Settings.folder)).mkdir();
			Settings.fullfolder = Settings.testProj + "tests\\" + Settings.folder + "\\";

			Log.create();
			Log.msg("Папка теста " + Settings.fullfolder + " создана.");

			(new File(Settings.fullfolder + "settings")).mkdir();
			(new File(Settings.fullfolder + "settings\\" + Settings.pervfolder)).mkdir();
			(new File(Settings.fullfolder + "settings\\" + Settings.obrfolder)).mkdir();
			(new File(Settings.fullfolder + "input")).mkdir();
			(new File(Settings.fullfolder + "output")).mkdir();

			Log.msg("Папка настроек теста " +Settings.fullfolder + "settings создана.");
			Log.msg("Папка настроек теста " +Settings.fullfolder + "settings\\" + Settings.pervfolder + " создана.");
			Log.msg("Папка настроек теста " +Settings.fullfolder + "settings\\" + Settings.obrfolder + " создана.");
			Log.msg("Папка входящих данных для теста " + Settings.fullfolder + "input создана.");
			Log.msg("Папка исходящих данных для теста " + Settings.fullfolder + "output создана.");

			if(isDefault)
			{
				Settings.readXML(Settings.testProj + "default\\general.xml",true);
				Settings.GenDoc.readXML(Settings.testProj + "default\\" + Settings.pervfolder + "\\gendoc.xml");
				Settings.PerVvod.readXML(Settings.testProj + "default\\" + Settings.pervfolder + "\\pervvod.xml");
				Settings.ContrVvod.readXML(Settings.testProj + "default\\" + Settings.pervfolder + "\\contrvvod.xml");
				Settings.FormES.readXML(Settings.testProj + "default\\" + Settings.pervfolder + "\\formes.xml");
				Settings.ContrES.readXML(Settings.testProj + "default\\" + Settings.pervfolder + "\\contres.xml");
				Settings.GenRpack.readXML(Settings.testProj + "default\\" + Settings.pervfolder + "\\genrpack.xml");
				Settings.GenSpack.readXML(Settings.testProj + "default\\" + Settings.obrfolder + "\\genspack.xml");
				DeltaDB.readXMLSettings(Settings.testProj + "default\\deltadb.xml");

			} else {			
				Settings.readXML(Settings.testProj + "tests\\" + copyfolder + "\\settings\\" + Settings.pervfolder + "\\gendoc.xml",true);							
				Settings.GenDoc.readXML(Settings.testProj + "tests\\" + copyfolder + "\\settings\\" + Settings.pervfolder + "\\gendoc.xml");	
				Settings.PerVvod.readXML(Settings.testProj + "tests\\" + copyfolder + "\\settings\\" + Settings.pervfolder + "\\pervvod.xml");
				Settings.ContrVvod.readXML(Settings.testProj + "tests\\" + copyfolder + "\\settings\\" + Settings.pervfolder + "\\contrvvod.xml");
				Settings.FormES.readXML(Settings.testProj + "tests\\" + copyfolder + "\\settings\\" + Settings.pervfolder + "\\formes.xml");
				Settings.ContrES.readXML(Settings.testProj + "tests\\" + copyfolder + "\\settings\\" + Settings.pervfolder + "\\contres.xml");
				Settings.GenRpack.readXML(Settings.testProj + "tests\\" + copyfolder + "\\settings\\" + Settings.pervfolder + "\\genrpack.xml");
				Settings.GenSpack.readXML(Settings.testProj + "tests\\" + copyfolder + "\\settings\\" + Settings.obrfolder + "\\genspack.xml");
				DeltaDB.readXMLSettings(Settings.testProj + "tests\\" + copyfolder + "\\settings\\deltadb.xml");
			}
			
			Settings.loadFromDB();
			Settings.createXML();
			Settings.GenDoc.createXML();
			Settings.PerVvod.createXML();
			Settings.ContrVvod.createXML();
			Settings.FormES.createXML();
			Settings.ContrES.createXML();
			Settings.GenRpack.createXML();
			Settings.GenSpack.createXML();
			DeltaDB.createXMLSettings();
			
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}

	}

	public static void load()
	{
		File dir = new File(Settings.testProj + "tests\\");
		
		String[] children = dir.list();
		String filename = children[children.length - 1];
		Settings.folder = filename;
		Settings.fullfolder = Settings.testProj + "tests\\" + Settings.folder + "\\";
		
		Log.create();
		
	}
	
	
}
