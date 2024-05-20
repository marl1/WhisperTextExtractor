package fr.wtx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class FileConvertor {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileConvertor.class);	
	
	public File convert(
						String ffmpegPath,
						String ffprobePath,
						String inputFilePath,
						String outputFilePath
												)  {
	
		
		File outputFile = Path.of(outputFilePath, "temp.wav").toFile();
		try {
			FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
			FFprobe ffprobe = new FFprobe(ffprobePath);

			FFmpegBuilder builder = new FFmpegBuilder()
	.overrideOutputFiles(true)
			  .setInput(inputFilePath)     // Filename, or a FFmpegProbeResult
			  
			  .overrideOutputFiles(true) // Override the output if it exists
			  
			  .addOutput(outputFile.toString() )   // Filename for the destination
			    .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
			  .setFormat("wav")
			  .setAudioSampleRate(8000)
			 .setAudioCodec("pcm_s16le")
			    .disableVideo()
			  .done();
	
			FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
	
			// Run a one-pass encode
			executor.createJob(builder).run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error trying to load folders. Check all folder are corrects.", e);
		}
		return outputFile;
	}
}
