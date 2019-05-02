package subtitles_corrector.util;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.mozilla.universalchardet.UniversalDetector;

public class SubtitlesUtil {

	/**
	 * Read subtitles file line by line and return list of lines
	 * 
	 * @param charset       of the subtitles file to be used when reading
	 * @param subtitlesFile
	 * @return
	 */
	public static List<String> loadSubtitlesIntoList(String charset, File subtitlesFile) {

		Scanner scanner = null;
		List<String> linesList = new ArrayList<String>();
		try {
			scanner = new Scanner(subtitlesFile, charset);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (scanner != null) {

			while (scanner.hasNextLine()) {

				linesList.add(scanner.nextLine());

			}

		}

		return linesList;
	}

	public static void writeLinesToFile(File file, List<String> correctedFileLines, Charset charset) {

		Writer fileWriter = null;
		try {
			fileWriter = new OutputStreamWriter(new FileOutputStream(file), charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String line : correctedFileLines) {
			try {

				fileWriter.append(line + System.lineSeparator());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void showMessage(Component component, String text) {
		JOptionPane.showMessageDialog(component, text);
	}

	/**
	 * Detects the charset of a file using org.mozilla.universalchardet.UniversalDetector
	 * @param file
	 * @return detected charset if found, otherwise null
	 */
	public static String detectCharsetOfFile(File file) {

		UniversalDetector detector = new UniversalDetector(null);
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] buf = new byte[2048];

		if (fis != null) {
			int nread;
			try {
				while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
					detector.handleData(buf, 0, nread);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			detector.dataEnd();
			String charset = detector.getDetectedCharset();
			detector.reset();
			return charset;
		}
		return null;

	}

}
