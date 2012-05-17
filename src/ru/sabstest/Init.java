package ru.sabstest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
			(new File(Settings.fullfolder + "input")).mkdir();

			Log.msg("Папка настроек теста " +Settings.fullfolder + "settings создана.");
			Log.msg("Папка входящих данных для теста " + Settings.fullfolder + "input создана.");

			if(isDefault)
			{
				Settings.readXML(Settings.testProj + "default\\general.xml",true);
				Settings.GenDoc.readXML(Settings.testProj + "default\\gendoc.xml");

			} else {			
				Settings.readXML(Settings.testProj + "tests\\" + copyfolder + "\\settings\\gendoc.xml",true);							
				Settings.GenDoc.readXML(Settings.testProj + "tests\\" + copyfolder + "\\settings\\gendoc.xml");			
			}
			Settings.loadFromDB();
			Settings.createXML();
			Settings.GenDoc.createXML();

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
	
	public static void copyFile(String sourcestr, String deststr)throws IOException {
		File sourceFile = new File(sourcestr);
		File destFile = new File(deststr);
		destFile.createNewFile();
		FileChannel source = null;
		FileChannel destination = null;

		if(!destFile.exists()) {
			destFile.createNewFile();
		}	    

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} 
		finally {
			if(source != null) {
				source.close();
			}
			if(destination != null) {
				destination.close();
			}
		}
	}
}
