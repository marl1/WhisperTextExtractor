package fr.wtx;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;


public class MainWindow extends JFrame {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MainWindow.class);	
	
	private JTextField ffmpegPathTextField = new JTextField("");
	private JTextField ffprobePathTextField = new JTextField("");
	private JTextField modelPathTextField = new JTextField("");
	private JTextField audioOrVideoPathTextField = new JTextField("");
	private JTextField tempPathTextField = new JTextField("");
	private JTextArea textArea = new JTextArea("");
	
	private JComboBox<String> languagesComboBox;
	
	private JCheckBox translateCheckBox;
	
	private JLabel progressText;
	private JProgressBar progressBar;
	
	public MainWindow() {
		// select Look and Feel
        try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			LOGGER.error("Couldn't look window theme.", e);
		}
	    this.setTitle("WhisperTextExtractor");
	    this.setSize(600, 800);
	    this.setLocationRelativeTo(null);

	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    JPanel panel=new JPanel(new MigLayout());

	    this.setContentPane(panel);

	    // FFmpeg options
	    JLayeredPane ffmpegPanel = new JLayeredPane();
	    ffmpegPanel.setLayout(new MigLayout());
	    ffmpegPanel.setBorder(BorderFactory.createTitledBorder("FFmpeg options"));
	    ((TitledBorder)ffmpegPanel.getBorder()).setTitleColor(Color.gray);
	    panel.add(ffmpegPanel, "width 100%, wrap");
	    ffmpegPanel.add(new JLabel("FFmpeg executable path:"), "wrap");
	    ffmpegPanel.add(ffmpegPathTextField, "width 100%");
	    ffmpegPanel.add(createPathSelectorButton(ffmpegPathTextField), " wrap");
	    
	    ffmpegPanel.add(new JLabel("FFprobe executable path:"), "wrap");
	    ffmpegPanel.add(ffprobePathTextField, "width 100%");
	    ffmpegPanel.add(createPathSelectorButton(ffprobePathTextField), " wrap");

	    
	    // Whisper.cpp options
	    JLayeredPane whisperPanel = new JLayeredPane();
	    whisperPanel.setLayout(new MigLayout(""));
	    whisperPanel.setBorder(BorderFactory.createTitledBorder("Whisper.cpp options"));
	    ((TitledBorder)whisperPanel.getBorder()).setTitleColor(Color.gray);
	    panel.add(whisperPanel, "width 100%");
	    whisperPanel.add(new JLabel("Whisper model path:"), "wrap");
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
	    translateCheckBox = new JCheckBox("Try to translate in English");
	    whisperPanel.add(translateCheckBox, "align left");

	    // Extraction options
	    JLayeredPane runPanel = new JLayeredPane();
	    runPanel.setBorder(BorderFactory.createTitledBorder("Extraction"));
	    ((TitledBorder)runPanel.getBorder()).setTitleColor(Color.gray);
	    runPanel.setLayout(new MigLayout(""));
	    panel.add(runPanel, "width 100%, height 100%, newline");
	    runPanel.add(new JLabel("Audio/Video file to extract text from:"), "wrap");
	    runPanel.add(audioOrVideoPathTextField, "split2, width 100%");
	    runPanel.add(createPathSelectorButton(audioOrVideoPathTextField), " wrap");
	    runPanel.add(new JLabel("A temporary file will be created on this folder:"), "wrap");
	    runPanel.add(tempPathTextField, "split2, width 100%");
		JButton loadOutputTempDirectoryButton = new JButton("...");
		loadOutputTempDirectoryButton.addActionListener(directorySelection(tempPathTextField));
	    runPanel.add(loadOutputTempDirectoryButton, " wrap");
	    JButton extractTextButton = new JButton("Extract text!");
	    
	    extractTextButton.addActionListener((actionEvent) -> launchExtraction());
	    
	    runPanel.add(extractTextButton, "width 100%, wrap");
	    
	    
	    JScrollPane textAreaScrollPanel = new JScrollPane(textArea);
	    textAreaScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    textArea.setWrapStyleWord(true);
	    textArea.setLineWrap(true);
	    runPanel.add(textAreaScrollPanel, "width 100%, height 100%, wrap");
	    progressText = new JLabel();
	    runPanel.add(progressText, "wrap");
	    progressBar = new JProgressBar();
	    runPanel.add(progressBar, "width 100%");
	    loadProperties();
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
		saveProperties();
		progressText.setText("Converting file...");
		progressBar.setIndeterminate(true);
	    // Create a new thread for the conversion and extraction processes
	    new Thread(() -> {
	        File outputFile;
			try {
				outputFile = new FileConvertor().convert(
				        ffmpegPathTextField.getText(),
				        ffprobePathTextField.getText(),
				        audioOrVideoPathTextField.getText(),
				        tempPathTextField.getText()
				);
			} catch (Exception e) {
		        SwingUtilities.invokeLater(() -> progressText.setText("Error. Check FFmpeg/FFprobe executable path. Check you have writing permission on temp folder."));
		        SwingUtilities.invokeLater(() -> progressBar.setIndeterminate(false));
		        SwingUtilities.invokeLater(() -> progressBar.setValue(100));
		        SwingUtilities.invokeLater(() -> textArea.setText(e.getMessage()));
				JOptionPane.showMessageDialog(this, "Couldn't load FFmpeg. Check the paths are pointing on FFmpeg/FFprobes executables (not FOLDERS! Executables!). Also check you have writing permission on the temp folder.");
				return;
			}
	        SwingUtilities.invokeLater(() -> progressText.setText("Extracting text..."));
	        progressText.setText("Extracting text from audio...");
	        String extractedText;
			try {
				extractedText = new TextExtractor().extract(
				        outputFile.toString(),
				        languagesComboBox.getItemAt(languagesComboBox.getSelectedIndex()),
				        translateCheckBox.isSelected(),
				        modelPathTextField.getText()
				);
			} catch (Exception e) {
		        SwingUtilities.invokeLater(() -> progressText.setText("Error. Check paths."));
		        SwingUtilities.invokeLater(() -> progressBar.setIndeterminate(false));
		        SwingUtilities.invokeLater(() -> progressBar.setValue(100));
		        SwingUtilities.invokeLater(() -> textArea.setText(e.getMessage()));
				JOptionPane.showMessageDialog(this, "Error.");
				return;
			}

	        // Update the text area on the Event Dispatch Thread
	        SwingUtilities.invokeLater(() -> textArea.setText(extractedText));
	        SwingUtilities.invokeLater(() -> progressText.setText("Done."));
	        SwingUtilities.invokeLater(() -> progressBar.setIndeterminate(false));
	        SwingUtilities.invokeLater(() -> progressBar.setValue(100));
	    }).start();
	}
	
	private void loadProperties() {
	    Properties prop = new Properties();
	    Path configPath = Path.of(System.getProperty("user.dir"), "config.properties");

	    try (InputStream in = Files.newInputStream(configPath)) {
	        prop.load(in);	        
	        this.ffmpegPathTextField.setText(prop.getProperty("ffmpeg.path"));
	        this.ffprobePathTextField.setText(prop.getProperty("ffprobe.path"));
	        this.audioOrVideoPathTextField.setText(prop.getProperty("audioOrVideo.path"));
	        this.tempPathTextField.setText(prop.getProperty("temp.path"));
	        this.modelPathTextField.setText(prop.getProperty("model.path"));
	    } catch (IOException e) {
	        LOGGER.error("Couldn't load the config.", e);
	    }
	    
        if (StringUtils.isBlank(this.modelPathTextField.getText())) {
	        this.tempPathTextField.setText(System.getProperty("user.dir"));
        }

	}

	private void saveProperties() {
		Properties prop = new Properties();
	    Path configPath = Path.of(System.getProperty("user.dir"), "config.properties");

	    try (OutputStream out = Files.newOutputStream(configPath)) {
	    	prop.setProperty("ffmpeg.path", this.ffmpegPathTextField.getText());
	    	prop.setProperty("ffprobe.path", this.ffprobePathTextField.getText());
	    	prop.setProperty("audioOrVideo.path", this.audioOrVideoPathTextField.getText());
	    	prop.setProperty("temp.path", this.tempPathTextField.getText());
	    	prop.setProperty("model.path", this.modelPathTextField.getText());
	    	prop.store(out, "");
	    } catch (IOException e) {
	        LOGGER.error("Couldn't save the config.", e);
	    }
	}
	
}
