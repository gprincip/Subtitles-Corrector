package subtitles_corrector.frames;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import subtitles_corrector.util.SubtitlesUtil;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private int width;
	private int height;

	private JLabel choosenFileLabel;
	private JLabel choosenDestinationLabel;
	private JButton chooseSubtitlesFileButton;
	private JButton chooseDestinationFileButton;
	private JButton runButton;
	private JButton openSubtitleButton;
	private JPanel centerPanel;
	private JPanel topPanel;
	private JPanel runPanel;
	private JFileChooser subtitlesFileChooser;
	private JFileChooser destinationFileChooser;
	private File subtitlesFile = null;
	private File destinationFolder = null;
	private String correctedFileCharset; //of the corrected file
	
	public MainFrame(int width, int height) {

		super("Subtitle corrector");

		this.width = width;
		this.height = height;

		setLayout(new BorderLayout());

		initComponents();
		initFields();
		addActionListeners();
		addComponentsToPanelsAndAddPanels();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(width, height);
		setVisible(true);
	}

	public File getSubtitlesFile() {
		return this.subtitlesFile;
	}
	
	private void addComponentsToPanelsAndAddPanels() {
		topPanel.add(chooseSubtitlesFileButton);
		topPanel.add(choosenFileLabel);
		runPanel.add(runButton);
		runPanel.add(openSubtitleButton);
		centerPanel.add(chooseDestinationFileButton);
		centerPanel.add(choosenDestinationLabel);
		
		add(topPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(runPanel, BorderLayout.SOUTH);
	}

	private void initFields() {
		this.correctedFileCharset = "UTF-8";
	}

	private void addActionListeners() {
		chooseSubtitlesFileButton.addActionListener(actionEvent -> {

			subtitlesFileChooser = new JFileChooser();
			int choosedState = subtitlesFileChooser.showOpenDialog(this);

			if (choosedState == JFileChooser.APPROVE_OPTION) {
				this.subtitlesFile = subtitlesFileChooser.getSelectedFile();
				choosenFileLabel.setText(subtitlesFile.getPath());
			}

		});
		
		runButton.addActionListener(actionEvent -> {
			
			boolean error = false;
			
			if(subtitlesFile == null) {
				SubtitlesUtil.showMessage(this, "Select subtitles file first and then click run!");
				error = true;
			}else if(!subtitlesFile.getName().endsWith("srt") &&
					 !subtitlesFile.getName().endsWith("txt") &&
				     !subtitlesFile.getName().endsWith("sub")){
				SubtitlesUtil.showMessage(this, "File can be either in srt, sub or txt format!");
				error = true;
			}
			
			if(destinationFolder == null) {
				SubtitlesUtil.showMessage(this, "Choose destination folder!");
				error = true;
			}
			
			if(!error) {
				processAndSaveSubtitlesFile(subtitlesFile, correctedFileCharset);
			}
			
		});
		
		chooseDestinationFileButton.addActionListener(eventAction -> {
			
			destinationFileChooser = new JFileChooser();
			destinationFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int choosedState = destinationFileChooser.showOpenDialog(this);
			
			if(choosedState == JFileChooser.APPROVE_OPTION) {
				destinationFolder = destinationFileChooser.getSelectedFile();
				choosenDestinationLabel.setText(destinationFolder.getAbsolutePath());
			}
			
		});
		
		openSubtitleButton.addActionListener(eventAction -> {
			
			if(subtitlesFile == null) {
				SubtitlesUtil.showMessage(this, "Select subtitles file!");
			}else if(!subtitlesFile.getName().endsWith("srt") &&
					 !subtitlesFile.getName().endsWith("txt") &&
				     !subtitlesFile.getName().endsWith("sub")){
				SubtitlesUtil.showMessage(this, "File can be either in srt, sub or txt format!");
			}else {
				SubtitleFrame subtitleFrame = new SubtitleFrame(700,700, subtitlesFile);
			}
			
		});
	}

	private void initComponents() {
		choosenFileLabel = new JLabel("filename");
		choosenDestinationLabel = new JLabel("filename");
		chooseSubtitlesFileButton = new JButton("Choose file");
		chooseDestinationFileButton = new JButton("Choose destination"); //where the corrected file will be saved
		runButton = new JButton("Run");
		openSubtitleButton = new JButton("Open subtitle");
		
		topPanel = new JPanel();
		centerPanel = new JPanel();
		runPanel = new JPanel();
	}

	/**
	 * Parse, correct and save new subtitles file	
	 * @param subtitlesFile
	 */
	private void processAndSaveSubtitlesFile(File subtitlesFile, String charset) {

		List<String> correctedFileLines = new ArrayList<String>();
		String subtitlesFileCharset = SubtitlesUtil.detectCharsetOfFile(subtitlesFile);
		List<String> subtitlesLines = SubtitlesUtil.loadSubtitlesIntoList(subtitlesFileCharset, subtitlesFile);
		
		for(String line : subtitlesLines) {
			processLineOfTextAndAddToArray(line, correctedFileLines);
		}
		
		// create new file and write lines of text to it
		String newFilename = "corrected_" + subtitlesFile.getName();
		File newFile = new File(destinationFolder, newFilename);

		SubtitlesUtil.writeLinesToFile(newFile, correctedFileLines, charset);
		SubtitlesUtil.showMessage(this, "File corrected!");

	}

	/**
	 * Replaces characters in single line of text and adds corrected line to array
	 * 
	 * @param line
	 * @param correctedFileLines
	 */
	private void processLineOfTextAndAddToArray(String line, List<String> correctedFileLines) {

		String correctedLine = line.replace("", "ž");
		correctedLine = correctedLine.replace("", "Ž");

		correctedLine = correctedLine.replace("", "š");
		correctedLine = correctedLine.replace("", "Š");

		correctedLine = correctedLine.replace("æ", "ć");
		correctedLine = correctedLine.replace("Æ", "Ć");

		correctedLine = correctedLine.replace("è", "č");
		correctedLine = correctedLine.replace("È", "Č");

		correctedFileLines.add(correctedLine);

	}

}