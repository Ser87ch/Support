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
			ResultSet rs = db.st.executeQuery("select top 1 isnull(PackFile,'') packfile from dbo.document_bon_pack where substring(subtype,12,1) = 'o' and substring(subtype,4,1) = 's' order by ID_PACK desc");
			rs.next();
			String spack = rs.getString("packfile");

			File sfile = new File(spack);
			File rfile = new File(Settings.testProj + "tests\\" + Settings.folder + "\\input\\001\\rpack.txt");

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

			Log.msg("Пакет ЭСИС-потверждений " + Settings.testProj + "tests\\" + Settings.folder + "\\001\\input\\rpack.txt" + " создан.");
			s.close();
			rd.close();
			db.close();
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}

	public static void createRpackError49()
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);

			db.connect();
			ResultSet rs = db.st.executeQuery("select top 1 isnull(PackFile,'') packfile from dbo.document_bon_pack where substring(subtype,12,1) = 'o' and substring(subtype,4,1) = 's' order by ID_PACK desc");
			rs.next();
			String spack = rs.getString("packfile");

			File sfile = new File(spack);
			File rfile = new File(Settings.testProj + "tests\\" + Settings.folder + "\\input\\002\\rpack.txt");

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
				char d = "4".toCharArray()[0];
				c[153] = (byte) d;
				d = "9".toCharArray()[0];
				c[154] = (byte) d;


				f = new byte[53];
				f = "Несуществующий лицевой счет получателя в ГРКЦ или РКЦ".getBytes("cp866"); //ЭЛЕКТРОННОЕ ПОДТВЕРЖДЕНИЕ
				for(int j = 0; j < 53; j++)
				{
					c[j + 155] = f[j];
				}
				d = " ".toCharArray()[0];
				for(int j = 208; j < 215; j++)
				{
					c[j] = (byte) d;
				}
				rd.write(c);
				rd.writeBytes("\r\n");
			}

			Log.msg("Пакет ЭСИС-потверждений " + Settings.testProj + "tests\\" + Settings.folder + "\\input\\002\\rpack.txt" + " создан.");
			s.close();
			rd.close();
			db.close();
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}


	public static void createBpackError49()
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);

			db.connect();
			ResultSet rs = db.st.executeQuery("select top 1 isnull(PackFile,'') packfile from dbo.document_bon_pack where substring(subtype,12,1) = 'o' and substring(subtype,4,1) = 's' order by ID_PACK desc");
			rs.next();
			String spack = rs.getString("packfile");

			File sfile = new File(spack);
			File bfile = new File(Settings.testProj + "tests\\" + Settings.folder + "\\input\\002\\bpack.txt");

			FileInputStream s = new FileInputStream(sfile);
			FileOutputStream r = new FileOutputStream(bfile);
			DataOutputStream rd = new DataOutputStream(r);


			byte[] b = new byte[732];
			byte[] c = new byte[730];

			s.read(b);	
			for(int i = 0; i < 9; i++)
			{
				c[i] = b[i + 38];
			}

			//УИС
			byte[] uic = new byte[10];
			for(int i = 0; i < 7; i++)
			{
				uic[i] = b[i + 40];
			}
			char g = "0".toCharArray()[0];                      
			for(int i = 0; i < 3; i++)
			{
				uic[i + 7] = (byte) g;
			}
			String uicstr = Settings.bik.substring(2) + "000";

			//БИК РКЦ			
			byte[] bik = new byte[9];
			for(int i = 0; i < 9; i++)
			{
				bik[i] = b[i + 38];
			}



			String bikrkc = new String(bik);
			//счет 30811
			PayDoc.Client vozvr = new PayDoc.Client(bikrkc,"30811810500000000010");
			vozvr.contrrazr();
			byte[] schvozvr = new byte[20];
			schvozvr = vozvr.ls.getBytes("cp866");


			for(int i = 0; i < 29; i++)
			{
				c[i + 9] = b[i + 9];
			}

			for(int i = 0; i < 9; i++)
			{
				c[i + 38] = b[i];
			}


			for(int i = 0; i < 54; i++)
			{
				c[i + 47] = b[i + 47];
			}




			byte[] f = new byte[16];
			f = "ВОЗВРАТ ПЛАТЕЖЕЙ".getBytes("cp866"); //ЭЛЕКТРОННОЕ ПОДТВЕРЖДЕНИЕ

			for(int j = 0; j < 16; j++)
			{
				c[j + 101] = f[j];
			}


			char e = " ".toCharArray()[0];
			for(int j = 0; j < 196; j++)
			{
				c[j + 117] = (byte) e;
			}

			for(int i = 0; i < 80; i++)
			{
				c[i + 311] = b[i + 391];
			}

			//наименование РКЦ
			byte[] rkcname = new byte[160];
			for(int i = 0; i < 80; i++)
			{
				rkcname[i] = b[i + 391];
			}
			for(int j = 0; j < 80; j++)
			{
				rkcname[j + 80] = (byte) e;
			}


			for(int i = 0; i < 80; i++)
			{
				c[i + 391] = b[i + 311];
			}

			for(int i = 0; i < 259; i++)
			{
				c[i + 471] = b[i + 471];
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


			for(int i = 0; i < cn; i++)
			{
				b = new byte[882];
				c = new byte[880];
				s.read(b);

				for(int j = 0; j < 14; j++)
				{
					c[j] = b[j];
				}

				for(int j = 0; j < 10; j++)
				{
					c[j + 14] = uic[j];
				}

				for(int j = 0; j < 9; j++)
				{
					c[j + 24] = bik[j];
				}
				for(int j = 0; j < 20; j++)
				{
					c[j + 33] = schvozvr[j];
				}
				char d = " ".toCharArray()[0];
				for(int j = 0; j < 20; j++)
				{
					c[j + 53] = (byte) d;
				}

				for(int j = 0; j < 13; j++)
				{
					c[j + 73] = b[j + 73];
				}

				for(int j = 0; j < 49; j++)
				{
					c[j + 86] = b[j + 24];
				}

				for(int j = 0; j < 19; j++)
				{
					c[j + 135] = b[j + 135];
				}

				for(int j = 0; j < 21; j++)
				{
					c[j + 154] = (byte) d;
				}

				for(int j = 0; j < 160; j++)
				{
					c[j + 175] = rkcname[j];
				}

				for(int j = 0; j < 181; j++)
				{
					c[j + 335] = b[j + 154];
				}					

				for(int j = 0; j < 181; j++)
				{
					c[j + 335] = b[j + 154];
				}


				byte[] str = new byte[6];
				for(int j = 0; j < 6; j++)
					str[j] = b[j];

				String num = new String(str);

				str = new byte[16];
				for(int j = 0; j < 16; j++)
					str[j] = b[j + 135];
				String sumi = new String(str);

				str = new byte[2];
				for(int j = 0; j < 2; j++)
					str[j] = b[j + 151];
				String sumf = new String(str);

				byte[] naznb = new byte[151];
				naznb = ("Возврат ошибочного электронного платежного документа " + num + " с датой составления "
						+ new SimpleDateFormat("dd/MM/yyyy").format(Settings.operDate) +", УИС " 
						+ uicstr + " на сумму " + sumi + "." + sumf + " код возврата 49").getBytes("cp866");
				for(int j = 0; j < 151; j++)
				{
					c[j + 516] = naznb[j];
				}

				for(int j = 0; j < 59; j++)
				{
					c[j + 667] = (byte) d;
				}

				for(int j = 0; j < 154; j++)
				{
					c[j + 726] = b[j + 726];
				}

				rd.write(c);
				rd.writeBytes("\r\n");
				rd.write(b);
			}

			Log.msg("Пакет ЭПС по возврату платежей " + Settings.testProj + "tests\\" + Settings.folder + "\\input\\002\\bpack.txt" + " создан.");
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
			Log.msg("Файл " + sourcestr + " скопирован в " + deststr + " .");
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
			ResultSet rs = db.st.executeQuery("select top 1 isnull(SUBTYPE,'') packfile from dbo.document_bon_pack where substring(SUBTYPE, 4,1) = 'r' and substring(SUBTYPE, 12,1) = 'i' and status = 0 and substring(subtype, 5, 4) = '" + new SimpleDateFormat("ddMM").format(Settings.operDate) + "' order by ID_PACK desc");

			String spack;
			if(rs.next())
				spack = rs.getString("packfile"); //p$9s0302.82o			
			else
				spack = "";

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

	public static String getBPackName()
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();			
			ResultSet rs = db.st.executeQuery("select top 1 isnull(SUBTYPE,'') packfile from dbo.document_bon_pack where substring(SUBTYPE, 4,1) = 'b' and substring(SUBTYPE, 12,1) = 'i' and status = 0 and substring(subtype, 5, 4) = '" + new SimpleDateFormat("ddMM").format(Settings.operDate) + "' order by ID_PACK desc");
			String spack;
			if(rs.next())
				spack = rs.getString("packfile"); //p$9s0302.82o			
			else
				spack = "";
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

			Log.msg("Имя файла S пакета " + "p$" + s + "b" + new SimpleDateFormat("ddMM").format(Settings.operDate) + "." + Settings.bik.substring(4,6) + "i .");
			return "p$" + s + "b" + new SimpleDateFormat("ddMM").format(Settings.operDate) + "." + Settings.bik.substring(4,6) + "i";
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
			return "";
		}
	}

	public static String getSPackName()
	{
		try {
			if(Settings.GenSpack.error.equals("00") || Settings.GenSpack.error.equals("23") || Settings.GenSpack.error.equals("24") 
					|| Settings.GenSpack.error.equals("25") || Settings.GenSpack.error.equals("26") || Settings.GenSpack.error.equals("27"))
			{
				DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
				db.connect();			
				ResultSet rs = db.st.executeQuery("select top 1 isnull(SUBTYPE,'') packfile from dbo.document_bon_pack where substring(SUBTYPE, 4,1) = 's' and substring(SUBTYPE, 10,3) = '" + Settings.bik.substring(4, 6) + "i' and status = 0 and substring(subtype, 5, 4) = '" + new SimpleDateFormat("ddMM").format(Settings.operDate) + "' order by ID_PACK desc");

				String spack;
				if(rs.next())
					spack = rs.getString("packfile"); //p$9s0302.82o			
				else
					spack = "";
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
			}
			else if(Settings.GenSpack.error.equals("11"))
			{
				Log.msg("Имя файла S пакета P$2s" + new SimpleDateFormat("ddMM").format(Settings.operDate) + ".a2i с кодом ошибки 11.");
				return "P$2s0302.a2i";
			}
			else if(Settings.GenSpack.error.equals("12"))
			{
				Log.msg("Имя файла S пакета P$2saa02.82i с кодом ошибки 12.");
				return "P$2saa02.82i";
			}
			else if(Settings.GenSpack.error.equals("13"))
			{
				Log.msg("Имя файла S пакета P$2s" + new SimpleDateFormat("ddMM").format(Settings.operDate) + ".99i с кодом ошибки 13.");
				return "P$2s0302.99i";
			}
			else if(Settings.GenSpack.error.equals("14"))
			{
				Log.msg("Имя файла S пакета P$*s"  + new SimpleDateFormat("ddMM").format(Settings.operDate) + ".82i с кодом ошибки 14.");
				return "P$*s0302.82i";
			}
			else if(Settings.GenSpack.error.equals("15"))
			{
				DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
				db.connect();			
				ResultSet rs = db.st.executeQuery("select top 1 isnull(SUBTYPE,'') packfile from dbo.document_bon_pack where substring(SUBTYPE, 4,1) = 's' and substring(SUBTYPE, 12,1) = 'i' and status = 0 and substring(subtype, 5, 4) = '" + new SimpleDateFormat("ddMM").format(Settings.operDate) + "' order by ID_PACK desc");

				String spack;
				if(rs.next())
					spack = rs.getString("packfile"); //p$9s0302.82o			
				else
					spack = "";
				db.close();
				Log.msg("Имя файла S пакета " + spack + " с кодом ошибки 14.");
				return spack;
			}
			
			
			return "";
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
			return "";
		}
	}

	public static String getPackNameFolder(String folder, String type)
	{
		String pack = "";
		File[] files = new File(folder).listFiles();
	    if(files!=null) 
	    { 
	        for(File f: files) 
	        {
	        	if(f.getName().startsWith("p$") && f.getName().substring(3, 4).toLowerCase().equals(type.toLowerCase()))
	        	{
	        		pack = f.getName();
	        		break;
	        	}
	        }
	    }
		return pack;
	}
	
	public static String copySPack(String testnum)
	{
		try{
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);

			db.connect();
			ResultSet rs = db.st.executeQuery("select top 1 isnull(PackFile,'') packfile, isnull(subtype,'') subtype from dbo.document_bon_pack where substring(SUBTYPE, 4,1) = 's' and substring(subtype,12,1) = 'o' order by ID_PACK desc");
			rs.next();
			String spack = rs.getString("packfile");
			String fl = rs.getString("subtype");
			copyFile(spack,Settings.testProj + "\\tests\\" + Settings.folder + "\\output\\" + testnum + "\\" + fl);
			return fl;
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
			return "";
		}
	}

	
	
	public static String copyRPack(String testnum)
	{
		try{
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);

			db.connect();
			ResultSet rs = db.st.executeQuery("select top 1 isnull(PackFile,'') packfile, isnull(subtype,'') subtype from dbo.document_bon_pack where substring(SUBTYPE, 4,1) = 'r' and substring(subtype,12,1) = 'o' order by ID_PACK desc");
			rs.next();
			String spack = rs.getString("packfile");
			String fl = rs.getString("subtype");
			copyFile(spack,Settings.testProj + "\\tests\\" + Settings.folder + "\\output\\" + testnum + "\\" + fl);
			return fl;
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
			return "";
		}
	}

	
	public static String copyBPack(String testnum)
	{
		try{
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);

			db.connect();
			ResultSet rs = db.st.executeQuery("select top 1 isnull(PackFile,'') packfile, isnull(subtype,'') subtype from dbo.document_bon_pack where substring(SUBTYPE, 4,1) = 'b' and substring(subtype,12,1) = 'o' order by ID_PACK desc");
			rs.next();
			String spack = rs.getString("packfile");
			String fl = rs.getString("subtype");
			copyFile(spack,Settings.testProj + "\\tests\\" + Settings.folder + "\\output\\" + testnum + "\\" + fl);
			return fl;
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
			return "";
		}
	}

	public static boolean compareSPack(String etal, String fl)
	{
		if(!new File(etal).getName().equals(new File(fl).getName()))
		{
			Log.msgCMP("Имена S пакетов не совпадают.");
			return false;
		}
		
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
		
		if(!new File(etal).getName().equals(new File(fl).getName()))
		{
			Log.msgCMP("Имена R пакетов не совпадают.");
			return false;
		}
		SPack.loadMaskR();

		SPack et = new SPack(etal);
		et.load();

		SPack sp = new SPack(fl);
		sp.load();

		if(et.equals(sp))
			Log.msgCMP("R пакет " + fl + " совпадает с эталонным R пакетом " + fl + " по маске rpack.msk .");
		else
			Log.msgCMP("R пакет " + fl + " не совпадает с эталонным R пакетом " + fl + " по маске rpack.msk .");
		return et.equals(sp);
	}
	
	public static boolean compareBPack(String etal, String fl)
	{
		
		if(!new File(etal).getName().equals(new File(fl).getName()))
		{
			Log.msgCMP("Имена B пакетов не совпадают.");
			return false;
		}
		SPack.loadMaskB();

		SPack et = new SPack(etal);
		et.loadB();

		SPack sp = new SPack(fl);
		sp.loadB();

		if(et.equals(sp))
			Log.msgCMP("B пакет " + fl + " совпадает с эталонным B пакетом " + fl + " по маске bpack.msk .");
		else
			Log.msgCMP("B пакет " + fl + " не совпадает с эталонным B пакетом " + fl + " по маске bpack.msk .");
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

	
	public static void clearFolder(File fld)
	{
		File[] files = fld.listFiles();
	    if(files!=null) 
	    { 
	        for(File f: files) 
	        {
	            if(f.isDirectory())
	            {
	                clearFolder(f);
	            } else {
	                f.delete();
	            }
	        }
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

		static void loadMaskB()
		{
			try {
				BufferedReader br;

				br = new BufferedReader(new FileReader(Settings.fullfolder + "settings\\" + Settings.obrfolder + "\\bpack.msk"));

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
		
		void loadB()
		{
			try {

				int cn = 2 * countLine();
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
