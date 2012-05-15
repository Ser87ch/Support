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

public class Settings{

	public static String server = "CHERNOBRIVENKO\\ATLANT:1259";
	public static String db = "sabs_zapd";
	public static String user = "robot";
	public static String pwd = "1";
	public static String path = "";
	public static String bik = "";
	public static String ks = "";
	public static Date operDate = new Date(0);
	public static String testProj = "G:\\sabstest\\";
	public static String folder = "a0001";

	public static void Load()
	{
		try {
		DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
		db.connect();

		//получение папки сабс
		ResultSet rs = db.st.executeQuery("select VALUE from dbo.XDM_CONF_SETTINGS where ID_PARAM = 1004");
		rs.next();
		Settings.path = rs.getString("VALUE");
		
		rs = db.st.executeQuery("SELECT top 1 OPER_DATE as dt FROM XDM_OPERDAY_PROP WHERE VALUE = 0");
		rs.next();
		Settings.operDate = rs.getDate("dt");

		rs = db.st.executeQuery("select X.BIK as BIK , isnull(B.KSNP,'') as KSNP from dbo.XDM_DEPARTMENT X inner join dbo.BNKSEEK B on B.NEWNUM = X.BIK");
		rs.next();
		Settings.bik = rs.getString("BIK");
		Settings.ks = rs.getString("KSNP");

		
		db.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void CreateXML()
	{
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("general");
			doc.appendChild(rootElement);

			rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\settings\\general.xsd");

			Element server = doc.createElement("server");
			server.appendChild(doc.createTextNode(Settings.server));
			rootElement.appendChild(server);

			Element db = doc.createElement("db");
			db.appendChild(doc.createTextNode(Settings.db));
			rootElement.appendChild(db);

			Element user = doc.createElement("user");
			user.appendChild(doc.createTextNode(Settings.user));
			rootElement.appendChild(user);

			Element pwd = doc.createElement("pwd");
			pwd.appendChild(doc.createTextNode(Settings.pwd));
			rootElement.appendChild(pwd);

			Element path = doc.createElement("path");
			path.appendChild(doc.createTextNode(Settings.path));
			rootElement.appendChild(path);

			Element bik = doc.createElement("bik");
			bik.appendChild(doc.createTextNode(Settings.bik));
			rootElement.appendChild(bik);
			
			Element ks = doc.createElement("ks");
			ks.appendChild(doc.createTextNode((Settings.ks)));
			rootElement.appendChild(ks);

			Element operdate = doc.createElement("operdate");
			operdate.appendChild(doc.createTextNode(new SimpleDateFormat("yyyy-MM-dd").format(Settings.operDate)));
			rootElement.appendChild(operdate);

			Element testproj = doc.createElement("testproj");
			testproj.appendChild(doc.createTextNode(Settings.testProj));
			rootElement.appendChild(testproj);
			
			Element folder = doc.createElement("folder");
			folder.appendChild(doc.createTextNode(Settings.folder));
			rootElement.appendChild(folder);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(Settings.testProj + Settings.folder + "\\settings\\general.xml"));

			//StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("File saved!");

			XML.validate(Settings.testProj + "XMLSchema\\settings\\general.xsd",Settings.testProj + Settings.folder + "\\settings\\general.xml");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	public static class GenDoc{
		public static int numBIK = 2;
		public static int numDoc = 5;
		public static int firstDoc = 1;

		public static void CreateXML()
		{
			try {

				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("gendoc");
				doc.appendChild(rootElement);

				rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\settings\\gendoc.xsd");

				Element numbik = doc.createElement("numbik");
				numbik.appendChild(doc.createTextNode(Integer.toString(GenDoc.numBIK)));
				rootElement.appendChild(numbik);

				Element numdoc = doc.createElement("numdoc");
				numdoc.appendChild(doc.createTextNode(Integer.toString(GenDoc.numDoc)));
				rootElement.appendChild(numdoc);

				Element firstdoc = doc.createElement("firstdoc");
				firstdoc.appendChild(doc.createTextNode(Integer.toString(GenDoc.firstDoc)));
				rootElement.appendChild(firstdoc);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(Settings.testProj + Settings.folder + "\\settings\\gendoc.xml"));

				//StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				System.out.println("File saved!");

				XML.validate(Settings.testProj + "XMLSchema\\settings\\gendoc.xsd",Settings.testProj + Settings.folder + "\\settings\\gendoc.xml");

			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			} catch (TransformerException tfe) {
				tfe.printStackTrace();
			}

		}
	}


}
