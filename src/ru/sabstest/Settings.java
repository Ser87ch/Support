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

	public static final String pervfolder = "perv"; 
	public static final String obrfolder = "obr"; 
	
	
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
				rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\gendoc.xsd");

				XML.createNode(doc, rootElement, "numbik", numBIK);	
				XML.createNode(doc, rootElement, "numdoc", numDoc);	
				XML.createNode(doc, rootElement, "firstdoc", firstDoc);	

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				File xml = new File(Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\gendoc.xml");
				StreamResult result = new StreamResult(xml);
				Log.msg("XML с настройками для генерации входящих документов " + Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\gendoc.xml создан.");
				//StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				XML.validate(Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\gendoc.xsd",Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\gendoc.xml");

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
				XML.validate(Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\gendoc.xsd",src);

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
				rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\pervvod.xsd");

				XML.createNode(doc, rootElement, "user", user);	
				XML.createNode(doc, rootElement, "pwd", pwd);	
				XML.createNode(doc, rootElement, "sign", sign);	
				XML.createNode(doc, rootElement, "key", key);	

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				File xml = new File(Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\pervvod.xml");
				StreamResult result = new StreamResult(xml);
				Log.msg("XML с настройками для первичного ввода докуметов " + Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\pervvod.xml создан.");
				//StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				XML.validate(Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\pervvod.xsd",Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\pervvod.xml");

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
				XML.validate(Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\pervvod.xsd",src);

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
				rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\pervvod.xsd");

				XML.createNode(doc, rootElement, "user", user);	
				XML.createNode(doc, rootElement, "pwd", pwd);	
				XML.createNode(doc, rootElement, "sign", sign);	
				XML.createNode(doc, rootElement, "key", key);	

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				File xml = new File(Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\contrvvod.xml");
				StreamResult result = new StreamResult(xml);
				Log.msg("XML с настройками для контрольго ввода докуметов " + Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\pervvod.xml создан.");
				//StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				XML.validate(Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\contrvvod.xsd",Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\contrvvod.xml");

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
				XML.validate(Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\contrvvod.xsd",src);

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

	
	public static class FormES{
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
				Element rootElement = doc.createElement("formes");
				doc.appendChild(rootElement);

				rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\formes.xsd");

				XML.createNode(doc, rootElement, "user", user);	
				XML.createNode(doc, rootElement, "pwd", pwd);	
				XML.createNode(doc, rootElement, "sign", sign);	
				XML.createNode(doc, rootElement, "key", key);	

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				File xml = new File(Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\formes.xml");
				StreamResult result = new StreamResult(xml);
				Log.msg("XML с настройками для формирования ЭС " + Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\formes.xml создан.");
				//StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				XML.validate(Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\formes.xsd",Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\formes.xml");

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
				XML.validate(Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\formes.xsd",src);

				//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				NodeList nList = doc.getElementsByTagName("formes");


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
				Log.msg("XML с настройками для формирования ЭС " + src + " загружен в программу.");
			} catch (Exception e) {
				e.printStackTrace();
				Log.msg(e);
			}
		}
	}
	
	public static class ContrES{
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
				Element rootElement = doc.createElement("contres");
				doc.appendChild(rootElement);

				rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\contres.xsd");

				XML.createNode(doc, rootElement, "user", user);	
				XML.createNode(doc, rootElement, "pwd", pwd);	
				XML.createNode(doc, rootElement, "sign", sign);	
				XML.createNode(doc, rootElement, "key", key);	

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				File xml = new File(Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\contres.xml");
				StreamResult result = new StreamResult(xml);
				Log.msg("XML с настройками для контроля ЭС " + Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\contres.xml создан.");
				//StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				XML.validate(Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\contres.xsd",Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\contres.xml");

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
				XML.validate(Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\contres.xsd",src);

				//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				NodeList nList = doc.getElementsByTagName("contres");


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
				Log.msg("XML с настройками для контроля ЭС " + src + " загружен в программу.");
			} catch (Exception e) {
				e.printStackTrace();
				Log.msg(e);
			}
		}
	}
	
	public static class GenRpack{
		public static String signobr = "";
		public static String keyobr = "";
		public static String signcontr = "";
		public static String keycontr = "";
		public static boolean isGenBpack = false;
		
		public static void createXML()
		{
			try {

				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("genrpack");
				doc.appendChild(rootElement);

				rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\genrpack.xsd");

				XML.createNode(doc, rootElement, "signobr", signobr);	
				XML.createNode(doc, rootElement, "keyobr", keyobr);	
				XML.createNode(doc, rootElement, "signcontr", signcontr);	
				XML.createNode(doc, rootElement, "keycontr", keycontr);	
				XML.createNode(doc, rootElement, "isgenbpack", Boolean.toString(isGenBpack));
				
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				File xml = new File(Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\genrpack.xml");
				StreamResult result = new StreamResult(xml);
				Log.msg("XML с настройками для генерации R-пакета " + Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\genrpack.xml создан.");
				//StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				XML.validate(Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\genrpack.xsd",Settings.fullfolder + "settings\\" + Settings.pervfolder + "\\genrpack.xml");

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
				XML.validate(Settings.testProj + "XMLSchema\\settings\\" + Settings.pervfolder + "\\genrpack.xsd",src);

				//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				NodeList nList = doc.getElementsByTagName("genrpack");


				for (int temp = 0; temp < nList.getLength(); temp++) {

					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;

						signobr = XML.getTagString("signobr", eElement);		
						keyobr = XML.getTagString("keyobr", eElement);	
						signcontr = XML.getTagString("signcontr", eElement);		
						keycontr = XML.getTagString("keycontr", eElement);		
						isGenBpack = Boolean.parseBoolean(XML.getTagString("isgenbpack", eElement));
					}
				}
				Log.msg("XML с настройками для генерации R-пакета " + src + " загружен в программу.");
			} catch (Exception e) {
				e.printStackTrace();
				Log.msg(e);
			}
		}
	}
	
	public static class GenSpack{
		
		public static int numBIK = 0;
		public static int numDoc = 0;
		public static int firstDoc = 0;
		public static String signobr = "";
		public static String keyobr = "";
		public static String signcontr = "";
		public static String keycontr = "";
		public static String error = "";
		
		public static void createXML()
		{
			try {

				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("genspack");
				doc.appendChild(rootElement);

				rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\settings\\" + Settings.obrfolder + "\\genspack.xsd");

				XML.createNode(doc, rootElement, "numbik", numBIK);	
				XML.createNode(doc, rootElement, "numdoc", numDoc);	
				XML.createNode(doc, rootElement, "firstdoc", firstDoc);
				XML.createNode(doc, rootElement, "signobr", signobr);	
				XML.createNode(doc, rootElement, "keyobr", keyobr);	
				XML.createNode(doc, rootElement, "signcontr", signcontr);	
				XML.createNode(doc, rootElement, "keycontr", keycontr);	
				XML.createNode(doc, rootElement, "error", error);	

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				File xml = new File(Settings.fullfolder + "settings\\" + Settings.obrfolder + "\\genspack.xml");
				StreamResult result = new StreamResult(xml);
				Log.msg("XML с настройками для генерации S-пакета " + Settings.fullfolder + "settings\\" + Settings.obrfolder + "\\genspack.xml создан.");
				//StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				XML.validate(Settings.testProj + "XMLSchema\\settings\\" + Settings.obrfolder + "\\genspack.xsd",Settings.fullfolder + "settings\\" + Settings.obrfolder + "\\genspack.xml");

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
				XML.validate(Settings.testProj + "XMLSchema\\settings\\" + Settings.obrfolder + "\\genspack.xsd",src);

				//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				NodeList nList = doc.getElementsByTagName("genspack");


				for (int temp = 0; temp < nList.getLength(); temp++) {

					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;
						
						numBIK = XML.getTagInt("numbik", eElement);	
						numDoc = XML.getTagInt("numdoc", eElement);	
						firstDoc = XML.getTagInt("firstdoc", eElement);
						signobr = XML.getTagString("signobr", eElement);		
						keyobr = XML.getTagString("keyobr", eElement);	
						signcontr = XML.getTagString("signcontr", eElement);		
						keycontr = XML.getTagString("keycontr", eElement);	
						error = XML.getTagString("error", eElement);

					}
				}
				Log.msg("XML с настройками для генерации S-пакета " + src + " загружен в программу.");
			} catch (Exception e) {
				e.printStackTrace();
				Log.msg(e);
			}
		}
	}
}


