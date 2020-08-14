import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		List<String> files = new ArrayList<String>();
		//D:\\codes\\CompilerLab\\testcases\\priv\\correct
		//D:\codes\CompilerLab\testcases\pub\correct
		File file = new File("D:\\codes\\CompilerLab\\testcases\\pub\\correct");
		
		File[] tempList = file.listFiles();
			
		try {
			Method method = Main.class.getMethod("main", String[].class);
			for (int i = 0; i < tempList.length; i++) {
				if (tempList[i].isFile()) {
					files.add(tempList[i].toString());
					// 文件名，不包含路径
					String fileName = tempList[i].getPath();
					if (fileName.endsWith(".java") == false)
						continue;
					System.out.println("get javaFile name: " + fileName);

					method.invoke(null, (Object) new String[] { fileName });
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
