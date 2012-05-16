import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

	static BufferedWriter out;
	public static void create()
	{
		try {
			FileWriter fstream = new FileWriter(Settings.testProj + "tests\\" + Settings.folder + "\\log.txt",true);
			out = new BufferedWriter(fstream);	
			msg("���� ���� " + Settings.testProj + "tests\\" + Settings.folder + "\\log.txt" + " ������.");
		} catch(Exception e) {
			e.printStackTrace();
			msg(e);
		}
	}	

	public static void close()
	{
		if(out != null)
		{
			try {
				msg("���� ���� " + Settings.testProj + "tests\\" + Settings.folder + "\\log.txt" + " ������.");
				out.write("----------------------------------------------------------------------------------------------------------------------------------------------------");
				out.newLine();
				out.newLine();
				out.close();
			} catch(Exception e) {
				e.printStackTrace();
				msg(e);
			}
		}
	}	

	public static void msg(String str)
	{
		if (out != null)
		{
			try {
				out.write("[" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS").format(new Date())+ "] msg: " + str);
				out.newLine();
				out.flush();
			} catch(Exception e) {
				e.printStackTrace();
				msg(e);
			}
		}
	}
	public static void msg(Exception e)
	{
		if (out != null)
		{
			try {
				out.write("[" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS").format(new Date())+ "] exception: " + e.getMessage());
				out.newLine();
				out.flush();
			} catch(Exception ex) {
				ex.printStackTrace();
				msg(e);
			}
		}
	}
}
