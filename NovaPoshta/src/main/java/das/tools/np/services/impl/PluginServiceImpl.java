package das.tools.np.services.impl;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.plugin.PluginInfo;
import das.tools.np.entity.plugin.PluginInterface;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.services.ConfigService;
import das.tools.np.services.PluginService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Note: jar file with plugin MUST have its name as the plugin's Class Name
 */
@Service
@Slf4j
public class PluginServiceImpl implements PluginService {
    public static final String APPLICATION_FOLDER = System.getProperty("user.dir");
    public static final String PLUGINS_FOLDER = "plugins";
    public static final String PATH_SEPARATOR = File.separator;
    public static final String PLUGINS_FOLDER_PATH = APPLICATION_FOLDER + PATH_SEPARATOR + PLUGINS_FOLDER;
    public static final String PLUGIN_EXTENSION = ".jar";
    public static final String PLUGIN_PACKAGE = "das.tools.np.entity.plugin";
    private final List<PluginInfo> allPlugins = new LinkedList<>();
    private final AlertService alertService;
    private final ConfigService configService;

    public PluginServiceImpl(AlertService alertService, ConfigService configService) {
        this.alertService = alertService;
        this.configService = configService;
    }

    @Override @PostConstruct
    public void loadPlugins() {
        reloadPluginsData(getPluginFiles());
    }

    private List<File> getPluginFiles() {
        List<File> jarFiles = new LinkedList<>();
        if (isFileExists(PLUGINS_FOLDER_PATH)) {
            listFiles(PLUGINS_FOLDER_PATH, jarFiles);
            if (log.isDebugEnabled()) log.debug("got jarFiles={}", jarFiles);
        }
        return jarFiles;
    }

    @Override
    public List<PluginInfo> getAllPlugins() {
        return allPlugins;
    }

    @Override
    public void launchPlugin(List<CargoNumber> numbers, String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile() && Files.isReadable(Paths.get(fileName))) {
            Class<?> clazz = loadClass(file);
            if (clazz != null) {
                try {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getMethod("doProcess", List.class);
                    method.invoke(instance, numbers);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                         InvocationTargetException e) {
                    log.error("Error launching method: ", e);
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    alertService.showError("Error launching plugin", e.getLocalizedMessage(), sw.toString());
                }
            }
        }
    }

    private void reloadPluginsData(List<File> jarFiles) {
        allPlugins.clear();
        for (File f : jarFiles) {
            Class<?> clazz = loadClass(f);
            if (clazz != null) {
                if (isPluginInterfaceExists(clazz) &&
                        isRequiredMethodsExists(clazz) &&
                        PluginInterface.class.isAssignableFrom(clazz)) {
                    PluginInfo newPlugin = getNewPlugin(clazz, f);
                    if (newPlugin != null) {
                        allPlugins.add(newPlugin);
                        if (log.isDebugEnabled()) log.debug("got new plugin={}", newPlugin);
                    }
                }
            }
        }
    }

    private PluginInfo getNewPlugin(Class<?> clazz, File f) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            String lang = configService.getCurrentLanguageCode();
            String name = "uk".equals(lang) ? (String) clazz.getMethod("getNameUK").invoke(instance) : (String) clazz.getMethod("getName").invoke(instance);
            String descr = "uk".equals(lang) ? (String) clazz.getMethod("getDescriptionUK").invoke(instance) : (String) clazz.getMethod("getDescription").invoke(instance);
            String relativePath = getRelativePath(f);
            if (relativePath.contains(File.separator)) {
                log.error(String.format("The '%s' plugin have too deep path '%s'. It may be only one level subdirectory.", f.getName(), relativePath));
                return null;
            }
            return PluginInfo.builder()
                    .name(name)
                    .description(descr)
                    .absolutePath(f.getAbsolutePath())
                    .className(clazz.getName())
                    .pluginSubdirectory(relativePath)
                    .build();
        } catch (NoSuchMethodException e) {
            log.error("The method wasn't found in class: ", e);
        } catch (InvocationTargetException e) {
            log.error("Couldn't invoke method: ", e);
        } catch (InstantiationException e) {
            log.error("Couldn't get an instance: ", e);
        } catch (IllegalAccessException e) {
            log.error("Couldn't get access to method: ", e);
        }
        return null;
    }

    private String getRelativePath(File f) {
        String absolutePath = f.getAbsolutePath();
        int posOfFileName = absolutePath.indexOf(f.getName()) - 1;
        String relativeName = absolutePath.substring(0, posOfFileName);
        int posOfAppFolder = absolutePath.indexOf(PLUGINS_FOLDER_PATH) + PLUGINS_FOLDER_PATH.length();
        String relativePath = relativeName.substring(posOfAppFolder);
        if (relativePath.startsWith(File.separator)) relativePath = relativePath.substring(1);
        return relativePath;
    }

    private Class<?> loadClass(File jarFile) {
        URLClassLoader loader = null;
        try {
            loader = new URLClassLoader(
                    new URL[] {jarFile.toURI().toURL()},
                    this.getClass().getClassLoader()
            );
            String className = PLUGIN_PACKAGE + "." + getPluginClassName(jarFile);
            if (log.isDebugEnabled()) log.debug("try to load class '{}' from file '{}'", className, jarFile.getAbsolutePath());
            return Class.forName(className, true, loader);
        } catch (MalformedURLException e) {
            log.error("Couldn't get class loader: ", e);
        } catch (ClassNotFoundException e) {
            log.error(String.format("The class '%s' was not found in '%s'", getPluginClassName(jarFile), jarFile.getAbsolutePath()));
        }
        return null;
    }

    private boolean isPluginInterfaceExists(Class<?> clazz) {
        return Arrays.asList(clazz.getInterfaces()).contains(PluginInterface.class);
    }

    private boolean isRequiredMethodsExists(Class<?> clazz) {
        List<String> methodNames = Arrays.stream(clazz.getMethods()).map(Method::getName).toList();
        List<String> requiredMethods = Arrays.stream(PluginInterface.class.getMethods()).map(Method::getName).toList();
        return new HashSet<>(methodNames).containsAll(requiredMethods);
    }

    private String getPluginClassName(File file) {
        int pos = file.getName().indexOf(PLUGIN_EXTENSION);
        return file.getName().substring(0, pos);
    }

    private void listFiles(String directory, List<File> files) {
        File[] fList = new File(directory).listFiles();
        if (fList != null) {
            for (File f : fList) {
                if (f.isFile() && f.getName().contains(PLUGIN_EXTENSION)) {
                    files.add(f);
                } else if (f.isDirectory()) {
                    listFiles(f.getAbsolutePath(), files);
                }
            }
        }
    }

    private boolean isFileExists(String file) {
        return (new File(file).exists());
    }
}
