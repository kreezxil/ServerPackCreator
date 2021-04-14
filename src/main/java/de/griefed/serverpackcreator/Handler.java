package de.griefed.serverpackcreator;

import de.griefed.serverpackcreator.i18n.IncorrectLanguageException;
import de.griefed.serverpackcreator.i18n.LocalizationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class Handler {
    private static final Logger appLogger = LogManager.getLogger(Handler.class);

    /** Handler-class makes the calls to every other class where the actual magic is happening. The main class of ServerPackCreator should never contain code which does work on the server pack itself.
     * @param args Command Line Argument determines whether ServerPackCreator will start into normal operation mode or with a step-by-step generation of a configuration file.
     */
    void main(String[] args) {
        String jarPath = null,
                jarName = null,
                javaVersion = null,
                osArch = null,
                osName = null,
                osVersion = null;

        List<String> programArgs = Arrays.asList(args);
        if (Arrays.asList(args).contains(Reference.LANG_ARGUMENT)) {
            try {
                LocalizationManager.init(programArgs.get(programArgs.indexOf(Reference.LANG_ARGUMENT) + 1));
            } catch (IncorrectLanguageException e) {
                appLogger.info(programArgs.get(programArgs.indexOf(Reference.LANG_ARGUMENT) + 1));
                appLogger.error("Incorrect language specified, falling back to English (United States)...");
                LocalizationManager.init();
            }
        } else {
            Reference.filesSetup.checkLocaleFile();
        }

        appLogger.warn("################################################################");
        appLogger.warn(LocalizationManager.getLocalizedString("handler.log.warn.wip1"));
        appLogger.warn(LocalizationManager.getLocalizedString("handler.log.warn.wip2"));
        appLogger.warn(LocalizationManager.getLocalizedString("handler.log.warn.wip3"));
        appLogger.warn(LocalizationManager.getLocalizedString("handler.log.warn.wip4"));
        appLogger.warn("################################################################");

        try {
            jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            jarName = jarPath.substring(jarPath.lastIndexOf("/") + 1);
            javaVersion = System.getProperty("java.version");
            osArch = System.getProperty("os.arch");
            osName = System.getProperty("os.name");
            osVersion = System.getProperty("os.version");
            appLogger.info(LocalizationManager.getLocalizedString("handler.log.info.system.enter"));
            appLogger.info(String.format(LocalizationManager.getLocalizedString("handler.log.info.system.jarpath"), jarPath));
            appLogger.info(String.format(LocalizationManager.getLocalizedString("handler.log.info.system.jarname"), jarName));
            appLogger.info(String.format(LocalizationManager.getLocalizedString("handler.log.info.system.java"), javaVersion));
            appLogger.info(String.format(LocalizationManager.getLocalizedString("handler.log.info.system.osarchitecture"), osArch));
            appLogger.info(String.format(LocalizationManager.getLocalizedString("handler.log.info.system.osname"), osName));
            appLogger.info(String.format(LocalizationManager.getLocalizedString("handler.log.info.system.osversion"), osVersion));
            appLogger.info(LocalizationManager.getLocalizedString("handler.log.info.system.include"));

        } catch (URISyntaxException ex) {
            appLogger.error(LocalizationManager.getLocalizedString("handler.log.error.system.properties"), ex);
        }

        if (Arrays.asList(args).contains(Reference.CONFIG_GEN_ARGUMENT)){

            Reference.cliSetup.setup();
            Reference.filesSetup.filesSetup();
            runInCli();

            System.exit(0);

        } else if (Arrays.asList(args).contains(Reference.RUN_CLI_ARGUMENT)) {
            if (!Reference.oldConfigFile.exists() && !Reference.configFile.exists()) {

                Reference.cliSetup.setup();
                Reference.filesSetup.filesSetup();
            }
            runInCli();

            System.exit(0);
        } else {
            Reference.mainGUI.main();
        }
    }

    /**
     * Run when ServerPackCreator is run in either -cli or -cgen mode. Runs what used to be the main content in Main in pre-1.x.x. times. Inits config checks and, if config checks are successfull, calls methods to create the server pack.
     */
    private void runInCli() {
        if (!Reference.configCheck.checkConfig()) {
            Reference.copyFiles.cleanupEnvironment(Reference.modpackDir);
            try {
                Reference.copyFiles.copyFiles(Reference.modpackDir, Reference.copyDirs, Reference.clientMods);
            } catch (IOException ex) {
                appLogger.error(LocalizationManager.getLocalizedString("handler.log.error.runincli.copyfiles"), ex);
            }
            Reference.copyFiles.copyStartScripts(Reference.modpackDir, Reference.modLoader, Reference.includeStartScripts);
            if (Reference.includeServerInstallation) {
                Reference.serverSetup.installServer(Reference.modLoader, Reference.modpackDir, Reference.minecraftVersion, Reference.modLoaderVersion, Reference.javaPath);
            } else {
                appLogger.info(LocalizationManager.getLocalizedString("handler.log.info.runincli.server"));
            }
            if (Reference.includeServerIcon) {
                Reference.copyFiles.copyIcon(Reference.modpackDir);
            } else {
                appLogger.info(LocalizationManager.getLocalizedString("handler.log.info.runincli.icon"));
            }
            if (Reference.includeServerProperties) {
                Reference.copyFiles.copyProperties(Reference.modpackDir);
            } else {
                appLogger.info(LocalizationManager.getLocalizedString("handler.log.info.runincli.properties"));
            }
            if (Reference.includeZipCreation) {
                Reference.serverSetup.zipBuilder(Reference.modpackDir, Reference.modLoader, Reference.includeServerInstallation);
            } else {
                appLogger.info(LocalizationManager.getLocalizedString("handler.log.info.runincli.zip"));
            }
            appLogger.info(String.format(LocalizationManager.getLocalizedString("handler.log.info.runincli.serverpack"), Reference.modpackDir));
            appLogger.info(String.format(LocalizationManager.getLocalizedString("handler.log.info.runincli.archive"), Reference.modpackDir));
            appLogger.info(LocalizationManager.getLocalizedString("handler.log.info.runincli.finish"));
            System.exit(0);
        } else {
            appLogger.error(LocalizationManager.getLocalizedString("handler.log.error.runincli"));
            System.exit(1);
        }
    }
}
