import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
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

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private int width;
	private int height;

	private JLabel choosenFileLabel;
	private JLabel choosenDestinationLabel;
	private JButton chooseSubtitlesFileButton;
	private JButton chooseDestinationFileButton;
	private JButton runButton;
	private JPanel centerPanel;
	private JPanel topPanel;
	private JPanel runPanel;
	private JFileChooser subtitlesFileChooser;
	private JFileChooser destinationFileChooser;
	private File subtitlesFile = null;
	private File destinationFolder = null;
	private Charset charset;
	
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

	private void addComponentsToPanelsAndAddPanels() {
		topPanel.add(chooseSubtitlesFileButton);
		topPanel.add(choosenFileLabel);
		runPanel.add(runButton);
		centerPanel.add(chooseDestinationFileButton);
		centerPanel.add(choosenDestinationLabel);
		
		add(topPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(runPanel, BorderLayout.SOUTH);
	}

	private void initFields() {
		this.charset = StandardCharsets.UTF_8;
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
				showMessage("Select subtitles file first and then click run!");
				error = true;
			}else if(!subtitlesFile.getName().endsWith("srt") &&
					 !subtitlesFile.getName().endsWith("txt") &&
				     !subtitlesFile.getName().endsWith("sub")){
				showMessage("File can be either in srt, sub or txt format!");
				error = true;
			}
			
			if(destinationFolder == null) {
				showMessage("Choose destination folder!");
				error = true;
			}
			
			if(!error) {
				processAndSaveSubtitlesFile(subtitlesFile, charset);
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
		
	}

	private void initComponents() {
		choosenFileLabel = new JLabel("filename");
		choosenDestinationLabel = new JLabel("filename");
		chooseSubtitlesFileButton = new JButton("Choose file");
		chooseDestinationFileButton = new JButton("Choose destination"); //where the corrected file will be saved
		runButton = new JButton("Run");
		
		topPanel = new JPanel();
		centerPanel = new JPanel();
		runPanel = new JPanel();
	}

	private void showMessage(String text) {
		JOptionPane.showMessageDialog(this, text);
	}

	/**
	 * Parse, correct and save new subtitles file	
	 * @param subtitleFile
	 */
	private void processAndSaveSubtitlesFile(File subtitleFile, Charset charset) {

		Scanner scanner = null;
		List<String> correctedFileLines = new ArrayList<String>();
		try {
			scanner = new Scanner(subtitleFile, "ISO_8859_1");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (scanner != null) {

			while (scanner.hasNextLine()) {

				String line = scanner.nextLine();
				processLineOfTextAndAddToArray(line, correctedFileLines);

			}

		}
		
		// create new file and write lines of text to it
		String newFilename = "corrected_" + subtitleFile.getName();
		File newFile = new File(destinationFolder, newFilename);

		writeLinesToCorrectedFile(newFile, correctedFileLines, charset);
		showMessage("File corrected!");

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

	private void writeLinesToCorrectedFile(File newFile, List<String> correctedFileLines, Charset charset) {

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
	}

}