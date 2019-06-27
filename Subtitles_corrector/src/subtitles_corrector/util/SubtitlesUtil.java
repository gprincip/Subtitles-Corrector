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
import javax.swing.JTextArea;

import org.mozilla.universalchardet.UniversalDetector;

import subtitles_corrector.model.SubtitleUnit;

public class SubtitlesUtil {

	private static final String FROM_TO_SUBTITLES_SEPARATOR = "-->";

	/**
	 * Read subtitles file line by line and return list of lines
	 * 
	 * @param charset of the subtitles file to be used when reading
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
	
	/**
	 * Converts list of strings to list of subtitle units
	 * @param lines
	 * @return
	 */
	public static List<SubtitleUnit> linesOfTextToListOfSubtitleUnits(List<String> lines) {

		// buffer accumulating lines of text that are storing information about one subtitle unit
		List<String> buffer = new ArrayList<String>();
		List<SubtitleUnit> subtitlesUnitsList = new ArrayList<SubtitleUnit>();
		
		int rowIndex = 0;
		
		for (String line : lines) {

			if (StringUtils.isNotBlank(line)) {
				buffer.add(line);
				continue; //add lines to buffer until we come across empty line which indicates the end of the current subtitle on the screen
			}

			Integer subtitleIndex = null;
			try {
				subtitleIndex = Integer.parseInt(buffer.get(0));
			} catch (NumberFormatException e) {
				// TODO: report error?
			}

			String fromAndTo = buffer.get(1);
			
			String text = "";
			for(int i=2; i<buffer.size(); i++) {
				text += (buffer.get(i) + "\n");
			}
			
			String fromAndToSplited[] = fromAndTo.split(FROM_TO_SUBTITLES_SEPARATOR);
			
			String from = "";
			String to = "";
			
			if(fromAndToSplited.length == 2) {
				
				from = fromAndToSplited[0].trim();
				to = fromAndToSplited[1].trim();
				
			}else {
				//TODO: report error?
			}
			
			buffer.clear();
			
			SubtitleUnit subUnit = new SubtitleUnit(rowIndex, subtitleIndex, from, to, text);
			subtitlesUnitsList.add(subUnit);
			rowIndex++;
		}

		return subtitlesUnitsList;
	}

	public static void writeLinesToFile(File file, List<String> correctedFileLines, String charset) {

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

	/** Converts text from text areas to list of subtitle units. </br>
	 * Important - we assume that text areas are in correct order, like in subtitles file, and
	 * row index is set in order as we iterate through text areas */
	public static void textAreasToSubtitleUnits(List<JTextArea> textAreas, List<SubtitleUnit> subtitleUnits) {
		
		if(subtitleUnits.size() > 0) {
			subtitleUnits.clear();
		}
		
		//counter used for counting 4 text areas as 4 TA represent one subtitle unit (one row in grid in editor)
		int TAcounter = 1;
		int rowIndex = 0;
		List<String> currentSubtitleData = new ArrayList<String>(); //text from 4 textAreas
		
		for(JTextArea textArea : textAreas) {
			
			currentSubtitleData.add(textArea.getText());
			
			if(TAcounter != 4) {
				TAcounter ++;
			}else {
				TAcounter = 1;
				
				Integer subtitleIndex = Integer.parseInt(currentSubtitleData.get(0));
				String from = currentSubtitleData.get(1);
				String to  = currentSubtitleData.get(2);
				String text = currentSubtitleData.get(3);
				subtitleUnits.add(new SubtitleUnit(rowIndex, subtitleIndex, from, to, text));
				currentSubtitleData.clear();
				rowIndex++; //increment rowIndex every time new subUnit is created
			}
			
		}
		
	}

	public static boolean validateSubtitles(File subtitlesFile) {
		//TODO: to be implemented!
		return false;
	}
	
	public static void saveSubtitleUnitsToFile(File file, List<SubtitleUnit> subtitleUnits, String charset) {
		
		List<String> subtitlesAsText = new ArrayList<String>();
		
		for(SubtitleUnit subUnit : subtitleUnits) {
			
			StringBuilder subBuilder = new StringBuilder();
			subBuilder.append(subUnit.getSubtitleIndex().toString()).append("\n");
			subBuilder.append(subUnit.getTimeFrom()).append(" --> ").append(subUnit.getTimeTo()).append("\n");
			subBuilder.append(subUnit.getText());
			subtitlesAsText.add(subBuilder.toString());
			
		}
		
		writeLinesToFile(file, subtitlesAsText, charset);
		
	}
	
	/**
	 * Adds subtitleUnit to subtitleUnit list at specified index and updates indexes of other subUnits in the list
	 * 
	 * @param subUnit to be added to the list
	 * @param subtitleUnitList ti which subUnit is added
	 * @param index on which the subUnit is added
	 * @return
	 */
	public static List<SubtitleUnit> addSubtitleUnitToSubtitleUnitList(SubtitleUnit subUnit, List<SubtitleUnit> subtitleUnitList, int index){
		
		subUnit.setIndex(index); //set index just in case
		subtitleUnitList.add(index, subUnit);
		
		for(int i=index + 1; i<subtitleUnitList.size(); i++) {
			
			subtitleUnitList.get(i).setIndex(i+1); //update the index
			
		}
		
		return subtitleUnitList;
	}
	
}











