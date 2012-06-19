package ru.sabstest;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import java.nio.channels.FileChannel;
import java.sql.ResultSet;

public class Pack {
	public static void createRpack()
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);

			db.connect();
			ResultSet rs = db.st.executeQuery("select top 1 isnull(PackFile,'') packfile from dbo.document_bon_pack order by DATE_INSERT desc");
			rs.next();
			String spack = rs.getString("packfile");
			
			File sfile = new File(spack);
			File rfile = new File(Settings.testProj + "tests\\" + Settings.folder + "\\output\\rpack.txt");

			FileInputStream s = new FileInputStream(sfile);
			FileOutputStream r = new FileOutputStream(rfile);
			DataOutputStream rd = new DataOutputStream(r);

			byte a;
			byte[] b = new byte[732];
			byte[] c = new byte[9];

			s.read(b);	
			for(int i=0; i < 9; i++)
			{
				c[i] = b[i + 57];
			}


			LineNumberReader  lnr = new LineNumberReader(new FileReader(sfile));
			lnr.skip(Long.MAX_VALUE);
			int cn = lnr.getLineNumber() - 1;
			
			
			for(int i=0; i < 9; i++)
			{
				a = b[i];
				b[i] = b[i + 38];
				b[i + 38] = a;
			}

			for(int i=0; i < 80; i++)
			{
				a = b[i + 311];
				b[i + 311] = b[i + 391];
				b[i + 391] = a;
			}

			rd.write(b);
			
		//	rd.writeBytes("\r\n");
			
			for(int i=0;i < cn;i++)
			{
				b = new byte[882];
				c = new byte[215];
				s.read(b);
				
				for(int j = 0; j < 153; j++)
				{
					c[j] = b[j];
				}
				char d = "0".toCharArray()[0];
				c[153] = (byte) d;
				c[154] = (byte) d;
				
				d = " ".toCharArray()[0];
				for(int j = 155; j < 215; j++)
				{
					c[j] = (byte) d;
				}
				rd.write(c);
				rd.writeBytes("\r\n");
			}

			Log.msg("Пакет ЭСИС-потверждений " + Settings.testProj + "tests\\" + Settings.folder + "\\output\\rpack.txt" + " создан.");
			s.close();
			rd.close();
			db.close();
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
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
	
	public static String getRPackName()
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();			
			ResultSet rs = db.st.executeQuery("select top 1 isnull(SUBTYPE,'') packfile from dbo.document_bon_pack order by DATE_INSERT desc");
			rs.next();
			String spack = rs.getString("packfile"); //p$1s0302.82o
			db.close();
			return spack.substring(0,3) + "r" + spack.substring(4,11) + "i";
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
			return "";
		}
	}
}
