package fr.wtx;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.file.Path;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.givimad.whisperjni.WhisperContext;
import io.github.givimad.whisperjni.WhisperFullParams;
import io.github.givimad.whisperjni.WhisperJNI;

public class TextExtractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(TextExtractor.class);	


	public String extract(String wavToExtractFrom, String language, boolean mustTranslate, String modelPath) throws Exception {
	    System.out.println("Hello!");

	    // Load platform binaries
	    try {
			WhisperJNI.loadLibrary();
		} catch (IOException e) {
			LOGGER.error("Couldn't load whisper.cpp.", e);
			throw e;
		}
	    WhisperJNI.setLibraryLogger(null); // Capture/disable whisper.cpp log

	    // Create an instance of WhisperJNI
	    WhisperJNI whisper = new WhisperJNI();
	    
	    // Read the entire audio file
	    float[] samples;
		try {
			samples = readFile(wavToExtractFrom);
		} catch (UnsupportedAudioFileException | IOException | URISyntaxException e) {
			LOGGER.error("Couldn't read wav file.", e);
			throw e;
		}
	    System.out.println("Number of samples read: " + samples.length);

	    // Initialize Whisper context with the model file
	    WhisperContext ctx;
		try {
			ctx = whisper.init(Path.of(modelPath));
		} catch (IOException e) {
			LOGGER.error("Couldn't open path to whisper model.", e);
			throw e;
		}
	    if (ctx == null) {
	        throw new RuntimeException("Failed to initialize Whisper context");
	    }

	    // Create WhisperFullParams
	    WhisperFullParams params = new WhisperFullParams();
	    params.translate = mustTranslate;
	    if (StringUtils.isNotBlank(language)) {
	    	params.language = language;
	    }
	    
	    
	    // Perform transcription
	    int result = whisper.full(ctx, params, samples, samples.length);
	    if (result != 0) {
	        throw new RuntimeException("Transcription failed with code " + result);
	    }

	    // Get the number of segments
	    int numSegments = whisper.fullNSegments(ctx);
	    System.out.println("Number of segments: " + numSegments);
	    
	    // Retrieve and print the transcribed text
	    StringBuilder transcribedText = new StringBuilder();
	    for (int i = 0; i < numSegments; i++) {
	        String segmentText = whisper.fullGetSegmentText(ctx, i);
	        transcribedText.append(segmentText).append(" ");
	    }
	    System.out.println("Transcribed text: " + transcribedText.toString().trim());

	    // Close the context to free native memory
	    ctx.close();
		return transcribedText.toString();
	}
	
	private static float[] readFile(String filePath) throws UnsupportedAudioFileException, IOException, URISyntaxException {
		 
		// sample is a 16 bit int 16000hz little endian wav file
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(
				filePath
				));
		// read all the available data to a little endian capture buffer
		ByteBuffer captureBuffer = ByteBuffer.allocate(audioInputStream.available());
		captureBuffer.order(ByteOrder.LITTLE_ENDIAN);
		int read = audioInputStream.read(captureBuffer.array());
		if (read == -1) {
			throw new IOException("Empty file");
		}
		// obtain the 16 int audio samples, short type in java
		ShortBuffer shortBuffer = captureBuffer.asShortBuffer();
		// transform the samples to f32 samples
		float[] samples = new float[captureBuffer.capacity() / 2];
		int i = 0;
		while (shortBuffer.hasRemaining()) {
			samples[i++] = Float.max(-1f, Float.min(((float) shortBuffer.get()) / (float) Short.MAX_VALUE, 1f));
		}
		return samples;
		
	}
	
}
