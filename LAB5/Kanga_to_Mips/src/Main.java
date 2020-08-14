import java.io.*;
import visitor.*;
import syntaxtree.*;

public class Main {
	static KangaParser parser = null;
	public static void main(String args[]){
		try {
			InputStream in = new FileInputStream(args[0]);
			if(parser == null) {
				parser = new KangaParser(in);
			}
			else {
				KangaParser.ReInit(in);
			}
			Node root = KangaParser.Goal();
			
			String mipsFilePathName = "output/" + getFileName(args[0]) + ".s";
			
			KangaToMips.setFilePath( mipsFilePathName );
			System.out.println("output FilePath is:" + mipsFilePathName);
			
			root.accept(new KangaToMips(), null);
			
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