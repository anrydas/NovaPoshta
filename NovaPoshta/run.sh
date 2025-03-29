#!/bin/bash
JFX_PATH=/full/path/to/jfx
${JAVA_HOME}/bin/java --module-path ${JFX_PATH}/lib --add-modules javafx.controls,javafx.fxm -jar NovaPoshta.jar &
