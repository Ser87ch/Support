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
	
	declare curcol cursor for  
	select c.name from dbo.syscolumns c 	
 	where c.id = @tblid

	declare @col varchar(128), @strins varchar(8000), @strsel varchar(8000)
	set @strins = ''
	set @strsel = ''

	open curcol
	fetch next from curcol into @col
	
	while @@fetch_status = 0   
	begin 	
		set @strins = @strins + @col + ',
'		
		set @strsel = @strsel + @col + ', '
		fetch next from curcol into @col
	end   
	set @strins = @strins + 'changedate,
action'

	close curcol   
	deallocate curcol

	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_ins_trg''))
drop trigger ' + @tblname + '_ins_trg'
	print @str
	exec(@str)

	set @str = 'create trigger dbo.' + @tblname + '_ins_trg on dbo.' + @tblname + '
for insert
as
begin
insert into dbo.' + @tblname + '_log
(
' + @strins + '
)
select ' + @strsel + ' getdate(), ''i''
from Inserted
end'
	print @str
	exec(@str)

	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_upd_trg''))
drop trigger ' + @tblname + '_upd_trg'
	print @str
	exec(@str)

	set @str = 'create trigger dbo.' + @tblname + '_upd_trg on dbo.' + @tblname + '
for update
as
begin
insert into dbo.' + @tblname + '_log
(
' + @strins + '
)
select ' + @strsel + ' getdate(), ''ud''
from Deleted

insert into dbo.' + @tblname + '_log
(
' + @strins + '
)
select ' + @strsel + ' getdate(), ''ui''
from Inserted
end'
	print @str
	exec(@str)


	set @str = 'if  exists (select * from dbo.sysobjects where id = object_id(''' + @tblname + '_del_trg''))
drop trigger ' + @tblname + '_del_trg'
	print @str
	exec(@str)

	set @str = 'create trigger dbo.' + @tblname + '_del_trg on dbo.' + @tblname + '
for delete
as
begin
insert into dbo.' + @tblname + '_log
(
' + @strins + '
)
select ' + @strsel + ' getdate(), ''d''
from Deleted
end'
	print @str
	exec(@str)	
	
	fetch next from cur into @tblname, @tblid     
end   

close cur   
deallocate cur