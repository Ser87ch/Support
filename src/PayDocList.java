import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.io.File;
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

public class PayDocList {
	private List<PayDoc> pdl;

	PayDocList() 
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();

			ResultSet rs = db.st.executeQuery("select top 1 NUM_ACC from dbo.Account where rest = (select min(rest) from dbo.Account where substring(NUM_ACC,1,1) = '4' and substring(NUM_ACC,1,5) <> '40101')");
			rs.next();
			String ls = rs.getString("NUM_ACC");

			PayDoc.Client plat = new PayDoc.Client(Settings.bik, Settings.ks, ls);

			pdl = new ArrayList<PayDoc>();

			ResultSet rsbik = db.st.executeQuery("select top " + Settings.GenDoc.numBIK + " NEWNUM, isnull(KSNP,'') ksnp from dbo.BNKSEEK where substring(NEWNUM,1,4) = '" + Settings.bik.substring(0, 4) + "' and UER in ('2','3','4','5') and NEWNUM <> '" + Settings.bik + "'");

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
					pd.naznach = "Тест";
					pd.datesp = Settings.operDate;
					pd.datepost = Settings.operDate;

					pdl.add(pd);
				}
			}

			db.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
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

	public void CreateXML()
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
			while(iter.hasNext())
			{

				Element paydoc = doc.createElement("doc");
				rootElement.appendChild(paydoc);
				paydoc.setAttribute("id", Integer.toString(i));
				i++;
				
				PayDoc pd = iter.next();
				
				Element num = doc.createElement("num");
				num.appendChild(doc.createTextNode(Integer.toString(pd.num)));
				paydoc.appendChild(num);
				
				Element date = doc.createElement("date");
				date.appendChild(doc.createTextNode(new SimpleDateFormat("yyyy-MM-dd").format(pd.date)));
				paydoc.appendChild(date);
				
				Element vidop = doc.createElement("vidop");
				vidop.appendChild(doc.createTextNode(pd.vidop));
				paydoc.appendChild(vidop);
				
				Element sum = doc.createElement("sum");
				sum.appendChild(doc.createTextNode(Float.toString(pd.sum)));
				paydoc.appendChild(sum);
				
				Element vidpl = doc.createElement("vidpl");
				vidpl.appendChild(doc.createTextNode(pd.vidpl.toString()));
				paydoc.appendChild(vidpl);
				
				//плательщик
				Element plat = doc.createElement("plat");
				paydoc.appendChild(plat);
				
				Element platbik = doc.createElement("bik");
				platbik.appendChild(doc.createTextNode(pd.plat.bik));
				plat.appendChild(platbik);
				
				Element platks = doc.createElement("ks");
				platks.appendChild(doc.createTextNode(pd.plat.ks));
				plat.appendChild(platks);
				
				Element platls = doc.createElement("ls");
				platls.appendChild(doc.createTextNode(pd.plat.ls));
				plat.appendChild(platls);
				
				Element platinn = doc.createElement("inn");
				platinn.appendChild(doc.createTextNode(pd.plat.inn));
				plat.appendChild(platinn);
				
				Element platkpp = doc.createElement("kpp");
				platkpp.appendChild(doc.createTextNode(pd.plat.kpp));
				plat.appendChild(platkpp);
				
				Element platname = doc.createElement("name");
				platname.appendChild(doc.createTextNode(pd.plat.name));
				plat.appendChild(platname);
				
				//получатель
				Element pol = doc.createElement("pol");
				paydoc.appendChild(pol);
				
				Element polbik = doc.createElement("bik");
				polbik.appendChild(doc.createTextNode(pd.pol.bik));
				pol.appendChild(polbik);
				
				Element polks = doc.createElement("ks");
				polks.appendChild(doc.createTextNode(pd.pol.ks));
				pol.appendChild(polks);
				
				Element polls = doc.createElement("ls");
				polls.appendChild(doc.createTextNode(pd.pol.ls));
				pol.appendChild(polls);
				
				Element polinn = doc.createElement("inn");
				polinn.appendChild(doc.createTextNode(pd.pol.inn));
				pol.appendChild(polinn);
				
				Element polkpp = doc.createElement("kpp");
				polkpp.appendChild(doc.createTextNode(pd.pol.kpp));
				pol.appendChild(polkpp);
				
				Element polname = doc.createElement("name");
				polname.appendChild(doc.createTextNode(pd.pol.name));
				pol.appendChild(polname);
				
				Element ocher = doc.createElement("ocher");
				ocher.appendChild(doc.createTextNode(Integer.toString(pd.ocher)));
				paydoc.appendChild(ocher);
				
				Element status = doc.createElement("status");
				status.appendChild(doc.createTextNode(pd.status));
				paydoc.appendChild(status);
				
				if(pd.status != "")
				{
					Element kbk = doc.createElement("kbk");
					kbk.appendChild(doc.createTextNode(pd.kbk));
					paydoc.appendChild(kbk);
					
					Element okato = doc.createElement("okato");
					okato.appendChild(doc.createTextNode(pd.okato));
					paydoc.appendChild(okato);
					
					Element osn = doc.createElement("osn");
					osn.appendChild(doc.createTextNode(pd.osn));
					paydoc.appendChild(osn);
					
					Element nalper = doc.createElement("nalper");
					nalper.appendChild(doc.createTextNode(pd.nalper));
					paydoc.appendChild(nalper);
					
					Element numdoc = doc.createElement("numdoc");
					numdoc.appendChild(doc.createTextNode(pd.numdoc));
					paydoc.appendChild(status);
					
					Element datedoc = doc.createElement("datedoc");
					datedoc.appendChild(doc.createTextNode(pd.datedoc));
					paydoc.appendChild(datedoc);
					
					Element typepl = doc.createElement("typepl");
					typepl.appendChild(doc.createTextNode(pd.typepl));
					paydoc.appendChild(typepl);								
				}
				Element naznach = doc.createElement("naznach");
				naznach.appendChild(doc.createTextNode(pd.naznach));
				paydoc.appendChild(naznach);		
				
				Element datesp = doc.createElement("datesp");
				datesp.appendChild(doc.createTextNode(new SimpleDateFormat("yyyy-MM-dd").format(pd.datesp)));
				paydoc.appendChild(datesp);
				
				Element datepost = doc.createElement("datepost");
				datepost.appendChild(doc.createTextNode(new SimpleDateFormat("yyyy-MM-dd").format(pd.datepost)));
				paydoc.appendChild(datepost);
			}
		
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(Settings.testProj + Settings.folder + "\\input\\paydocs.xml"));

			transformer.transform(source, result);

			System.out.println("File saved!");

			XML.validate(Settings.testProj + "XMLSchema\\input\\paydocs.xsd",Settings.testProj + Settings.folder + "\\input\\paydocs.xml");
			
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
}

