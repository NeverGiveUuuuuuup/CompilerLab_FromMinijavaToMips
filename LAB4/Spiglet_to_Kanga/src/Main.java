import java.io.*;
import visitor.*;
import syntaxtree.*;

public class Main {
	static SpigletParser parser = null;
	public static void main(String args[]){
		try {
			InputStream in = new FileInputStream(args[0]);
			if(parser == null) {
				parser = new SpigletParser(in);
			}
			else {
				SpigletParser.ReInit(in);
			}
			Node root = SpigletParser.Goal();
			
			String spigletFilePathName = "output/" + getFileName(args[0]) + ".kg";
			
			SpigletToKanga.setFilePath( spigletFilePathName );
			System.out.println("output FilePath is:" + spigletFilePathName);
			
			root.accept(new SpigletToKanga(), null);
			
			System.out.println("finished");
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (TokenMgrError e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getFileName(String filePath) {
		int a = filePath.lastIndexOf("/");
		a = Math.max(filePath.lastIndexOf("/"), filePath.lastIndexOf("\\"));
		int b =filePath.indexOf(".");
		return filePath.substring(a+1, b);
	}
}