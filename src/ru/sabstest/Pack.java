package ru.sabstest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


public class Pack {



	public static void createRpack()
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);

			db.connect();
			ResultSet rs = db.st.executeQuery("select top 1 isnull(PackFile,'') packfile from dbo.document_bon_pack where substring(subtype,12,1) = 'o' and substring(subtype,4,1) = 's' order by DATE_INSERT desc");
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
			f = "ЭЛЕКТРОННОЕ ПОДТВЕРЖДЕНИЕ".getBytes("cp866"); //ЭЛЕКТРОННОЕ ПОДТВЕРЖДЕНИЕ

			for(int j = 0; j < 25; j++)
			{
				c[j + 180] = f[j];
			}

			rd.write(c);

			rd.writeBytes("\r\n");
			
			//кол-во строк
			byte[] arr = new byte[9];
			for(int i = 0; i < 9; i++)
			{
				arr[i] = b[i + 57];
			}
		
			String scn = new String(arr);
			int cn = Integer.parseInt(scn);
			
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

			Log.msg("Пакет ЭСИС-потверждений " + Settings.testProj + "tests\\" + Settings.folder + "\\input\\rpack.txt" + " создан.");
			s.close();
			rd.close();
			db.close();
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}

	
	public static void createBpack()
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);

			db.connect();
			ResultSet rs = db.st.executeQuery("select top 1 isnull(PackFile,'') packfile from dbo.document_bon_pack where substring(subtype,12,1) = 'o' and substring(subtype,4,1) = 's' order by DATE_INSERT desc");
			rs.next();
			String spack = rs.getString("packfile");

			File sfile = new File(spack);
			File bfile = new File(Settings.testProj + "tests\\" + Settings.folder + "\\input\\bpack.txt");

			FileInputStream s = new FileInputStream(sfile);
			FileOutputStream r = new FileOutputStream(bfile);
			DataOutputStream rd = new DataOutputStream(r);
			
			
			byte[] b = new byte[732];
			byte[] c = new byte[730];

			s.read(b);	
			for(int i = 0; i < 101; i++)
			{
				c[i] = b[i];
			}
			
			byte[] f = new byte[16];
			f = "ВОЗВРАТ ПЛАТЕЖЕЙ".getBytes("cp866"); //ЭЛЕКТРОННОЕ ПОДТВЕРЖДЕНИЕ

			for(int j = 0; j < 16; j++)
			{
				c[j + 101] = f[j];
			}

			char e;
			e = " ".toCharArray()[0];
			for(int j = 0; j < 196; j++)
			{
				c[j + 117] = (byte) e;
			}
			
			for(int i = 0; i < 419; i++)
			{
				c[i + 311] = b[i + 311];
			}
			
			rd.write(c);

			rd.writeBytes("\r\n");
			
			
			Log.msg("Пакет ЭПС по возврату платежей " + Settings.testProj + "tests\\" + Settings.folder + "\\input\\bpack.txt" + " создан.");
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
			Log.msg("Файл " + sourcestr + "скопирован в " + deststr + " .");
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
			ResultSet rs = db.st.executeQuery("select top 1 isnull(SUBTYPE,'') packfile from dbo.document_bon_pack where substring(SUBTYPE, 4,1) = 'r' and substring(SUBTYPE, 12,1) = 'i' and status = 0 and substring(subtype, 5, 4) = '" + new SimpleDateFormat("ddMM").format(Settings.operDate) + "' order by DATE_INSERT desc");
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

			Log.msg("Имя файла S пакета " + "p$" + s + "r" + new SimpleDateFormat("ddMM").format(Settings.operDate) + "." + Settings.bik.substring(4,6) + "i .");
			return "p$" + s + "r" + new SimpleDateFormat("ddMM").format(Settings.operDate) + "." + Settings.bik.substring(4,6) + "i";
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

			Log.msg("Имя файла S пакета " + "p$" + s + "s" + new SimpleDateFormat("ddMM").format(Settings.operDate) + "." + Settings.bik.substring(4,6) + "i .");
			return "p$" + s + "s" + new SimpleDateFormat("ddMM").format(Settings.operDate) + "." + Settings.bik.substring(4,6) + "i";
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
			return "";
		}
	}

	public static void copySPack()
	{
		try{
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);

			db.connect();
			ResultSet rs = db.st.executeQuery("select top 1 isnull(PackFile,'') packfile from dbo.document_bon_pack where substring(subtype,12,1) = 'o' order by DATE_INSERT desc");
			rs.next();
			String spack = rs.getString("packfile");

			copyFile(spack,Settings.testProj + "\\tests\\" + Settings.folder + "\\output\\spack.txt");
			
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}

	public static void copyRPack()
	{
		try{
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);

			db.connect();
			ResultSet rs = db.st.executeQuery("select top 1 isnull(PackFile,'') packfile from dbo.document_bon_pack where substring(subtype,12,1) = 'o' order by DATE_INSERT desc");
			rs.next();
			String spack = rs.getString("packfile");

			copyFile(spack,Settings.testProj + "\\tests\\" + Settings.folder + "\\output\\rpack.txt");

		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}

	public static boolean compareSPack(String etal, String fl)
	{
		SPack.loadMask();

		SPack et = new SPack(etal);
		et.load();

		SPack sp = new SPack(fl);
		sp.load();

		if(et.equals(sp))
			Log.msgCMP("S пакет " + fl + " совпадает с эталонным S пакетом " + fl + " по маске spack.msk .");
		else
			Log.msgCMP("S пакет " + fl + " не совпадает с эталонным S пакетом " + fl + " по маске spack.msk .");
		return et.equals(sp);
	}
	
	public static boolean compareRPack(String etal, String fl)
	{
		SPack.loadMaskR();

		SPack et = new SPack(etal);
		et.load();

		SPack sp = new SPack(fl);
		sp.load();

		if(et.equals(sp))
			Log.msgCMP("R пакет " + fl + " совпадает с эталонным R пакетом " + fl + " по маске spack.msk .");
		else
			Log.msgCMP("R пакет " + fl + " не совпадает с эталонным R пакетом " + fl + " по маске spack.msk .");
		return et.equals(sp);
	}

	public static void copyPack(String src, String dest)
	{
		try {
			DB db2 = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db2.connect();
			
			BufferedReader br;

			br = new BufferedReader(new FileReader(src));
			String tmp;
			tmp = br.readLine();
			
			FileWriter fstream = new FileWriter(dest);
			BufferedWriter out = new BufferedWriter(fstream);	
			out.write(tmp);
			out.flush();
			
			String uic = "", uicprev = "", otd = "";
			int elnum = 0, ndoc = 0;
			while((tmp = br.readLine()) != null)
			{
				out.newLine();
				uicprev = uic;
				uic = tmp.substring(14, 24);
				otd = tmp.substring(10, 14) + "-" +  tmp.substring(8, 10) + "-" + tmp.substring(6, 8); 
				elnum++;
				ndoc++;
				
				if(!uic.equals(uicprev))
				{
					String s = "select isnull(max(elnum),0) as el, isnull(max(n_doc),0) as ndoc from dbo.DOCUMENT_BON where date_doc = '" + otd + "' and uic = '" + uic + "'";		
					ResultSet rsel = db2.st.executeQuery(s);					
					rsel.next();
					elnum = rsel.getInt("el") + 1;
					ndoc = rsel.getInt("ndoc") + 1;
				}
				
				String s = String.format("%06d", elnum) + tmp.substring(6,73) + String.format("%03d", ndoc) + tmp.substring(76);	
				out.write(s);
				out.flush();
			}
			
			db2.close();
			Log.msg("S пакет " + src + " скопирован в " + dest + " с изменением номеров документа и электронных.");
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);				
		}			
	}

	static class SPack
	{
		String fl;
		String head;
		List<String> lines;

		static String mskhd, mskln;

		SPack(String fl)
		{
			this.fl = fl;
		}

		int countLine()
		{
			try {
				
				FileInputStream s = new FileInputStream(new File(fl));
				byte[] b = new byte[732];	

				s.read(b);	
				byte[] arr = new byte[9];
				for(int i = 0; i < 9; i++)
				{
					arr[i] = b[i + 57];
				}
			
				String scn = new String(arr);
				int cn = Integer.parseInt(scn);
				return cn;
			} catch(Exception e) {
				e.printStackTrace();
				Log.msg(e);	
				return -1;
			}
		}

		static void loadMask()
		{
			try {
				BufferedReader br;

				br = new BufferedReader(new FileReader(Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\spack.msk"));

				mskhd = br.readLine();
				mskln = br.readLine();

				br.close();				
			} catch(Exception e) {
				e.printStackTrace();
				Log.msg(e);				
			}
		}

		static void loadMaskR()
		{
			try {
				BufferedReader br;

				br = new BufferedReader(new FileReader(Settings.fullfolder + "settings\\" + Settings.obrfolder + "\\rpack.msk"));

				mskhd = br.readLine();
				mskln = br.readLine();

				br.close();				
			} catch(Exception e) {
				e.printStackTrace();
				Log.msg(e);				
			}
		}
		
		


		void load()
		{
			try {

				int cn = countLine();
				BufferedReader br;

				br = new BufferedReader(new FileReader(fl));
				String tmp;
				tmp = br.readLine();
				head = "";
				for(int i = 0; i < mskhd.length(); i++)
				{
					if(mskhd.substring(i,i + 1).equals("1"))
						head = head + tmp.substring(i, i + 1);
				}

				lines = new ArrayList<String>();

				for(int i = 0; i < cn; i++)
				{	
					tmp = br.readLine();
					String s = "";

					for(int j = 0; j < mskln.length(); j++)
					{
						if(mskln.substring(j,j + 1).equals("1"))
							s = s + tmp.substring(j, j + 1);
					}
					lines.add(s);
				}

				br.close();

			} catch(Exception e) {
				e.printStackTrace();
				Log.msg(e);				
			}
		}

		boolean equals(SPack sp)
		{
			if(!this.head.equals(sp.head))			
				return false;

			if(this.lines.size() != sp.lines.size())
				return false;

			ListIterator <String> iter = this.lines.listIterator();
			while(iter.hasNext())
			{
				String s = iter.next();

				ListIterator <String> itersp = sp.lines.listIterator();
				boolean lneq = false;
				while(itersp.hasNext() && !lneq)
				{
					if(itersp.next().equals(s))
						lneq = true;
				}

				if(!lneq)
					return false;
			}

			return true;
		}

	}
}
