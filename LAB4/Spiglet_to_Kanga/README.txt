作者：韩佳衡（学号1600016618）
功能：将spiglet代码转换成Kanga代码
使用方法：
（1）：
	直接执行Main.java，命令行参数设置为：待转换的spiglet文件路径名，如“input/1.spg”。
	适合单个代码的转换。
（2）：
	直接执行Test.java，不需要命令行参数。
	适合将文件夹内的所有spiglet文件进行批量转换。
	但是需要提前设置好待转换的spiglet文件所在的文件夹的绝对路径名，如“D:\codes\CompilerLab\LAB4\Spiglet_to_Kanga\input”。
实现说明：
（1）自测是可以通过ucla的1-basic.spg, 2-call.spg, Factorial.spg文件测试
（2）自测无法通过ucla的BinaryTree.spg, QuickSort.spg文件测试，
	原因是会出现“Exceeded maximum number of instructions allowed (1000000), exiting...”
其他：
本次作业我试图去除内层循环语句中的ASTORE和ALOAD语句，但是失败了。这可能是出现上述过多冗余命令的原因。
