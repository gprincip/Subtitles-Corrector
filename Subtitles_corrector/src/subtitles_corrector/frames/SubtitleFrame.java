package subtitles_corrector.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import subtitles_corrector.model.ButtonWrapper;
import subtitles_corrector.model.SubtitleUnit;
import subtitles_corrector.threads.DelayedActionThread;
import subtitles_corrector.util.SubtitlesUtil;

public class SubtitleFrame extends JFrame{

	private static final String SAVE_BUTTON_LABEL = "Save subtitles";
	/**
	 * Color used for stripes in subtitles panel */
	private static final Color evenColor = new Color(215, 230, 255);
	/**
	 * Color used for stripes in subtitles panel */
	private static final Color oddColor = new Color(235, 245, 255);
	
	private static final Color headingRowColor = new Color(20, 170, 220);
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	private static final long serialVersionUID = 1L;
	
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
	private List<ButtonWrapper> addButtons;
	
	public SubtitleFrame(int width, int height, File subtitlesFile) {
		
		super();
		
		this.width = width;
		this.height = height;
		this.subtitlesFile = subtitlesFile;
		
		setTitle(subtitlesFile.getAbsolutePath());
		
		initComponents();
		initFields();
		addActionListeners();
		displaySubtitles(subtitleUnits);
		addPanels();
		
		setVisible(true);
		setSize(width, height);
		
		waitAndSetScrollbars();
	}


	private void waitAndSetScrollbars() {
		DelayedActionThread scrollbarSetter = new DelayedActionThread(scrollPane);
		Thread t1 = new Thread(scrollbarSetter);
		t1.start();
	}


