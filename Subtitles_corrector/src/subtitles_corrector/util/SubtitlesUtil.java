package subtitles_corrector.util;

import java.awt.Component;
import java.io.File;
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

public class SubtitlesUtil {

	/**
	 * Read subtitles file line by line and return list of lines
	 * @param charset of the subtitles file to be used when reading
	 * @param subtitlesFile
	 * @return
	 */
	public static List<String> loadSubtitlesIntoList(Charset charset, File subtitlesFile){
		
		Scanner scanner = null;
		List<String> linesList = new ArrayList<String>();
		try {
			scanner = new Scanner(subtitlesFile, charset.toString());
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
	
	public static void writeLinesToFile(File newFile, List<String> correctedFileLines, Charset charset) {

		Writer fileWriter = null;
		try {
			fileWriter = new OutputStreamWriter(new FileOutputStream(newFile), charset);
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

	
}
