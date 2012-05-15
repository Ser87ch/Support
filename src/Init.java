import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;


public class Init {
	static boolean isDefault = false;
	static String copyfolder = "a00001";

	public static void mkfolder()
	{
		try {
		File dir = new File(Settings.testProj + "tests\\");

		String[] children = dir.list();
		if (children == null || children.length == 0) {
			Settings.folder = "a00001";
		} else {
			String filename = children[children.length - 1];
			if (filename.substring(1, 6).equals("99999"))
			{
				char s = (char)(filename.charAt(0) +  1);
				Settings.folder = Character.toString(s) + "00001";
			} else {
				int i = Integer.parseInt(filename.substring(1, 6)) + 1;
				DecimalFormat myFormat = new java.text.DecimalFormat("00000");
				Settings.folder = filename.substring(0, 1) + myFormat.format(new Integer(i));
			}
		}
		(new File(Settings.testProj + "tests\\" + Settings.folder)).mkdir();
		(new File(Settings.testProj + "tests\\" + Settings.folder + "\\settings")).mkdir();
		(new File(Settings.testProj + "tests\\" + Settings.folder + "\\input")).mkdir();
		if(isDefault)
		{
			copyFile(Settings.testProj + "default\\gendoc.xml",Settings.testProj + "tests\\" + Settings.folder + "\\settings\\gendoc.xml");
			copyFile(Settings.testProj + "default\\general.xml",Settings.testProj + "tests\\" + Settings.folder + "\\settings\\general.xml");
		} else {
			copyFile(Settings.testProj + "tests\\" + copyfolder + "\\settings\\gendoc.xml",Settings.testProj + "tests\\" + Settings.folder + "\\settings\\gendoc.xml");
			copyFile(Settings.testProj + "tests\\" + copyfolder + "\\settings\\gendoc.xml",Settings.testProj + "tests\\" + Settings.folder + "\\settings\\general.xml");
		}
		} catch(Exception e) {
			e.getStackTrace();
		}
		
	}
	
	public static void copyFile(String sourcestr, String deststr)throws IOException {
		File sourceFile = new File(sourcestr);
		File destFile = new File(deststr);
		destFile.createNewFile();
		FileChannel source = null;
	    FileChannel destination = null;
		
		if(!destFile.exists()) {
	        destFile.createNewFile();
	    }	    

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    } 
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
}
