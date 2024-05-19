package fr.wtx;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.github.weisj.darklaf.LafManager;

import net.miginfocom.swing.MigLayout;


public class MainWindow extends JFrame {
	
	private JTextField ffmpegPathTextField = new JTextField("");
	private JTextField ffprobePathTextField = new JTextField("");
	private JTextField modelPathTextField = new JTextField("");
	private JTextField audioOrVideoPathTextField = new JTextField("");
	private JTextField tempPathTextField = new JTextField("");
	
	private JComboBox languagesComboBox;
	
	public MainWindow() {
		LafManager.install();
	    this.setTitle("");
	    this.setSize(600, 800);
	    this.setLocationRelativeTo(null);

	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    JPanel panel=new JPanel(new MigLayout());

	    this.setContentPane(panel);

	    // FFmpeg options
	    JLayeredPane ffmpegPanel = new JLayeredPane();
	    ffmpegPanel.setEnabled(false);
	    ffmpegPanel.setLayout(new MigLayout());
	    ffmpegPanel.setBorder(BorderFactory.createTitledBorder("FFmpeg options"));
	    panel.add(ffmpegPanel, "width 100%, wrap");
	    ffmpegPanel.add(new JLabel("FFmpeg executable path:"), "wrap");
	    ffmpegPanel.add(ffmpegPathTextField, "width 100%");
	    ffmpegPanel.add(createPathSelectorButton(ffmpegPathTextField), " wrap");
	    
	    ffmpegPanel.add(new JLabel("FFprobe executable path:"), "wrap");
	    ffmpegPanel.add(ffprobePathTextField, "width 100%");
	    ffmpegPanel.add(createPathSelectorButton(ffprobePathTextField), " wrap");

	    
	    // Whisper.cpp options
	    JLayeredPane whisperPanel = new JLayeredPane();
	    whisperPanel.setEnabled(false);
	    whisperPanel.setLayout(new MigLayout(""));
	    whisperPanel.setBorder(BorderFactory.createTitledBorder("Whisper.cpp options"));
	    panel.add(whisperPanel, "width 100%");
	    whisperPanel.add(new JLabel("Model path:"), "wrap");
	    whisperPanel.add(modelPathTextField, "span 3, width 100%");
	    whisperPanel.add(createPathSelectorButton(modelPathTextField), " wrap");
	    
	    // list found here, not so sure https://github.com/openai/whisper/blob/main/whisper/tokenizer.py#L11, I just know for sure fr=French and en=English
	    String languages[] = { " ", "en", "zh", "de", "es", "ru", "ko", "fr", "ja", "pt", "tr", "pl", "ca", "nl", "ar", "sv", "it", "id", "hi", 
	    		"fi", "vi", "he", "uk", "el", "ms", "cs", "ro", "da", "hu", "ta", "no", "th", "ur", "hr", "bg", "lt", "la", "mi", "ml", "cy", 
	    		"sk", "te", "fa", "lv", "bn", "sr", "az", "sl", "kn", "et", "mk", "br", "eu", "is", "hy", "ne", "mn", "bs", "kk", "sq", "sw", 
	    		"gl", "mr", "pa", "si", "km", "sn", "yo", "so", "af", "oc", "ka", "be", "tg", "sd", "gu", "am", "yi", "lo", "uz", "fo", "ht", 
	    		"ps", "tk", "nn", "mt", "sa", "lb", "my", "bo", "tl", "mg", "as", "tt", "haw", "ln", "ha", "ba", "jw", "su", "yue" };
	    Arrays.sort(languages);
	    
	    
	    languagesComboBox = new JComboBox<String>(languages);
	    whisperPanel.add(new JLabel("Language (optional):"));
	    whisperPanel.add(languagesComboBox, "");
	    whisperPanel.add(new JCheckBox("Try to translate in English"), "align left");

	    // Extraction options
	    JLayeredPane runPanel = new JLayeredPane();
	    runPanel.setBorder(BorderFactory.createTitledBorder("Extraction"));
	    runPanel.setLayout(new MigLayout(""));
	    panel.add(runPanel, "width 100%, height 100%, newline");
	    runPanel.add(new JLabel("Audio/Video file to extract text from:"), "wrap");
	    runPanel.add(audioOrVideoPathTextField, "split2, width 100%");
	    runPanel.add(createPathSelectorButton(audioOrVideoPathTextField), " wrap");
	    runPanel.add(new JLabel("Temporary output directory:"), "wrap");
	    runPanel.add(tempPathTextField, "split2, width 100%");
		JButton loadOutputTempDirectoryButton = new JButton("...");
		loadOutputTempDirectoryButton.addActionListener(directorySelection(tempPathTextField));
	    runPanel.add(loadOutputTempDirectoryButton, " wrap");
	    JButton extractTextButton = new JButton("Extract text!");
	    
	    extractTextButton.addActionListener((actionEvent) -> launchExtraction());
	    
	    runPanel.add(extractTextButton, "width 100%, wrap");
	    
	    JTextArea textArea = new JTextArea("");
	    JScrollPane textAreaScrollPanel = new JScrollPane(textArea);
	    textAreaScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    textArea.setWrapStyleWord(true);
	    textArea.setLineWrap(true);
	    runPanel.add(textAreaScrollPanel, "width 100%, height 100%, wrap");

	    this.setVisible(true);

	}

	private JButton createPathSelectorButton(JTextField jTextField) {
		JButton loadFfmpegButton = new JButton("...");
	    loadFfmpegButton.addActionListener(pathSelection(jTextField));
		return loadFfmpegButton;
	}

	private ActionListener pathSelection(JTextField jTextField) {
		ActionListener loadAction = (actionEvent) -> {
	    	final JFileChooser fc = new JFileChooser(Paths.get("").toAbsolutePath().toString());
	    	int returnVal = fc.showOpenDialog(this);
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	        	jTextField.setText(fc.getSelectedFile().toString());
	        }
	    };
		return loadAction;
	}
	
	private ActionListener directorySelection(JTextField jTextField) {
		ActionListener loadAction = (actionEvent) -> {
	    	final JFileChooser fc = new JFileChooser(Paths.get("").toAbsolutePath().toString());
	        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
	    	int returnVal = fc.showOpenDialog(this);
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	        	jTextField.setText(fc.getSelectedFile().toString());
	        }
	    };
		return loadAction;
	}
	
	private void launchExtraction() {
		System.out.println("Extraction !!!!");
			new FileConvertor().convert(
					this.ffmpegPathTextField.getText(),
					this.ffprobePathTextField.getText(),
					this.audioOrVideoPathTextField.getText(),
					this.tempPathTextField.getText()
					);

	}



	
	
}
