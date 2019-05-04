package subtitles_corrector.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import subtitles_corrector.model.SubtitleUnit;
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
	private List<JPanel> subtitleUnitPanels; //one row in the grid - representing one subtitle unit
	private List<JTextArea> textAreas; //text areas from the GUI - used to edit subtitles, and to read from them so subtitle could be saved to a file
	private List<SubtitleUnit> subtitleUnits;
	private JLabel detectedCharsetTextField;
	private File subtitlesFile;
	private List<String> subtitlesLines;
	private JButton saveButton;
	private JFileChooser saveFileChooser;
	private String subtitlesFileCharset;
	
	public SubtitleFrame(int width, int height, File subtitlesFile) {
		
		super();
		
		this.width = width;
		this.height = height;
		this.subtitlesFile = subtitlesFile;
		
		setTitle(subtitlesFile.getAbsolutePath());
		
		initComponents();
		initFields();
		addActionListeners();
		addPanels();

		setSize(width, height);
		setVisible(true);
		
	}


	private void initFields() {
		subtitlesFileCharset = SubtitlesUtil.detectCharsetOfFile(subtitlesFile);
		detectedCharsetTextField.setText("Detected charset: " + subtitlesFileCharset);
		subtitlesLines = SubtitlesUtil.loadSubtitlesIntoList(subtitlesFileCharset, subtitlesFile);
		subtitleUnits = SubtitlesUtil.linesOfTextToListOfSubtitleUnits(subtitlesLines);
		subtitlesPanel.setLayout(new GridLayout(subtitleUnits.size() + 1, 1)); //+1 for heading row
		displaySubtitles(subtitleUnits);
		bottomPanel.setLayout(new FlowLayout());
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);
	}


	private void addActionListeners() {
		
		saveButton.addActionListener(eventAction -> {
			
			List<String> lines = new ArrayList<String>();
			
			int chooserState = saveFileChooser.showOpenDialog(this);
			
			File destinationFolder = null;
			
			if(chooserState == JFileChooser.APPROVE_OPTION) {
				destinationFolder = saveFileChooser.getSelectedFile();
				File newFile = new File(destinationFolder, "saved_" + subtitlesFile.getName());
				
				//save current text in text areas to this.subtitleUnits so it can then be saved to a file if needed
				SubtitlesUtil.textAreasToSubtitleUnits(textAreas, subtitleUnits);
				SubtitlesUtil.saveSubtitleUnitsToFile(newFile, subtitleUnits, "UTF-8");

				SubtitlesUtil.showMessage(this, "File saved!");				
			}
			
		});
		
	}

	private void initComponents() {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		subtitlesPanel = new JPanel();
		bottomPanel = new JPanel();
		subtitleUnitPanels = new ArrayList<JPanel>();
		scrollPane = new JScrollPane(subtitlesPanel);
		detectedCharsetTextField = new JLabel();
		saveButton = new JButton(SAVE_BUTTON_LABEL);
		saveFileChooser = new JFileChooser();
		saveFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		textAreas = new ArrayList<JTextArea>();
	}
	
	private void addPanels() {

		add(scrollPane);
		bottomPanel.add(saveButton);
		bottomPanel.add(detectedCharsetTextField);
		add(bottomPanel);

	}
	
	/**
	 * Logic needed to show subtitles on GUI from list of subtitle units
	 * @param subtitleUnits
	 */
	private void displaySubtitles(List<SubtitleUnit> subtitleUnits) {
		
		//TODO: fix component sizes not to be hardcoded
		int panelWidth = 18;
		int panelHeight = 18;
		int textPanelHeight = 36;
		int headingRowHeight = 25;
		
		JLabel indexLabel = new JLabel("No.");
		JLabel startLabel = new JLabel("start");
		JLabel endLabel = new JLabel("end");
		JLabel textLabel = new JLabel("text");
		
		indexLabel.setPreferredSize(new Dimension(40, headingRowHeight));
		startLabel.setPreferredSize(new Dimension(100, headingRowHeight));
		endLabel.setPreferredSize(new Dimension(100, headingRowHeight));
		textLabel.setPreferredSize(new Dimension(100, headingRowHeight));
		
		JPanel indexHeadingPanel = new JPanel();
		indexHeadingPanel.setPreferredSize(new Dimension(40, headingRowHeight));
		indexHeadingPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); indexHeadingPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.BLACK));
		indexHeadingPanel.add(indexLabel);
		indexHeadingPanel.setBackground(new Color(20, 170, 220));
		
		JPanel startHeadingPanel = new JPanel(); startHeadingPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
		startHeadingPanel.setPreferredSize(new Dimension(100, headingRowHeight)); 
		startHeadingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		startHeadingPanel.add(startLabel);
		startHeadingPanel.setBackground(new Color(20, 170, 220));
		
		JPanel endHeadingPanel = new JPanel(); 
		endHeadingPanel.setPreferredSize(new Dimension(100, headingRowHeight)); 
		endHeadingPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); endHeadingPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
		endHeadingPanel.add(endLabel);
		endHeadingPanel.setBackground(new Color(20, 170, 220));
		
		JPanel textHeadingPanel = new JPanel(); 
		textHeadingPanel.setPreferredSize(new Dimension(400, headingRowHeight)); 
		textHeadingPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); textHeadingPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
		textHeadingPanel.add(textLabel);
		textHeadingPanel.setBackground(new Color(20, 170, 220));
		
		JPanel headingRow = new JPanel();
		headingRow.setLayout(new BoxLayout(headingRow, BoxLayout.X_AXIS));
		headingRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		
		headingRow.add(indexHeadingPanel);
		headingRow.add(startHeadingPanel);
		headingRow.add(endHeadingPanel);
		headingRow.add(textHeadingPanel);
		subtitlesPanel.add(headingRow);

		int colorCounter = 0;
		Color evenColor = new Color(215, 230, 255);
		Color oddColor = new Color(230, 240, 255);
		
		for(SubtitleUnit subUnit : subtitleUnits){
			
			//create stripes
			Color currentRowColor = null;
			
			if(colorCounter == 0) {
				currentRowColor = evenColor;
				colorCounter++;
			}else {
				currentRowColor = oddColor;
				colorCounter = 0;
			}
			
			//make 4 textfields representing data for one subtitle unit
			//create panel for each component so size can be manualy adjusted using setPreferedSize
			
			JTextArea index = new JTextArea();
			textAreas.add(index);
			index.setPreferredSize(new Dimension(40, panelHeight));
			JPanel indexPanel = new JPanel();
			indexPanel.add(index); 
			indexPanel.setPreferredSize(new Dimension(40, panelHeight));
			indexPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			indexPanel.setBackground(currentRowColor);
			indexPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.BLACK));
			
			JTextArea from = new JTextArea(); 
			textAreas.add(from);
			from.setPreferredSize(new Dimension(100, panelHeight));
			JPanel fromPanel = new JPanel(); 
			fromPanel.add(from); 
			fromPanel.setPreferredSize(new Dimension(100, panelHeight));
			fromPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			fromPanel.setBackground(currentRowColor);
			fromPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
			
			JTextArea to = new JTextArea();
			textAreas.add(to);
			to.setPreferredSize(new Dimension(100, panelHeight));
			JPanel toPanel = new JPanel(); 
			toPanel.add(to); 
			toPanel.setPreferredSize(new Dimension(100, panelHeight));
			toPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			toPanel.setBackground(currentRowColor);
			toPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
			
			JTextArea text = new JTextArea();
			textAreas.add(text);
			text.setPreferredSize(new Dimension(400, textPanelHeight));
			JPanel textPanel = new JPanel(); 
			textPanel.add(text); 
			textPanel.setPreferredSize(new Dimension(400, textPanelHeight));
			textPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			textPanel.setBackground(currentRowColor);
			textPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
			
			index.setText(subUnit.getIndex().toString());
			from.setText(subUnit.getTimeFrom());
			to.setText(subUnit.getTimeTo());
			text.setText(subUnit.getText());
			
			JPanel oneRow = new JPanel();
			oneRow.setLayout(new BoxLayout(oneRow, BoxLayout.X_AXIS));
			oneRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
			
			oneRow.add(indexPanel);
			oneRow.add(fromPanel);
			oneRow.add(toPanel);
			oneRow.add(textPanel);
			//oneRow.setBackground(new Color(215, 230, 255));
			
			subtitleUnitPanels.add(oneRow);
			subtitlesPanel.add(oneRow);
			
		};
				
	}

}

