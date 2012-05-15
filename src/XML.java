import java.io.File;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;


public abstract class XML {

	public static void validate(String xsd, String xml)
	{
		Source schemaFile = new StreamSource(new File(xsd));
		Source xmlFile = new StreamSource(new File(xml));
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();

			validator.validate(xmlFile);
			System.out.println(xmlFile.getSystemId() + " is valid");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
