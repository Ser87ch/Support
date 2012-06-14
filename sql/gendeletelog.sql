--------------------Log Triggers--------------------
use System
go
declare cur cursor for  
select name,id from dbo.sysobjects where xtype = 'u' and not (name like '%_log')

declare @tblname varchar(128), @tblid int, @str varchar(8000)

 

open cur
fetch next from cur into @tblname, @tblid  

while @@fetch_status = 0   
begin	
	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_ins_trg''))
drop trigger ' + @tblname + '_ins_trg'
	print @str
	exec(@str)

	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_upd_trg''))
drop trigger ' + @tblname + '_upd_trg'
	print @str
	exec(@str)

	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_del_trg''))
drop trigger ' + @tblname + '_del_trg'
	print @str
	exec(@str)

	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_log''))
drop table ' + @tblname + '_log'
	print @str
	exec(@str)
	
	fetch next from cur into @tblname, @tblid    
end   

close cur   
deallocate cur