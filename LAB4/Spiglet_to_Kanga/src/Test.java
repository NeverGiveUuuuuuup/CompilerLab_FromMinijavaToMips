import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Test {

	public Test() {
		
	}

	public static void main(String[] args) {
		List<String> files = new ArrayList<String>();
		
		//notice! must be absolute filePath!
		File file = new File("D:\\codes\\CompilerLab\\LAB4\\Spiglet_to_Kanga\\input");
		
		File[] tempList = file.listFiles();
			
		try {
			Method method = Main.class.getMethod("main", String[].class);
			for (int i = 0; i < tempList.length; i++) {
				if (tempList[i].isFile()) {
					files.add(tempList[i].toString());
					// 文件名，包含路径
					String fileName = tempList[i].getPath();
					if (fileName.endsWith(".spg") == false)
						continue;
					System.out.println("get spiglet file: " + fileName);

					method.invoke(null, (Object) new String[] { fileName });
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
