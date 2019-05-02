package subtitles_corrector.frames;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import subtitles_corrector.util.SubtitlesUtil;

public class SubtitleFrame extends JFrame{

	private static final String SAVE_BUTTON_LABEL = "Save subtitles";

	private static final long serialVersionUID = 1L;
	
	private MainFrame mainFrame;
	private int width;
	private int height;
	private JPanel subtitlesPanel;
	private JPanel bottomPanel;
	private JScrollPane scrollPane;
	private List<JTextField> subtitlesTextFields; // used for displaying subtitles line by line
	private JLabel detectedCharsetTextField;
	private File subtitlesFile;
	private List<String> subtitlesLines;
	private JButton saveButton;
	private JFileChooser saveFileChooser;
	private String subtitlesFileCharset;
	
	public SubtitleFrame(int width, int height, File subtitlesFile) {
		
		super();
		
		this.mainFrame = mainFrame;
		
		this.width = width;
		this.height = height;
		this.subtitlesFile = subtitlesFile;
		
		setTitle(subtitlesFile.getAbsolutePath());
		
		initComponents();
		initFields();
		addActionListeners();
		addComponentsToPanelsAndAddPanels();

		setSize(width, height);
		setVisible(true);
		
	}


	private void initFields() {
		subtitlesFileCharset = SubtitlesUtil.detectCharsetOfFile(subtitlesFile);
		detectedCharsetTextField.setText("Detected charset: " + subtitlesFileCharset);
		subtitlesLines = SubtitlesUtil.loadSubtitlesIntoList(subtitlesFileCharset, subtitlesFile);
		subtitlesPanel.setLayout(new GridLayout(subtitlesLines.size(), 1));
		bottomPanel.setLayout(new FlowLayout());
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);
	}


	private void addActionListeners() {
		
		saveButton.addActionListener(eventAction -> {
			
			List<String> lines = new ArrayList<String>();
			subtitlesTextFields.stream().forEach(textField -> {
				lines.add(textField.getText());
			});
			
			int chooserState = saveFileChooser.showOpenDialog(this);
			
			File destinationFolder = null;
			
			if(chooserState == JFileChooser.APPROVE_OPTION) {
				destinationFolder = saveFileChooser.getSelectedFile();
				File newFile = new File(destinationFolder, "saved_" + subtitlesFile.getName());
				SubtitlesUtil.writeLinesToFile(newFile, lines, StandardCharsets.UTF_8);
				SubtitlesUtil.showMessage(this, "File saved!");				
			}
			
		});
		
	}

	private void initComponents() {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		subtitlesPanel = new JPanel();
		bottomPanel = new JPanel();
		scrollPane = new JScrollPane(subtitlesPanel);
		subtitlesTextFields = new ArrayList<JTextField>();
		detectedCharsetTextField = new JLabel();
		saveButton = new JButton(SAVE_BUTTON_LABEL);
		saveFileChooser = new JFileChooser();
		saveFileChooser.setFileSelectionMode(saveFileChooser.DIRECTORIES_ONLY);
	}
	
	private void addComponentsToPanelsAndAddPanels() {
		
		for (int i = 0; i < subtitlesLines.size(); i++) {

			JTextField newTextField = new JTextField();

			// add stripes with different colors
			if (i % 2 == 0) {
				newTextField.setBackground(new Color(215, 230, 255));
			} else {
				newTextField.setBackground(new Color(230, 240, 255));
			}

			subtitlesTextFields.add(newTextField);
			subtitlesPanel.add(newTextField);
			newTextField.setText(subtitlesLines.get(i));
		}

		add(scrollPane);
		bottomPanel.add(saveButton);
		bottomPanel.add(detectedCharsetTextField);
		add(bottomPanel);

	}
	

}

