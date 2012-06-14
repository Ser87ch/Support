--------------------Log Tables--------------------
use System
go
declare cur cursor for  
select name,id from dbo.sysobjects where xtype = 'u' and not (name like '%_log')

declare @tblname varchar(128), @tblid int, @str varchar(8000)

 

open cur
fetch next from cur into @tblname, @tblid  

while @@fetch_status = 0   
begin   
	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_log''))
drop table ' + @tblname + '_log

	create table dbo.' + @tblname + '_log(
'
	declare curcol cursor for  
	select c.name, t.name, c.length, c.xprec, c.xscale  from dbo.syscolumns c 
	inner join dbo.systypes t on c.xtype = t.xtype
	where c.id = @tblid and t.name <> 'NCIID'

	declare @col varchar(128), @type varchar(128), @len smallint, @prec tinyint, @scale tinyint
	
	open curcol
	fetch next from curcol into @col, @type, @len, @prec, @scale
	
	while @@fetch_status = 0   
	begin 	
		set @str = @str + @col + ' ' + @type
		
		if @type in ('char','varchar','nchar','nvarchar')
			set @str = @str + ' (' + convert(varchar,@len) + ')'

		if @type = 'decimal'
			set @str = @str + ' (' + convert(varchar,@prec) + ',' +  convert(varchar,@scale) + ')'

		
		set @str = @str + ',
'
		fetch next from curcol into @col, @type, @len, @prec, @scale 
	end   

	close curcol   
	deallocate curcol
	
	set @str = @str + 'changedate datetime,
action varchar(2)
)'
	print @str
	exec(@str)
	fetch next from cur into @tblname, @tblid     

end   

close cur   
deallocate cur