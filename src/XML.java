import java.io.File;
import java.sql.Date;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XML {

	public static void validate(String xsd, String xml)
	{
		try {
			Source schemaFile = new StreamSource(new File(xsd));
			Source xmlFile = new StreamSource(new File(xml));
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();

			validator.validate(xmlFile);

			Log.msg("XML файл " + xml + " соответствует схеме " + xsd + ".");
		} catch (Exception e) {
			e.printStackTrace();
			Log.msg("XML файл " + xml + " не соответствует схеме " + xsd + ".");
			Log.msg(e);
		}
	}

	public static String getTagString(String sTag, Element eElement) {
		try {
			if(eElement.getElementsByTagName(sTag).item(0) == null)
				return "";
			NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

			Node nValue = (Node) nlList.item(0);
			
			if(nValue == null)
				return "";
			
			return nValue.getNodeValue();

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static int getTagInt(String sTag, Element eElement) {
		try {
			if(eElement.getElementsByTagName(sTag).item(0) == null)
				return 0;
			NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

			Node nValue = (Node) nlList.item(0);

			if(nValue == null)
				return 0;
			
			return Integer.parseInt(nValue.getNodeValue());

		} catch (Exception e) {
			//	e.printStackTrace();
			return 0;
		}
	}

	public static Date getTagDate(String sTag, Element eElement) {
		try {
			if(eElement.getElementsByTagName(sTag).item(0) == null)
				return new Date(0);
			NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

			Node nValue = (Node) nlList.item(0);
			
			if(nValue == null)
				return new Date(0);
			
			return Date.valueOf(nValue.getNodeValue());

		} catch (Exception e) {
			//	e.printStackTrace();
			return new Date(0);
		}
	}
	
	public static float getTagFloat(String sTag, Element eElement) {
		try {
			if(eElement.getElementsByTagName(sTag).item(0) == null)
				return 0;
			NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

			Node nValue = (Node) nlList.item(0);

			return Float.parseFloat(nValue.getNodeValue());

		} catch (Exception e) {
			//	e.printStackTrace();
			return 0;
		}
	}
}
