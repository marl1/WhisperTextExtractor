package fr.wtx;

import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
	
	private JComboBox languagesComboBox;
	
	public MainWindow() {
		LafManager.install();
	    this.setTitle("");
	    this.setSize(600, 800);
	    this.setLocationRelativeTo(null);

	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    JPanel panel=new JPanel(new MigLayout());

	    this.setContentPane(panel);

	    JLayeredPane ffmpegPanel = new JLayeredPane();
	    ffmpegPanel.setEnabled(false);
	    ffmpegPanel.setLayout(new MigLayout());
	    ffmpegPanel.setBorder(BorderFactory.createTitledBorder("FFmpeg options"));
	    panel.add(ffmpegPanel, "width 100%, wrap");
	    ffmpegPanel.add(new JLabel("FFmpeg executable path:"), "wrap");
	    ffmpegPanel.add(new JTextField(), "width 100%");
	    ffmpegPanel.add(new JButton("..."), " wrap");
	    ffmpegPanel.add(new JLabel("FFprobe executable path:"), "wrap");
	    ffmpegPanel.add(new JTextField(), "width 100%");
	    ffmpegPanel.add(new JButton("..."), " wrap");
	    
	    JLayeredPane whisperPanel = new JLayeredPane();
	    whisperPanel.setEnabled(false);
	    whisperPanel.setLayout(new MigLayout(""));
	    whisperPanel.setBorder(BorderFactory.createTitledBorder("Whisper.cpp options"));
	    panel.add(whisperPanel, "width 100%");
	    whisperPanel.add(new JLabel("Model path:"), "wrap");
	    whisperPanel.add(new JTextField(), "span 3, width 100%");
	    whisperPanel.add(new JButton("..."), " wrap");
	    
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

	    JLayeredPane runPanel = new JLayeredPane();
	    runPanel.setBorder(BorderFactory.createTitledBorder("Extraction"));
	    runPanel.setLayout(new MigLayout(""));
	    panel.add(runPanel, "width 100%, height 100%, newline");
	    runPanel.add(new JLabel("Audio/Video file to extract from:"), "wrap");
	    runPanel.add(new JTextField(), "split2, width 100%");
	    runPanel.add(new JButton("..."), " wrap");
	    runPanel.add(new JButton("Extract text!"), "width 100%, wrap");
	    
	    JTextArea textArea = new JTextArea("");
	    JScrollPane textAreaScrollPanel = new JScrollPane(textArea);
	    textAreaScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    textArea.setWrapStyleWord(true);
	    textArea.setLineWrap(true);
	    runPanel.add(textAreaScrollPanel, "width 100%, height 100%, wrap");

	    this.setVisible(true);

	}

}
