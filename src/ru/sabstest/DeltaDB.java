package ru.sabstest;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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





public class DeltaDB {
	static private List<Table> tables;

	public static void createXML(String filename)
	{
		try 
		{
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);

			db.connect();

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("deltadb");
			doc.appendChild(rootElement);

			rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\output\\deltadb.xsd");
			ListIterator <Table> iter = tables.listIterator();

			Log.msg("Начало создания XML с изменениями в БД.");
			while(iter.hasNext())
			{

				Element tbl = doc.createElement("table");
				rootElement.appendChild(tbl);
				String table = iter.next().name + "_log";
				tbl.setAttribute("name", table);
				ResultSet rs = db.st.executeQuery("select * from "+ table + " order by changedate,action");

				while(rs.next()) 
				{
					Element row = doc.createElement("row");
					row.setAttribute("id", Integer.toString(rs.getRow()));
					row.setAttribute("action", rs.getString("action"));
					row.setAttribute("changedate", rs.getString("changedate"));
					tbl.appendChild(row);

					ResultSetMetaData rsMetaData = rs.getMetaData();

					int numberOfColumns = rsMetaData.getColumnCount();
					for(int i = 1; i <= numberOfColumns; i++)
					{	
						if(rsMetaData.getColumnName(i) != "action" && rsMetaData.getColumnName(i) != "changedate")
						{
							Element col = doc.createElement("column");
							col.setAttribute("name", rsMetaData.getColumnName(i));
							String s = rs.getString(i)!= null ? rs.getString(i) : "";
							col.appendChild(doc.createTextNode(s));
							row.appendChild(col);	
						}
					}

				}
				Log.msg("Таблица с измениями в БД '" + table + "' записана в XML.");
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(Settings.testProj + "\\tests\\" + Settings.folder + "\\output\\" + filename));

			transformer.transform(source, result);
			Log.msg("XML с изменениями в БД " + Settings.testProj + "\\tests\\" + Settings.folder + "\\output\\" + filename + " создан.");			

			XML.validate(Settings.testProj + "XMLSchema\\output\\deltadb.xsd",Settings.testProj + "\\tests\\" +  Settings.folder + "\\output\\" + filename);			



			db.close();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			Log.msg(pce);
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
			Log.msg(tfe);
		}
		catch (Exception e) {
			Log.msg(e);
			e.printStackTrace();
		}
	}

	private static boolean createTableLog(Table t)
	{
		try
		{
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();
			String s = "select c.name cname, t.name tname, c.length, c.xprec, c.xscale  from dbo.syscolumns c \r\n" + 
			"	inner join dbo.systypes t on c.xtype = t.xtype\r\n" + 
			"	inner join dbo.sysobjects tb on tb.id = c.id and tb.xtype = 'u' and not (tb.name like '%_log')\r\n" + 
			"	where tb.name = '" + t.name + "' and t.name <> 'NCIID'\r\n and c.name in("+ t.toStr() +")\r\n";

			ResultSet rs = db.st.executeQuery(s);
			if (rs.next()) 
			{  
				s = "if  exists (select * from dbo.sysobjects where id = object_id('" + t.name + "_log'))\r\n" + 
				"drop table " + t.name + "_log\r\n" + 
				"\r\n" + 
				"create table dbo." + t.name + "_log(\r\n";
				do 
				{  
					s = s + rs.getString("cname") + " " + rs.getString("tname");
					if(rs.getString("tname").equals("char")|| rs.getString("tname").equals("varchar") || rs.getString("tname").equals("nchar")  || rs.getString("tname").equals("nvarchar"))
						s = s + "(" + rs.getString("length") + ")";					
					else if(rs.getString("tname").equals("decimal"))
						s = s + "(" + rs.getString("xprec") + ", " + rs.getString("xscale") + ")";
					s = s + ",\r\n";


				} while (rs.next());  
			} else {  
				return false;
			}
			s = s + "changedate datetime,\r\n" + 
			"action varchar(2))\r\n";	
			db.st.executeUpdate(s);
			db.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.msg(e);
			return false;
		}

	}

	private static boolean createTriggerIns(Table t)
	{
		try
		{
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();
			String s = "select c.name cname, t.name tname, c.length, c.xprec, c.xscale  from dbo.syscolumns c \r\n" + 
			"	inner join dbo.systypes t on c.xtype = t.xtype\r\n" + 
			"	inner join dbo.sysobjects tb on tb.id = c.id and tb.xtype = 'u' and not (tb.name like '%_log')\r\n" + 
			"	where tb.name = '" + t.name + "' and t.name <> 'NCIID'\r\n and c.name in("+ t.toStr() +")\r\n";
			ResultSet rs = db.st.executeQuery(s);
			if (rs.next()) 
			{
				String drop = "if  exists (select * from dbo.sysobjects where id = object_id('" + t.name + "_ins_trg'))\r\n drop trigger " + t.name +"_ins_trg\r\n";

				s = "create trigger dbo." + t.name + "_ins_trg on dbo." + t.name + "\r\n" + 
				"for insert\r\n" + 
				"as\r\n" + 
				"begin\r\n insert into " + t.name + "_log\r\n" + 
				"(\r\n";

				String a = "";

				do 
				{
					a = a + rs.getString("cname") + ", ";
				} while (rs.next());  
				s = s + a + "changedate, action\r\n" +
				")\r\n" + 
				"select " + a + "getdate(), 'i'\r\n" + 
				"from Inserted\r\n" + 
				"end\r\n";
				db.st.executeUpdate(drop);

			} else {  
				return false;
			}

			db.st.executeUpdate(s);
			db.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.msg(e);
			return false;
		}
	}

	private static boolean createTriggerDel(Table t)
	{
		try
		{
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();
			String s = "select c.name cname, t.name tname, c.length, c.xprec, c.xscale  from dbo.syscolumns c \r\n" + 
			"	inner join dbo.systypes t on c.xtype = t.xtype\r\n" + 
			"	inner join dbo.sysobjects tb on tb.id = c.id and tb.xtype = 'u' and not (tb.name like '%_log')\r\n" + 
			"	where tb.name = '" + t.name + "' and t.name <> 'NCIID'\r\n and c.name in("+ t.toStr() +")\r\n";
			ResultSet rs = db.st.executeQuery(s);
			if (rs.next()) 
			{
				String drop = "if  exists (select * from dbo.sysobjects where id = object_id('" + t.name + "_del_trg'))\r\n drop trigger " + t.name +"_del_trg\r\n";

				s = "create trigger dbo." + t.name + "_del_trg on dbo." + t.name + "\r\n" + 
				"for delete\r\n" + 
				"as\r\n" + 
				"begin\r\n insert into " + t.name + "_log\r\n" + 
				"(\r\n";

				String a = "";

				do 
				{
					a = a + rs.getString("cname") + ", ";
				} while (rs.next());  
				s = s + a + "changedate, action\r\n" +
				")\r\n" + 
				"select " + a + "getdate(), 'd'\r\n" + 
				"from Deleted\r\n" + 
				"end\r\n";
				db.st.executeUpdate(drop);

			} else {  
				return false;
			}

			db.st.executeUpdate(s);
			db.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.msg(e);
			return false;
		}
	}

	private static boolean createTriggerUpd(Table t)
	{
		try
		{
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();
			String s = "select c.name cname, t.name tname, c.length, c.xprec, c.xscale  from dbo.syscolumns c \r\n" + 
			"	inner join dbo.systypes t on c.xtype = t.xtype\r\n" + 
			"	inner join dbo.sysobjects tb on tb.id = c.id and tb.xtype = 'u' and not (tb.name like '%_log')\r\n" + 
			"	where tb.name = '" + t.name + "' and t.name <> 'NCIID'\r\n and c.name in("+ t.toStr() +")\r\n";
			ResultSet rs = db.st.executeQuery(s);
			if (rs.next()) 
			{
				String drop = "if  exists (select * from dbo.sysobjects where id = object_id('" + t.name + "_upd_trg'))\r\n drop trigger " + t.name +"_upd_trg\r\n";

				s = "create trigger dbo." + t.name + "_upd_trg on dbo." + t.name + "\r\n" + 
				"for update\r\n" + 
				"as\r\n" + 
				"begin\r\n" ;

				String a = "", b = "";				

				do 
				{
					a = a + rs.getString("cname") + ", ";
					b = b + "i." + rs.getString("cname") + " = d." + rs.getString("cname") + " and ";
				} while (rs.next());  


				String prkeysql = "Select o2.name As TableName, o.name As PrimaryKeyName, tc.name As ColumnName\r\n" + 
				"From sysconstraints c\r\n" + 
				"Inner Join sysobjects o On c.constid = o.id\r\n" + 
				"Inner Join sysindexes i On o.name = i.name And o.parent_obj = i.id\r\n" + 
				"Inner Join sysindexkeys ik On i.id = ik.id And i.indid = ik.indid\r\n" + 
				"Inner Join syscolumns tc On ik.id = tc.id And ik.colid = tc.colid\r\n" + 
				"Inner Join sysobjects o2 On ik.id = o2.id\r\n" + 
				"Where o.xtype = 'PK' And o2.xtype = 'U' and o2.name = '" + t.name + "'\r\n";
				
				rs = db.st.executeQuery(prkeysql);
				rs.next();
				String prkey = rs.getString("ColumnName");
				b = b + "i." + prkey + " = d." + prkey;
				
				s = s + "insert into " + t.name + "_log\r\n" + 
				"(\r\n" +
				a + "changedate, action\r\n" +
				")\r\n" + 
				"select " + a + "getdate(), 'ud'\r\n" + 
				"from Deleted d\r\n" + 
				"where not exists(select 1 from Inserted i where " + b + ")\r\n" + 
				"insert into " + t.name + "_log\r\n" + 
				"(\r\n" + a + "changedate, action\r\n" +
				")\r\n" + 
				"select " + a + "getdate(), 'ui'\r\n" + 
				"from Inserted i\r\n" +		
				"where not exists(select 1 from Deleted d where " + b + ")\r\n" + 
				"end\r\n";

				db.st.executeUpdate(drop);

			} else {  
				return false;
			}

			db.st.executeUpdate(s);
			db.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.msg(e);
			return false;
		}
	}

	public static void createDBLog()
	{
		try 
		{
			ListIterator <Table> iter = tables.listIterator();

			while(iter.hasNext()) 
			{
				Table t = iter.next();
				createTableLog(t);
				createTriggerIns(t);
				createTriggerDel(t);
				createTriggerUpd(t);
			}
			Log.msg("Таблицы для записи лога для таблиц " + toStr() + " созданы.");
			Log.msg("Триггера для записи лога для таблиц " + toStr() + " созданы.");

		} catch (Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}

	public static void deleteDBLog()
	{
		try {
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();
			String s = "declare cur cursor for  \r\n" + 
			"select name,id from dbo.sysobjects where xtype = 'u' and not (name like '%_log')and name in("+ DeltaDB.toStr() +")\r\n" + 
			"\r\n" + 
			"declare @tblname varchar(128), @tblid int, @str varchar(8000)\r\n" + 
			"\r\n" + 
			" \r\n" + 
			"\r\n" + 
			"open cur\r\n" + 
			"fetch next from cur into @tblname, @tblid  \r\n" + 
			"\r\n" + 
			"while @@fetch_status = 0   \r\n" + 
			"begin	\r\n" + 
			"	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_ins_trg''))\r\n" + 
			"drop trigger ' + @tblname + '_ins_trg'\r\n" + 
			"	print @str\r\n" + 
			"	exec(@str)\r\n" + 
			"\r\n" + 
			"	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_upd_trg''))\r\n" + 
			"drop trigger ' + @tblname + '_upd_trg'\r\n" + 
			"	print @str\r\n" + 
			"	exec(@str)\r\n" + 
			"\r\n" + 
			"	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_del_trg''))\r\n" + 
			"drop trigger ' + @tblname + '_del_trg'\r\n" + 
			"	print @str\r\n" + 
			"	exec(@str)\r\n" + 
			"\r\n" + 
			"	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_log''))\r\n" + 
			"drop table ' + @tblname + '_log'\r\n" + 
			"	print @str\r\n" + 
			"	exec(@str)\r\n" + 
			"	\r\n" + 
			"	fetch next from cur into @tblname, @tblid    \r\n" + 
			"end   \r\n" + 
			"\r\n" + 
			"close cur   \r\n" + 
			"deallocate cur";
			db.st.executeUpdate(s);

			Log.msg("Таблицы и триггера для записи лога для таблиц " + toStr() + " удалены.");
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}

	public static String toStr()
	{
		ListIterator <Table> iter = tables.listIterator();
		String s = "";
		int i = 0;
		while(iter.hasNext()) 
		{
			s = s + ((i==0)?"":", ") + "'" + iter.next().name + "'";
			i++;
		}


		return s;

	}

	public static void createXMLSettings()
	{
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("deltadb");
			doc.appendChild(rootElement);

			rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Settings.testProj + "XMLSchema\\settings\\deltadb.xsd");

			ListIterator <Table> iter = tables.listIterator();
			while(iter.hasNext())
			{
				Table t = iter.next();				
				Element tbl = doc.createElement("table");
				tbl.setAttribute("name", t.name);
				rootElement.appendChild(tbl);				
				ListIterator <String> sitr = t.columns.listIterator();
				while(sitr.hasNext())
				{
					XML.createNode(doc, tbl, "column", sitr.next());
				}
			}		

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			File xml = new File(Settings.fullfolder + "settings\\deltadb.xml");
			StreamResult result = new StreamResult(xml);
			Log.msg("XML с настройками для подсчета изменений в БД " + Settings.fullfolder + "settings\\deltadb.xml создан.");
			//StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			XML.validate(Settings.testProj + "XMLSchema\\settings\\deltadb.xsd",Settings.fullfolder + "settings\\deltadb.xml");

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

	public static void readXMLSettings(String src)
	{
		try {

			File fXmlFile = new File(src);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			XML.validate(Settings.testProj + "XMLSchema\\settings\\deltadb.xsd",src);

			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("table");		

			tables = new ArrayList<Table>();

			for (int temp = 0; temp < nList.getLength(); temp++)
			{
				Element NmElmnt = (Element) nList.item(temp);
				String s = NmElmnt.getAttribute("name");			
				Table t = new Table(s);

				NodeList nlList = NmElmnt.getElementsByTagName("column");
				for (int i = 0; i < nlList.getLength(); i++)
				{
					Element Elmnt = (Element) nlList.item(i);					
					NodeList Nm = Elmnt.getChildNodes();   
					s = ((Node) Nm.item(0)).getNodeValue();	
					t.add(s);
				}				
				tables.add(t);
			}
			Log.msg("XML с настройками для подсчета изменений в БД " + src + " загружен в программу.");
		} catch (Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}

	public static class Table
	{
		String name;
		List<String> columns;

		Table(String tbl)
		{
			name = tbl;
			columns = new ArrayList<String>();
		}

		public void add(String col)
		{
			columns.add(col);
		}

		public String toStr()
		{
			ListIterator <String> iter = columns.listIterator();
			String s = "";
			int i = 0;
			while(iter.hasNext()) 
			{
				s = s + ((i==0)?"":", ") + "'" + iter.next() + "'";
				i++;
			}


			return s;

		}
	}
}
