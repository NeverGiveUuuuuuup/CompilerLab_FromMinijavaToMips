import java.io.*;
import visitor.*;
import syntaxtree.*;

public class Main {
	static PigletParser parser = null;
	public static void main(String args[]){
		try {
			InputStream in = new FileInputStream(args[0]);
			if(parser == null) {
				parser = new PigletParser(in);
			}
			else {
				PigletParser.ReInit(in);
			}
			Node root = PigletParser.Goal();
			
			String pigletFilePathName = "output/" + getFileName(args[0]) + ".spg";
			TranslateToSpiglet.setFilePath( pigletFilePathName );
			System.out.println("output FilePath is:" + pigletFilePathName);
			
			root.accept(new TranslateToSpiglet());
			
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