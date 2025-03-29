JFX_PATH=full_path_to_jfx
start %JAVA_HOME%\bin\javaw.exe -Djdk.gtk.version=2 --module-path %JFX_PATH%\lib --add-modules javafx.controls,javafx.fxml --add-exports javafx.base/com.sun.javafx.event=ALL-UNNAME -jar NovaPoshta.jar
