package subtitles_corrector.util;

public class StringUtils {

	/**
	 * Checks if string is not null or not blank (empty)
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str) {
		
		if(str != null) {
			if(str.length() > 0) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isBlank(String str) {
		return !isNotBlank(str);
	}
	
}
