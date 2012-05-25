package ru.sabstest;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.io.File;
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

public class Settings{

	public static String server = "";
	public static String db = "";
	public static String user = "";
	public static String pwd = "";
	public static String path = "";
	public static String bik = "";
	//	public static String ks = "";
	public static Date operDate = new Date(0);
	public static String testProj= "";
	public static String folder = "";
	public static String fullfolder = "";

	public static void loadFromDB()
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();

			//получение папки сабс
			ResultSet rs = db.st.executeQuery("select VALUE from dbo.XDM_CONF_SETTINGS where ID_PARAM = 1004");
			rs.next();
			Settings.path = rs.getString("VALUE");
			Log.msg("Папка САБС " + Settings.path);

			rs = db.st.executeQuery("select X.BIK as BIK , isnull(B.KSNP,'') as KSNP from dbo.XDM_DEPARTMENT X inner join dbo.BNKSEEK B on B.NEWNUM = X.BIK");
			rs.next();
			Settings.bik = rs.getString("BIK");
			//	Settings.ks = rs.getString("KSNP");
			Log.msg("БИК ПУ " + Settings.bik);

			rs = db.st.executeQuery("SELECT top 1 OPER_DATE as dt FROM XDM_OPERDAY_PROP WHERE VALUE = 0");
			rs.next();
			Settings.operDate = rs.getDate("dt");
			Log.msg("Опер. день " + new SimpleDateFormat("dd.MM.yyyy").format(Settings.operDate));		

