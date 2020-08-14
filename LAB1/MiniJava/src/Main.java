import java.io.*;
import errorPrinter.*;
import visitor.*;
import syntaxtree.*;
import symbol.*;

public class Main {
	public static void main(String args[]){
		try {
			InputStream in = new FileInputStream(args[0]);
			new MiniJavaParser(in);
			Node root = MiniJavaParser.Goal();	
			MType allClassList = new MClassList();
			
			//1.建立符号表
			root.accept(new BuildSymbolTableVisitor(), allClassList);
			//2.类型检查
			root.accept(new TypeCheckVisitor(), allClassList);
			System.out.println("NO PROBLEM");
			
			//3.翻译成pigletCode
			TranslateToPiglet.FilePath = "p.pg";
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
}