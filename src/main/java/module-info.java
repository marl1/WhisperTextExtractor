open module whisperTextExtractor {
	exports fr.wtx;
	requires java.base;
	requires java.desktop;

    requires org.slf4j;
    requires org.apache.commons.lang3;
    requires ffmpeg;
    requires whisper.jni;
	requires darklaf.core;
	requires com.miglayout.swing;

}