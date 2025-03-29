#!/bin/bash
JFX_PATH=/full/path/to/jfx
${JAVA_HOME}/bin/java -Djdk.gtk.version=2 --module-path ${JFX_PATH}/lib --add-modules javafx.controls,javafx.fxml --add-exports javafx.base/com.sun.javafx.event=ALL-UNNAMED -jar NovaPoshta.jar &