	private void initFields() {
		subtitlesFileCharset = SubtitlesUtil.detectCharsetOfFile(subtitlesFile);
		detectedCharsetTextField.setText("Detected charset: " + subtitlesFileCharset);
		subtitlesLines = SubtitlesUtil.loadSubtitlesIntoList(subtitlesFileCharset, subtitlesFile);
		subtitleUnits = SubtitlesUtil.linesOfTextToListOfSubtitleUnits(subtitlesLines);
		//subtitlesPanel.setLayout(new GridLayout(subtitleUnits.size() + 1, 1)); //+1 for heading row
		subtitlesPanel.setLayout(new BoxLayout(subtitlesPanel, BoxLayout.Y_AXIS));
		bottomPanel.setLayout(new FlowLayout());
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);
		addButtons = new ArrayList<ButtonWrapper>();
	}


	private void addActionListeners() {
		
		saveButton.addActionListener(eventAction -> {
						
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
		
		/*
		 * two main panels - subtitles panel and bottom panel
		 * bottom panel - save button and detect charset textfield
		 * subtitles panel - grid layout with one column and subtitleUnits.size() + 1 rows
		 * every row has box layout and consists of 4 panels every panel has one attribute
		 * from subtitle unit (index, from, to, text)
		 * */
		
		int indexPanelWidth = 40;
		int startPanelWidth = 100;
		int endPanelWidth = 100;
		int textPanelWidth = 300;
		
		int addButtonWidth = 45;
		int addButtonHeight = 20;
		
		int characterHeight = 18; //TODO hight of one character. Make this dinamic, so text areas expand if there are more then one line of code
		//int textPanelHeight = 36;
		int headingRowHeight = 25;
		
		int marginSize = 10; //make panels wider then the components inside them so we don't get visual glitches
		
		JPanel headingRow = new JPanel();
		headingRow.setLayout(new BoxLayout(headingRow, BoxLayout.X_AXIS));
		headingRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		
		setupHeadingRow(headingRow, indexPanelWidth, startPanelWidth, endPanelWidth, textPanelWidth, addButtonWidth, addButtonHeight, marginSize, headingRowHeight);
				
		subtitlesPanel.add(headingRow);
		
		int colorCounter = 0;
		int addButtonRowIndex = 0;
		for(SubtitleUnit subUnit : subtitleUnits){
						
			int panelHeight = characterHeight * (subUnit.getText().split(NEW_LINE_SEPARATOR).length);
			
			//create stripes
			Color currentRowColor = null;
			
			if(colorCounter == 0) {
				currentRowColor = evenColor;
				colorCounter++;
			}else {
				currentRowColor = oddColor;
				colorCounter = 0;
			}
			
			JPanel oneRow = new JPanel();
			oneRow.setLayout(new BoxLayout(oneRow, BoxLayout.X_AXIS));
			//oneRow.setLayout(new FlowLayout(FlowLayout.LEFT));
			oneRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
			
			//make 4 textfields representing data for one subtitle unit
			//create panel for each component so size can be manualy adjusted using setPreferedSize
			
			setupRows(oneRow, indexPanelWidth, startPanelWidth, endPanelWidth, textPanelWidth, addButtonWidth, addButtonHeight, marginSize, headingRowHeight);
			
			JTextArea index = new JTextArea();			
			textAreas.add(index);
			index.setPreferredSize(new Dimension(indexPanelWidth, characterHeight));
			JPanel indexPanel = new JPanel();
			indexPanel.add(index); 
			indexPanel.setPreferredSize(new Dimension(indexPanelWidth + marginSize, characterHeight + marginSize));
			indexPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			indexPanel.setBackground(currentRowColor);
			indexPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.BLACK));
			
			JTextArea from = new JTextArea(); 
			textAreas.add(from);
			from.setPreferredSize(new Dimension(startPanelWidth, characterHeight));
			JPanel fromPanel = new JPanel(); 
			fromPanel.add(from); 
			fromPanel.setPreferredSize(new Dimension(startPanelWidth + marginSize, characterHeight + marginSize));
			fromPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			fromPanel.setBackground(currentRowColor);
			fromPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
			
			JTextArea to = new JTextArea();
			textAreas.add(to);
			to.setPreferredSize(new Dimension(endPanelWidth, characterHeight));
			JPanel toPanel = new JPanel(); 
			toPanel.add(to); 
			toPanel.setPreferredSize(new Dimension(endPanelWidth + marginSize, characterHeight + marginSize));
			toPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			toPanel.setBackground(currentRowColor);
			toPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
			
			JTextArea text = new JTextArea();
			textAreas.add(text);
			text.setPreferredSize(new Dimension(textPanelWidth, panelHeight));
			JPanel textPanel = new JPanel(); 
			textPanel.add(text); 
			textPanel.setPreferredSize(new Dimension(textPanelWidth + marginSize, panelHeight + marginSize));
			textPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			textPanel.setBackground(currentRowColor);
			textPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));

			setSubtitleTextActionListeners(text, textPanel);
			
			index.setText(subUnit.getSubtitleIndex().toString());
			from.setText(subUnit.getTimeFrom());
			to.setText(subUnit.getTimeTo());
			text.setText(subUnit.getText());
			
			
            
			oneRow.add(indexPanel);
			oneRow.add(fromPanel);
			oneRow.add(toPanel);
			oneRow.add(textPanel);
			
			JPanel addButtonPanel = new JPanel();
			JButton addButton = new JButton("+");
			addButton.setPreferredSize(new Dimension(addButtonWidth,addButtonHeight));
			addButtonPanel.add(addButton);
			oneRow.add(addButtonPanel);
			addButtons.add(new ButtonWrapper(addButton, addButtonRowIndex));
			addButtonRowIndex++;
			
			subtitleUnitPanels.add(oneRow);
			subtitlesPanel.add(oneRow);
			
		};
		
	}

	private void setupHeadingRow(JPanel headingRow, int indexPanelWidth, int startPanelWidth, int endPanelWidth, int textPanelWidth, int addButtonWidth, int addButtonHeight, int marginSize, int headingRowHeight) {
	
		JLabel indexLabel = new JLabel("No.");
		JLabel startLabel = new JLabel("start");
		JLabel endLabel = new JLabel("end");
		JLabel textLabel = new JLabel("text");
		JButton addButon = new JButton("+");
		
		JPanel indexHeadingPanel = new JPanel();
		Border indexBorder = BorderFactory.createMatteBorder(0, 1, 0, 1, Color.BLACK);
		setupHeadingRowElement(indexHeadingPanel, indexBorder, indexLabel, headingRowColor, new FlowLayout(FlowLayout.LEFT), indexPanelWidth + marginSize, headingRowHeight);
		headingRow.add(indexHeadingPanel);
		
		JPanel startHeadingPanel = new JPanel();
		Border startBorder = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK);
		setupHeadingRowElement(startHeadingPanel, startBorder, startLabel, headingRowColor, new FlowLayout(FlowLayout.LEFT), startPanelWidth + marginSize, headingRowHeight);
		headingRow.add(startHeadingPanel);
		
		JPanel endHeadingPanel = new JPanel();
		Border endBorder = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK);
		setupHeadingRowElement(endHeadingPanel, endBorder, endLabel, headingRowColor, new FlowLayout(FlowLayout.LEFT), endPanelWidth + marginSize, headingRowHeight);
		headingRow.add(endHeadingPanel);
		
		JPanel textHeadingPanel = new JPanel(); 
		Border textBorder = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK);
		setupHeadingRowElement(textHeadingPanel, textBorder, textLabel, headingRowColor, new FlowLayout(FlowLayout.LEFT), textPanelWidth + marginSize, headingRowHeight);
		headingRow.add(textHeadingPanel);
		
		//customized settings for button 
		//TODO: change this so button is set using setupHeadingRowElement
		JPanel addButtonPanelHeading = new JPanel();
		addButon.setPreferredSize(new Dimension(addButtonWidth,addButtonHeight));
		addButtonPanelHeading.add(addButon);
		headingRow.add(addButtonPanelHeading);
		
		ButtonWrapper headingAddButtonWrapper = new ButtonWrapper(addButon, 0); //index of heading row is 0
		addButtons.add(headingAddButtonWrapper);
		addActionListener(headingAddButtonWrapper);
		
	}

	private void setupHeadingRowElement(JPanel panel, Border border, JComponent component, Color color, LayoutManager layout, int panelWidth, int panelHeight){
		
		panel.add(component);
		panel.setPreferredSize(new Dimension(panelWidth, panelHeight));
		if(layout != null) {
			panel.setLayout(layout);
		}
		panel.setBackground(color);
		panel.setBorder(border);
	}

	private void setupRows(JPanel oneRow, int indexPanelWidth, int startPanelWidth, int endPanelWidth,
			int textPanelWidth, int addButtonWidth, int addButtonHeight, int marginSize, int headingRowHeight) {
		
		JTextArea index = new JTextArea();			
		JTextArea from = new JTextArea(); 
		JTextArea to = new JTextArea();
		JTextArea text = new JTextArea();
		

	}

	
	private void addActionListener(ButtonWrapper headingAddButtonWrapper) {
		// TODO not implemented yet
		
	}
	
	private void setSubtitleTextActionListeners(JTextArea text, JPanel textPanel) {
		
		text.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {

			}
			
			@Override
			public void keyReleased(KeyEvent e) {

			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				//key 10 - enter
				if(e.getKeyCode() == 10) {
					
					Dimension textPanelDimension = textPanel.getPreferredSize();
					textPanel.setPreferredSize(new Dimension(textPanelDimension.width, textPanelDimension.height + 18));
					
					Dimension textDimension = text.getPreferredSize();
					text.setPreferredSize(new Dimension(textDimension.width, textDimension.height + 18));
					
				//key 8 - backspace
				} else if (e.getKeyCode() == 8) {
					//allow vertical resize only if panel is more then numberOfLines * characterSize tall and not smaller then characterSize
					int numberOfLines = text.getText().split("\n").length;
					int condition = numberOfLines > 1 ? numberOfLines * 30 : 30;
					if (textPanel.getHeight() > condition) { //TODO: ajust this value to be exactly one char tall

						Dimension textPanelDimension = textPanel.getPreferredSize();
						textPanel.setPreferredSize(new Dimension(textPanelDimension.width, textPanelDimension.height - 18));
						
						Dimension textDimension = text.getPreferredSize();
						text.setPreferredSize(new Dimension(textDimension.width, textDimension.height - 18));
					}
				}
			}
			
		});

		
	}
}