			db.close();
		} catch(Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}

	public static void createXML()
	{
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("general");
			doc.appendChild(rootElement);

			rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\settings\\general.xsd");

			XML.createNode(doc, rootElement, "server", Settings.server);			
			XML.createNode(doc, rootElement, "db", Settings.db);
			XML.createNode(doc, rootElement, "user", Settings.user);
			XML.createNode(doc, rootElement, "pwd", Settings.pwd);
			XML.createNode(doc, rootElement, "path", Settings.path);
			XML.createNode(doc, rootElement, "bik", Settings.bik);			
			//	XML.createNode(doc, rootElement, "ks", Settings.ks);
			XML.createNode(doc, rootElement, "operdate", Settings.operDate);
			XML.createNode(doc, rootElement, "testproj", Settings.testProj);
			XML.createNode(doc, rootElement, "folder", Settings.folder);
			XML.createNode(doc, rootElement, "fullfolder", Settings.fullfolder);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			File xml = new File(Settings.fullfolder + "settings\\general.xml");
			StreamResult result = new StreamResult(xml);
			Log.msg("XML c общими настройками " + Settings.fullfolder + "settings\\general.xml создан.");

			//StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			XML.validate(Settings.testProj + "XMLSchema\\settings\\general.xsd", Settings.fullfolder + "settings\\general.xml");


		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			Log.msg(pce);
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
			Log.msg(tfe);
		} catch (Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}

	}
	public static void readXML(String src)
	{
		readXML(src, false);
	}
	public static void readXML(String src, boolean init)
	{
		try {

			File fXmlFile = new File(src);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			XML.validate(Settings.testProj + "XMLSchema\\settings\\general.xsd",src);

			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("general");


			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					server = XML.getTagString("server", eElement);	
					db = XML.getTagString("db", eElement);	
					user = XML.getTagString("user", eElement);	
					pwd = XML.getTagString("pwd", eElement);
					testProj = XML.getTagString("testproj", eElement);	
					if(!init)
					{						
						path = XML.getTagString("path", eElement);	
						bik = XML.getTagString("bik", eElement);	
						operDate = XML.getTagDate("operdate", eElement);	

						folder = XML.getTagString("folder", eElement);	
						fullfolder = XML.getTagString("fullfolder", eElement);

					}
				}
			}
			Log.msg("XML c общими настройками " + src + " загружен в программу.");
		} catch (Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}



	public static class GenDoc{
		public static int numBIK = 0;
		public static int numDoc = 0;
		public static int firstDoc = 0;

		public static void createXML()
		{
			try {

				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("gendoc");
				doc.appendChild(rootElement);

				rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\settings\\gendoc.xsd");

				XML.createNode(doc, rootElement, "numbik", GenDoc.numBIK);	
				XML.createNode(doc, rootElement, "numdoc", GenDoc.numDoc);	
				XML.createNode(doc, rootElement, "firstdoc", GenDoc.firstDoc);	

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				File xml = new File(Settings.fullfolder + "settings\\gendoc.xml");
				StreamResult result = new StreamResult(xml);
				Log.msg("XML с настройками для генерации входящих документов " + Settings.fullfolder + "settings\\gendoc.xml создан.");
				//StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				XML.validate(Settings.testProj + "XMLSchema\\settings\\gendoc.xsd",Settings.fullfolder + "settings\\gendoc.xml");

			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
				Log.msg(pce);
			} catch (TransformerException tfe) {
				tfe.printStackTrace();
				Log.msg(tfe);
			} catch (Exception e) {
				e.printStackTrace();
				Log.msg(e);
			}

		}

		public static void readXML(String src)
		{
			try {

				File fXmlFile = new File(src);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				XML.validate(Settings.testProj + "XMLSchema\\settings\\gendoc.xsd",src);

				//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				NodeList nList = doc.getElementsByTagName("gendoc");


				for (int temp = 0; temp < nList.getLength(); temp++) {

					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;

						numBIK = XML.getTagInt("numbik", eElement);	
						numDoc = XML.getTagInt("numdoc", eElement);	
						firstDoc = XML.getTagInt("firstdoc", eElement);		

					}
				}
				Log.msg("XML с настройками для генерации входящих документов " + src + " загружен в программу.");
			} catch (Exception e) {
				e.printStackTrace();
				Log.msg(e);
			}
		}
	}
	
	public static class PerVvod{
		public static String user = "";
		public static String pwd = "";
		public static String sign = "";
		public static String key = "";
		
		public static void createXML()
		{
			try {

				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("pervvod");
				doc.appendChild(rootElement);

				rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\settings\\pervvod.xsd");

				XML.createNode(doc, rootElement, "user", user);	
				XML.createNode(doc, rootElement, "pwd", pwd);	
				XML.createNode(doc, rootElement, "sign", sign);	
				XML.createNode(doc, rootElement, "key", key);	

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				File xml = new File(Settings.fullfolder + "settings\\pervvod.xml");
				StreamResult result = new StreamResult(xml);
				Log.msg("XML с настройками для первичного ввода докуметов " + Settings.fullfolder + "settings\\pervvod.xml создан.");
				//StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				XML.validate(Settings.testProj + "XMLSchema\\settings\\pervvod.xsd",Settings.fullfolder + "settings\\pervvod.xml");

			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
				Log.msg(pce);
			} catch (TransformerException tfe) {
				tfe.printStackTrace();
				Log.msg(tfe);
			} catch (Exception e) {
				e.printStackTrace();
				Log.msg(e);
			}

		}

		public static void readXML(String src)
		{
			try {

				File fXmlFile = new File(src);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				XML.validate(Settings.testProj + "XMLSchema\\settings\\pervvod.xsd",src);

				//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				NodeList nList = doc.getElementsByTagName("pervvod");


				for (int temp = 0; temp < nList.getLength(); temp++) {

					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;

						user = XML.getTagString("user", eElement);	
						pwd = XML.getTagString("pwd", eElement);	
						sign = XML.getTagString("sign", eElement);		
						key = XML.getTagString("key", eElement);		

					}
				}
				Log.msg("XML с настройками для первичного ввода документов " + src + " загружен в программу.");
			} catch (Exception e) {
				e.printStackTrace();
				Log.msg(e);
			}
		}
	}

	
	public static class ContrVvod{
		public static String user = "";
		public static String pwd = "";
		public static String sign = "";
		public static String key = "";

		public static void createXML()
		{
			try {

				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("contrvvod");
				doc.appendChild(rootElement);

				rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\settings\\pervvod.xsd");

				XML.createNode(doc, rootElement, "user", user);	
				XML.createNode(doc, rootElement, "pwd", pwd);	
				XML.createNode(doc, rootElement, "sign", sign);	
				XML.createNode(doc, rootElement, "key", key);	

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				File xml = new File(Settings.fullfolder + "settings\\contrvvod.xml");
				StreamResult result = new StreamResult(xml);
				Log.msg("XML с настройками для контрольго ввода докуметов " + Settings.fullfolder + "settings\\pervvod.xml создан.");
				//StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				XML.validate(Settings.testProj + "XMLSchema\\settings\\contrvvod.xsd",Settings.fullfolder + "settings\\contrvvod.xml");

			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
				Log.msg(pce);
			} catch (TransformerException tfe) {
				tfe.printStackTrace();
				Log.msg(tfe);
			} catch (Exception e) {
				e.printStackTrace();
				Log.msg(e);
			}

		}

		public static void readXML(String src)
		{
			try {

				File fXmlFile = new File(src);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				XML.validate(Settings.testProj + "XMLSchema\\settings\\contrvvod.xsd",src);

				//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				NodeList nList = doc.getElementsByTagName("contrvvod");


				for (int temp = 0; temp < nList.getLength(); temp++) {

					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;

						user = XML.getTagString("user", eElement);	
						pwd = XML.getTagString("pwd", eElement);	
						sign = XML.getTagString("sign", eElement);	
						key = XML.getTagString("key", eElement);	

					}
				}
				Log.msg("XML с настройками для контрольного ввода документов " + src + " загружен в программу.");
			} catch (Exception e) {
				e.printStackTrace();
				Log.msg(e);
			}
		}
	}

}


