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
	static private List<String> tables;

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
			ListIterator <String> iter = tables.listIterator();

			Log.msg("Начало создания XML с изменениями в БД.");
			while(iter.hasNext())
			{

				Element tbl = doc.createElement("table");
				rootElement.appendChild(tbl);
				String table = iter.next()+"_log";
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

		//	XML.validate(Settings.testProj + "XMLSchema\\output\\deltadb.xsd",Settings.testProj + "\\tests\\" +  Settings.folder + "\\output\\" + filename);			



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

	public static void createDBLog()
	{
		try 
		{
			DB db = new DB(Settings.server, Settings.db, Settings.user, Settings.pwd);
			db.connect();
			String s = "declare cur cursor for  \r\n" + 
			"select name,id from dbo.sysobjects where xtype = 'u' and not (name like '%_log') and name in("+ DeltaDB.toStr() +")\r\n" + 
			"\r\n" + 
			"declare @tblname varchar(128), @tblid int, @str varchar(8000)\r\n" + 
			"\r\n" + 
			" \r\n" + 
			"\r\n" + 
			"open cur\r\n" + 
			"fetch next from cur into @tblname, @tblid  \r\n" + 
			"\r\n" + 
			"while @@fetch_status = 0   \r\n" + 
			"begin   \r\n" + 
			"	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_log''))\r\n" + 
			"drop table ' + @tblname + '_log\r\n" + 
			"\r\n" + 
			"	create table dbo.' + @tblname + '_log(\r\n" + 
			"'\r\n" + 
			"	declare curcol cursor for  \r\n" + 
			"	select c.name, t.name, c.length, c.xprec, c.xscale  from dbo.syscolumns c \r\n" + 
			"	inner join dbo.systypes t on c.xtype = t.xtype\r\n" + 
			"	where c.id = @tblid and t.name <> 'NCIID'\r\n" + 
			"\r\n" + 
			"	declare @col varchar(128), @type varchar(128), @len smallint, @prec tinyint, @scale tinyint\r\n" + 
			"	\r\n" + 
			"	open curcol\r\n" + 
			"	fetch next from curcol into @col, @type, @len, @prec, @scale\r\n" + 
			"	\r\n" + 
			"	while @@fetch_status = 0   \r\n" + 
			"	begin 	\r\n" + 
			"		set @str = @str + @col + ' ' + @type\r\n" + 
			"		\r\n" + 
			"		if @type in ('char','varchar','nchar','nvarchar')\r\n" + 
			"			set @str = @str + ' (' + convert(varchar,@len) + ')'\r\n" + 
			"\r\n" + 
			"		if @type = 'decimal'\r\n" + 
			"			set @str = @str + ' (' + convert(varchar,@prec) + ',' +  convert(varchar,@scale) + ')'\r\n" + 
			"\r\n" + 
			"		\r\n" + 
			"		set @str = @str + ',\r\n" + 
			"'\r\n" + 
			"		fetch next from curcol into @col, @type, @len, @prec, @scale \r\n" + 
			"	end   \r\n" + 
			"\r\n" + 
			"	close curcol   \r\n" + 
			"	deallocate curcol\r\n" + 
			"	\r\n" + 
			"	set @str = @str + 'changedate datetime,\r\n" + 
			"action varchar(2)\r\n" + 
			")'\r\n" + 
			"	print @str\r\n" + 
			"	exec(@str)\r\n" + 
			"	fetch next from cur into @tblname, @tblid     \r\n" + 
			"\r\n" + 
			"end   \r\n" + 
			"\r\n" + 
			"close cur   \r\n" + 
			"deallocate cur";
			db.st.executeUpdate(s);
			Log.msg("Таблицы для записи лога для таблиц " + toStr() + " созданы.");
			s = "declare cur cursor for  \r\n" + 
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
			"begin   \r\n" + 
			"	\r\n" + 
			"	declare curcol cursor for  \r\n" + 
			"	select c.name from dbo.syscolumns c 	\r\n" + 
			" 	where c.id = @tblid\r\n" + 
			"\r\n" + 
			"	declare @col varchar(128), @strins varchar(8000), @strsel varchar(8000)\r\n" + 
			"	set @strins = ''\r\n" + 
			"	set @strsel = ''\r\n" + 
			"\r\n" + 
			"	open curcol\r\n" + 
			"	fetch next from curcol into @col\r\n" + 
			"	\r\n" + 
			"	while @@fetch_status = 0   \r\n" + 
			"	begin 	\r\n" + 
			"		set @strins = @strins + @col + ',\r\n" + 
			"'		\r\n" + 
			"		set @strsel = @strsel + @col + ', '\r\n" + 
			"		fetch next from curcol into @col\r\n" + 
			"	end   \r\n" + 
			"	set @strins = @strins + 'changedate,\r\n" + 
			"action'\r\n" + 
			"\r\n" + 
			"	close curcol   \r\n" + 
			"	deallocate curcol\r\n" + 
			"\r\n" + 
			"	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_ins_trg''))\r\n" + 
			"drop trigger ' + @tblname + '_ins_trg'\r\n" + 
			"	print @str\r\n" + 
			"	exec(@str)\r\n" + 
			"\r\n" + 
			"	set @str = 'create trigger dbo.' + @tblname + '_ins_trg on dbo.' + @tblname + '\r\n" + 
			"for insert\r\n" + 
			"as\r\n" + 
			"begin\r\n" + 
			"insert into dbo.' + @tblname + '_log\r\n" + 
			"(\r\n" + 
			"' + @strins + '\r\n" + 
			")\r\n" + 
			"select ' + @strsel + ' getdate(), ''i''\r\n" + 
			"from Inserted\r\n" + 
			"end'\r\n" + 
			"	print @str\r\n" + 
			"	exec(@str)\r\n" + 
			"\r\n" + 
			"	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_upd_trg''))\r\n" + 
			"drop trigger ' + @tblname + '_upd_trg'\r\n" + 
			"	print @str\r\n" + 
			"	exec(@str)\r\n" + 
			"\r\n" + 
			"	set @str = 'create trigger dbo.' + @tblname + '_upd_trg on dbo.' + @tblname + '\r\n" + 
			"for update\r\n" + 
			"as\r\n" + 
			"begin\r\n" + 
			"insert into dbo.' + @tblname + '_log\r\n" + 
			"(\r\n" + 
			"' + @strins + '\r\n" + 
			")\r\n" + 
			"select ' + @strsel + ' getdate(), ''ud''\r\n" + 
			"from Deleted\r\n" + 
			"\r\n" + 
			"insert into dbo.' + @tblname + '_log\r\n" + 
			"(\r\n" + 
			"' + @strins + '\r\n" + 
			")\r\n" + 
			"select ' + @strsel + ' getdate(), ''ui''\r\n" + 
			"from Inserted\r\n" + 
			"end'\r\n" + 
			"	print @str\r\n" + 
			"	exec(@str)\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_del_trg''))\r\n" + 
			"drop trigger ' + @tblname + '_del_trg'\r\n" + 
			"	print @str\r\n" + 
			"	exec(@str)\r\n" + 
			"\r\n" + 
			"	set @str = 'create trigger dbo.' + @tblname + '_del_trg on dbo.' + @tblname + '\r\n" + 
			"for delete\r\n" + 
			"as\r\n" + 
			"begin\r\n" + 
			"insert into dbo.' + @tblname + '_log\r\n" + 
			"(\r\n" + 
			"' + @strins + '\r\n" + 
			")\r\n" + 
			"select ' + @strsel + ' getdate(), ''d''\r\n" + 
			"from Deleted\r\n" + 
			"end'\r\n" + 
			"	print @str\r\n" + 
			"	exec(@str)	\r\n" + 
			"	\r\n" + 
			"	fetch next from cur into @tblname, @tblid     \r\n" + 
			"end   \r\n" + 
			"\r\n" + 
			"close cur   \r\n" + 
			"deallocate cur";
			db.st.executeUpdate(s);
			Log.msg("Триггера для записи лога для таблиц " + toStr() + " созданы.");
			db.close();
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
		ListIterator <String> iter = tables.listIterator();
		String s = "";
		int i = 0;
		while(iter.hasNext()) 
		{
			s = s + ((i==0)?"":", ") + "'" + iter.next() + "'";
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

			ListIterator <String> iter = tables.listIterator();
			while(iter.hasNext())
			{
				String s = iter.next();
				XML.createNode(doc, rootElement, "table", s);

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



			tables = new ArrayList<String>();

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Element NmElmnt = (Element) nList.item(temp);
				NodeList Nm = NmElmnt.getChildNodes();   
				String s = ((Node) Nm.item(0)).getNodeValue();						
				tables.add(s);
			}
			Log.msg("XML с настройками для подсчета изменений в БД " + src + " загружен в программу.");
		} catch (Exception e) {
			e.printStackTrace();
			Log.msg(e);
		}
	}
}
