import java.io.*;
import errorPrinter.*;
import visitor.*;
import syntaxtree.*;
import symbol.*;
/**
 * @author hanjiaheng 1600016618
 * input is FilePath, like: D:\codes\CompilerLab\testcases\priv\correct\test27.java
 * both abusolute and related filePath are ok.
 */

public class Main {
	static MiniJavaParser parser = null;
	public static void main(String args[]){
		try {
			InputStream in = new FileInputStream(args[0]);
			if(parser == null) {
				parser = new MiniJavaParser(in);
			}
			else {
				MiniJavaParser.ReInit(in);
			}
			Node root = MiniJavaParser.Goal();	
			MType allClassList = new MClassList();
			
			//1.建立符号表
			root.accept(new BuildSymbolTableVisitor(), allClassList);
			//2.类型检查
			root.accept(new TypeCheckVisitor(), allClassList);
			System.out.println("NO PROBLEM");
			
			//3.翻译成pigletCode
			String pigletFilePathName = "pg/" + getFileName(args[0]) + ".pg";
			TranslateToPiglet.FilePath = pigletFilePathName;
			System.out.println("output FilePath is:" + pigletFilePathName);
			
			root.accept(new TranslateToPiglet(), allClassList);
			System.out.println("Translate To Piglet finished");
			
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (TokenMgrError e) {
			e.printStackTrace();
		} catch(FoundErrorException e) {
			System.out.println("ERROR FOUND");
			System.out.println(e.getMessage());
		} catch (Exception e) {
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
