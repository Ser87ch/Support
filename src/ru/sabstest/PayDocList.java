package ru.sabstest;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.Math;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class PayDocList {
	private List<PayDoc> pdl;

	public PayDocList() 
	{

	}

	public void generate()
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();

			ResultSet rs = db.st.executeQuery("select top 1 NUM_ACC from dbo.Account where rest = (select min(rest) from dbo.Account where substring(NUM_ACC,1,1) = '4' and substring(NUM_ACC,1,5) <> '40101')");
			rs.next();
			String ls = rs.getString("NUM_ACC");

			PayDoc.Client plat = new PayDoc.Client(Settings.bik, ls);

			pdl = new ArrayList<PayDoc>();

			ResultSet rsbik = db.st.executeQuery("select top " + Settings.GenDoc.numBIK + " NEWNUM, isnull(KSNP,'') ksnp from dbo.BNKSEEK where substring(NEWNUM,1,4) = '" + Settings.bik.substring(0, 4) + "' and UER in ('2','3','4','5') and NEWNUM <> '" + Settings.bik + "'");
			int i = 1;
			while(rsbik.next()) {
				String bikpol = rsbik.getString("NEWNUM");
				String kspol = rsbik.getString("ksnp");
				String lspol = "40702810000000000005";

				PayDoc.Client pol = new PayDoc.Client(bikpol, kspol, lspol, "111111111111", "222222222", "ЗАО Тест");
				pol.contrrazr();

				for(int j = 0; j < Settings.GenDoc.numDoc; j++)
				{
					PayDoc pd = new PayDoc();
					pd.num = Settings.GenDoc.firstDoc + j;
					pd.date = Settings.operDate;
					pd.vidop = "01";
					pd.sum =  ((float) Math.round(new Random().nextFloat() * 10000))/ 100;				
					pd.vidpl = VidPlat.EL;
					pd.plat = plat;
					pd.pol = pol;
					pd.ocher = 6;
					pd.status = "";
					pd.naznach = "Оплата теста";
					pd.datesp = Settings.operDate;
					pd.datepost = Settings.operDate;

					pdl.add(pd);
					Log.msg("Документ №" + Integer.toString(i) + " сгенерирован.");
					i++;
				}

			}
			Log.msg("Генерация документов завершена.");
			db.close();
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}
	
	public void generateS()
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();

			
			DB db2 = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db2.connect();
			
			ResultSet rs = db.st.executeQuery("select top 1 NUM_ACC from dbo.Account where rest = (select min(rest) from dbo.Account where substring(NUM_ACC,1,1) = '4' and substring(NUM_ACC,1,5) <> '40101')");
			rs.next();
			String ls = rs.getString("NUM_ACC");

			PayDoc.Client plat = new PayDoc.Client(Settings.bik, "", ls, "111111111111", "222222222", "ЗАО Получатель");

			pdl = new ArrayList<PayDoc>();

			ResultSet rsbik = db.st.executeQuery("select top " + Settings.GenSpack.numBIK + " NEWNUM, isnull(KSNP,'') ksnp from dbo.BNKSEEK where substring(NEWNUM,1,4) = '" + Settings.bik.substring(0, 4) + "' and UER in ('2','3','4','5') and NEWNUM <> '" + Settings.bik + "'");
			int i = 1;
			while(rsbik.next()) {
				String bikpol = rsbik.getString("NEWNUM");
				String kspol = rsbik.getString("ksnp");
				String lspol = "40702810000000000005";
				
				
				
				String s = "select isnull(max(elnum),0) as el, isnull(max(n_doc),0) as ndoc from dbo.DOCUMENT_BON where date_doc = '" + new SimpleDateFormat("yyyy-MM-dd").format(Settings.operDate) + "' and uic = '" + bikpol.substring(2,9) + "000" + "'";		
				ResultSet rsel = db2.st.executeQuery(s);
				int elnum = 0, ndoc = 0;
				rsel.next();
				elnum = rsel.getInt("el");
				ndoc = rsel.getInt("ndoc");
				
				PayDoc.Client pol = new PayDoc.Client(bikpol, kspol, lspol, "222222222222", "111111111", "ЗАО Плательщик");
				pol.contrrazr();

				for(int j = 0; j < Settings.GenSpack.numDoc; j++)
				{
					PayDoc pd = new PayDoc();
					pd.num = ndoc + 1 + j;
					pd.date = Settings.operDate;
					pd.vidop = "01";
					pd.sum =  ((float) Math.round(new Random().nextFloat() * 10000))/ 100;				
					pd.vidpl = VidPlat.EL;
					pd.pol = plat;
					pd.plat = pol;
					pd.ocher = 6;
					pd.status = "";
					pd.naznach = "Оплата теста";
					pd.datesp = Settings.operDate;
					pd.datepost = Settings.operDate;
					pd.elnum = elnum + 1 + j;
					pdl.add(pd);
					Log.msg("Документ №" + Integer.toString(i) + " для S пакета сгенерирован.");
					i++;
				}

			}
			Log.msg("Генерация документов для S пакета завершена.");
			db.close();
			db2.close();
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}
	
	public int length()
	{
		return pdl.size();
	}
	
	public int sumAll() //сумма умноженная на 100
	{
		int sum = 0;
		ListIterator <PayDoc> iter = pdl.listIterator();
		while(iter.hasNext())
		{
			int i = (int) (iter.next().sum * 100);
			sum = sum + i;
			
		}

		return sum;
	}
	
	public PayDoc get(int i)
	{
		return (PayDoc) pdl.get(i);
	}

	@Override
	public String toString()
	{
		String str = "";
		ListIterator <PayDoc> iter = pdl.listIterator();
		while(iter.hasNext())
		{
			str = str + iter.next().toString() + "\n";
		}

		return str;
	}

	public void createXML()
	{
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("paydocs");
			doc.appendChild(rootElement);

			rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\input\\paydocs.xsd");
			ListIterator <PayDoc> iter = pdl.listIterator();
			int i = 1;
			Log.msg("Начало создания XML с входящими документами.");
			while(iter.hasNext())
			{

				Element paydoc = doc.createElement("doc");
				rootElement.appendChild(paydoc);
				paydoc.setAttribute("id", Integer.toString(i));


				PayDoc pd = iter.next();

				XML.createNode(doc, paydoc, "num", pd.num);
				XML.createNode(doc, paydoc, "date", pd.date);
				XML.createNode(doc, paydoc, "vidop", pd.vidop);
				XML.createNode(doc, paydoc, "sum", pd.sum);
				XML.createNode(doc, paydoc, "vidpl", pd.vidpl);

				//плательщик
				Element plat = doc.createElement("plat");
				paydoc.appendChild(plat);

				XML.createNode(doc, plat, "bik", pd.plat.bik);
				XML.createNode(doc, plat, "ks", pd.plat.ks);
				XML.createNode(doc, plat, "ls", pd.plat.ls);
				XML.createNode(doc, plat, "inn", pd.plat.inn);
				XML.createNode(doc, plat, "kpp", pd.plat.kpp);
				XML.createNode(doc, plat, "name", pd.plat.name);

				//получатель
				Element pol = doc.createElement("pol");
				paydoc.appendChild(pol);				

				XML.createNode(doc, pol, "bik", pd.pol.bik);
				XML.createNode(doc, pol, "ks", pd.pol.ks);
				XML.createNode(doc, pol, "ls", pd.pol.ls);
				XML.createNode(doc, pol, "inn", pd.pol.inn);
				XML.createNode(doc, pol, "kpp", pd.pol.kpp);
				XML.createNode(doc, pol, "name", pd.pol.name);

				XML.createNode(doc, paydoc, "ocher", pd.ocher);
				XML.createNode(doc, paydoc, "status", pd.status);

				if(pd.status != "")
				{
					XML.createNode(doc, paydoc, "kbk", pd.kbk);
					XML.createNode(doc, paydoc, "okato", pd.okato);
					XML.createNode(doc, paydoc, "osn", pd.osn);
					XML.createNode(doc, paydoc, "nalper", pd.nalper);
					XML.createNode(doc, paydoc, "numdoc", pd.numdoc);
					XML.createNode(doc, paydoc, "datedoc", pd.datedoc);
					XML.createNode(doc, paydoc, "typepl", pd.typepl);
				}
				
				XML.createNode(doc, paydoc, "naznach", pd.naznach);
				XML.createNode(doc, paydoc, "datesp", pd.datesp);
				XML.createNode(doc, paydoc, "datepost", pd.datepost);

				Log.msg("Документ №" + i + " записан в XML.");
				i++;
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(Settings.testProj + "\\tests\\" + Settings.folder + "\\input\\paydocs.xml"));

			transformer.transform(source, result);
			Log.msg("XML с входящими документами " + Settings.testProj + "\\tests\\" + Settings.folder + "\\input\\paydocs.xml создан.");			

			XML.validate(Settings.testProj + "XMLSchema\\input\\paydocs.xsd",Settings.testProj + "\\tests\\" +  Settings.folder + "\\input\\paydocs.xml");			

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			Log.msg(pce);
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
			Log.msg(tfe);
		}
	}

	public void readXML(String src)
	{
		try {
			pdl = new ArrayList<PayDoc>();
			
			File fXmlFile = new File(src);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			XML.validate(Settings.testProj + "XMLSchema\\input\\paydocs.xsd",src);

			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("doc");


			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					PayDoc pd = new PayDoc();
					pd.num = XML.getTagInt("num", eElement);	
					pd.date = XML.getTagDate("date", eElement);	
					pd.vidop = XML.getTagString("vidop", eElement);
					pd.sum = XML.getTagFloat("sum", eElement);
					pd.vidpl = VidPlat.valueOf(XML.getTagString("vidpl", eElement));
					
					NodeList nlList = eElement.getElementsByTagName("plat");
					Element clElement = (Element) nlList.item(0);
										
					pd.plat = new PayDoc.Client(XML.getTagString("bik", clElement), XML.getTagString("ks", clElement),
							XML.getTagString("ls", clElement), XML.getTagString("inn", clElement),
							XML.getTagString("kpp", clElement), XML.getTagString("name", clElement));
					
					nlList = eElement.getElementsByTagName("pol");
					clElement = (Element) nlList.item(0);
					pd.pol = new PayDoc.Client(XML.getTagString("bik", clElement), XML.getTagString("ks", clElement),
							XML.getTagString("ls", clElement), XML.getTagString("inn", clElement),
							XML.getTagString("kpp", clElement), XML.getTagString("name", clElement));
					
					pd.ocher = XML.getTagInt("ocher", eElement);
					pd.status = XML.getTagString("status", eElement);
					pd.kbk = XML.getTagString("kbk", eElement);
					pd.okato = XML.getTagString("okato", eElement);
					pd.osn = XML.getTagString("osn", eElement);
					pd.nalper = XML.getTagString("nalper", eElement);
					pd.numdoc = XML.getTagString("numdoc", eElement);
					pd.datedoc = XML.getTagString("datedoc", eElement);
					pd.typepl = XML.getTagString("typepl", eElement);
					pd.naznach = XML.getTagString("naznach", eElement);
					pd.datesp = XML.getTagDate("datesp", eElement);
					pd.datepost = XML.getTagDate("datepost", eElement);
					
					pdl.add(pd);
					Log.msg("Документ №" + Integer.toString(temp + 1) + " загружен в программу.");
				}
			}
			Log.msg("XML с входящими документами " + src + " загружен в программу.");
		} catch (Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}
	
	public void createSpack()
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);

			db.connect();
			ResultSet rs = db.st.executeQuery("select b.RKC rkc, b.NAMEP name, r.namep rname  from bnkseek b inner join bnkseek r on b.rkc = r.newnum where b.newnum = '" + Settings.bik + "'");
			rs.next();
			String rkcbik = rs.getString("RKC");
			String rkcname = rs.getString("rname");
			String bnkname = rs.getString("name");
			
			File sfile = new File(Settings.testProj + "tests\\" + Settings.folder + "\\input\\spack.txt");
			FileOutputStream s = new FileOutputStream(sfile);
			DataOutputStream sd = new DataOutputStream(s);
			
			String sf = rkcbik + String.format("%18s", "") + new SimpleDateFormat("ddMMyyyy").format(Settings.operDate) +
			String.format("%3s", "") + Settings.bik + String.format("%10s", "") + String.format("%09d", this.length()) + 
			String.format("%9s", "") + String.format("%018d", this.sumAll() ) + String.format("%8s", "") + 
			String.format("%-210s", "ЭЛЕКТРОННЫЕ ПЛАТЕЖИ") + String.format("%-80s", rkcname) +
			String.format("%-80s", bnkname) + String.format("%259s", "");			
			
			byte[] b = new byte[730];
			b = sf.getBytes("cp866"); 
			sd.write(b);			
			
			Log.msg("Служебная запись записана в S пакет.");
			
			int i = 1;
			ListIterator <PayDoc> iter = pdl.listIterator();
			while(iter.hasNext())
			{
				sd.writeBytes("\r\n");
				PayDoc pd = iter.next();
				
				sf = String.format("%06d", pd.elnum) + new SimpleDateFormat("ddMMyyyy").format(pd.date) +
				pd.plat.bik.substring(2,9) + "000" + pd.plat.bik + pd.plat.ls + String.format("%20s", pd.plat.ks) +
				String.format("%03d", pd.num) + new SimpleDateFormat("ddMMyyyy").format(pd.date) + pd.vidop +
				pd.pol.bik + pd.pol.ls + String.format("%20s", pd.pol.ks) +
				String.format("%018d", (int)(pd.sum * 100)) + String.format("%01d", pd.ocher) + 
				String.format("%12s", pd.plat.inn) + String.format("%9s", pd.plat.kpp) +
				String.format("%-160s", pd.plat.name) + 	String.format("%12s", pd.pol.inn) + 
				String.format("%9s", pd.pol.kpp) + String.format("%-160s", pd.pol.name) + 
				String.format("%-210s", pd.naznach) + new SimpleDateFormat("ddMMyyyy").format(pd.datepost) + 
				String.format("%8s", "") + new SimpleDateFormat("ddMMyyyy").format(pd.datesp) +
				new SimpleDateFormat("ddMMyyyy").format(pd.date) + String.format("%2s", pd.status) + 
				String.format("%20s", pd.kbk) + String.format("%11s", pd.okato) + String.format("%2s", pd.osn) +
				String.format("%10s", pd.nalper) + String.format("%15s", pd.numdoc) + String.format("%10s", pd.datedoc) +
				String.format("%2s", pd.typepl) + String.format("%50s", "");
				
				b = new byte[880];
				b = sf.getBytes("cp866"); 
				sd.write(b);
				
				Log.msg("Документ №" + i + " записан в S пакет.");
				i++;
			}
			
			Log.msg("S пакет создан.");
			s.close();
			sd.close();
			db.close();
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}		
	}
}


