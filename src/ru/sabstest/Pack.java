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
import java.text.SimpleDateFormat;


public class Pack {
	
	
	
	public static void createRpack()
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);

			db.connect();
			ResultSet rs = db.st.executeQuery("select top 1 isnull(PackFile,'') packfile from dbo.document_bon_pack where substring(subtype,12,1) = 'o' order by DATE_INSERT desc");
			rs.next();
			String spack = rs.getString("packfile");
			
			File sfile = new File(spack);
			File rfile = new File(Settings.testProj + "tests\\" + Settings.folder + "\\input\\rpack.txt");

			FileInputStream s = new FileInputStream(sfile);
			FileOutputStream r = new FileOutputStream(rfile);
			DataOutputStream rd = new DataOutputStream(r);

			
			byte[] b = new byte[732];
			byte[] c = new byte[205];

			s.read(b);	
			for(int i = 0; i < 9; i++)
			{
				c[i] = b[i];
			}
			
			for(int i = 0; i < 29; i++)
			{
				c[i + 9] = b[i + 9];
			}
			
			for(int i = 0; i < 9; i++)
			{
				c[i + 38] = b[i + 38];
			}
			
			
			for(int i = 0; i < 46; i++)
			{
				c[i + 47] = b[i + 47];
			}
			
			char e;
			e = " ".toCharArray()[0];
			for(int j = 0; j < 87; j++)
			{
				c[j + 93] = (byte) e;
			}			
			byte[] f = new byte[25];
			f = "ÝËÅÊÒÐÎÍÍÎÅ ÏÎÄÒÂÅÐÆÄÅÍÈÅ".getBytes("cp866"); //ÝËÅÊÒÐÎÍÍÎÅ ÏÎÄÒÂÅÐÆÄÅÍÈÅ
			
			for(int j = 0; j < 25; j++)
			{
				c[j + 180] = f[j];
			}
			
			rd.write(c);
			
			rd.writeBytes("\r\n");
			
			LineNumberReader  lnr = new LineNumberReader(new FileReader(sfile));
			lnr.skip(Long.MAX_VALUE);
			int cn = lnr.getLineNumber() - 2;
			
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

			Log.msg("Ïàêåò ÝÑÈÑ-ïîòâåðæäåíèé " + Settings.testProj + "tests\\" + Settings.folder + "\\input\\rpack.txt" + " ñîçäàí.");
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
	
	
	public static String getSPackName()
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();			
			ResultSet rs = db.st.executeQuery("select top 1 isnull(SUBTYPE,'') packfile from dbo.document_bon_pack where substring(SUBTYPE, 4,1) = 's' and substring(SUBTYPE, 12,1) = 'i' and status = 0 and substring(subtype, 5, 4) = '" + new SimpleDateFormat("ddMM").format(Settings.operDate) + "' order by DATE_INSERT desc");
			rs.next();
			String spack = rs.getString("packfile"); //p$9s0302.82o
			db.close();
			String s;
			
			if(spack.equals(""))
			{
				s = "1";				
			}
			else if(spack.substring(2,3).equals("9"))
			{
				s = "A";
			}
			else
			{
				char a = (char)(spack.charAt(2) + 1); 
				s = Character.toString(a);
			}
			
			return "p$" + s + "s" + new SimpleDateFormat("ddMM").format(Settings.operDate) + "." + Settings.bik.substring(4,6) + "i";
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
			return "";
		}
	}
}
