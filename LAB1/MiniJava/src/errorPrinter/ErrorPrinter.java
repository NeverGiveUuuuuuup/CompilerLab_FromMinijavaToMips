package errorPrinter;

public class ErrorPrinter {
	public static boolean addErrorMsg(String msg) {
		throw new FoundErrorException(msg);
	}	
}
