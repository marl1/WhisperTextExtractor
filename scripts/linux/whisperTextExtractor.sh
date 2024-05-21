#!/bin/bash
# Get the directory of the current script
SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

chmod +x "$SCRIPT_DIR/bin/java" 
# Execute the Java application
"$SCRIPT_DIR/bin/java" -jar "$SCRIPT_DIR/jars/whisperTextExtractor-0.0.1-SNAPSHOT-jar-with-dependencies.jar"

# Exit the script
exit 0
