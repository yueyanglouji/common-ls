package l.s.common.util;

public class StringUtil {

	public static boolean isEmpty(String str) {
		return (str == null || "".equals(str));
	}
	
	public static boolean notEmpty(String str) {
		return !isEmpty(str);
	}
	
	public static boolean emptyOR(String... str) {
		if(str == null || str.length == 0) {
			throw new RuntimeException("isEmptyOr : no String found");
		}
		for(String s : str) {
			if(isEmpty(s)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean hasEmptyStr(String... str) {
		return emptyOR(str);
	}
	
	public static boolean emptyAND(String... str) {
		if(str == null || str.length == 0) {
			throw new RuntimeException("isEmptyOr : no String found");
		}
		for(String s : str) {
			if(notEmpty(s)) {
				return false;
			}
		}
		
		return true;
	}
}
