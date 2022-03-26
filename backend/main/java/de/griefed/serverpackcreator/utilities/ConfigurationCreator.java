/* Copyright (C) 2022  Griefed
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 *
 * The full license can be found at https:github.com/Griefed/ServerPackCreator/blob/main/LICENSE
 */
package de.griefed.serverpackcreator.utilities;

import de.griefed.serverpackcreator.ApplicationProperties;
import de.griefed.serverpackcreator.ConfigurationHandler;
import de.griefed.serverpackcreator.VersionLister;
import de.griefed.serverpackcreator.curseforge.CurseCreateModpack;
import de.griefed.serverpackcreator.i18n.LocalizationManager;
import de.griefed.serverpackcreator.utilities.misc.Generated;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

/**
 * Utility-class revolving around the creation of a new configuration file.
 * @author Griefed
 */
@Generated
public class ConfigurationCreator {

    private static final Logger LOG = LogManager.getLogger(ConfigurationCreator.class);

    private final LocalizationManager LOCALIZATIONMANAGER;
    private final ConfigurationHandler CONFIGURATIONHANDLER;
    private final BooleanUtilities BOOLEANUTILITIES;
    private final ApplicationProperties APPLICATIONPROPERTIES;
    private final ListUtilities LISTUTILITIES;
    private final StringUtilities STRINGUTILITIES;
    private final ConfigUtilities CONFIGUTILITIES;
    private final SystemUtilities SYSTEMUTILITIES;
    private final CurseCreateModpack CURSECREATEMODPACK;
    private final VersionLister VERSIONLISTER;

    public ConfigurationCreator(LocalizationManager injectedLocalizationManager, ConfigurationHandler injectedConfigurationHandler,
                                BooleanUtilities injectedBooleanUtilities, ApplicationProperties injectedApplicationProperties,
                                ListUtilities injectedListUtilities, StringUtilities injectedStringUtilities, ConfigUtilities injectedConfigUtilities,
                                SystemUtilities injectedSystemUtilities, CurseCreateModpack injectedCurseCreateModpack,
                                VersionLister injectedVersionLister) {

        if (injectedApplicationProperties == null) {
            this.APPLICATIONPROPERTIES = new ApplicationProperties();
        } else {
            this.APPLICATIONPROPERTIES = injectedApplicationProperties;
        }

        if (injectedLocalizationManager == null) {
            this.LOCALIZATIONMANAGER = new LocalizationManager(APPLICATIONPROPERTIES);
        } else {
            this.LOCALIZATIONMANAGER = injectedLocalizationManager;
        }

        if (injectedBooleanUtilities == null) {
            this.BOOLEANUTILITIES = new BooleanUtilities(LOCALIZATIONMANAGER, APPLICATIONPROPERTIES);
        } else {
            this.BOOLEANUTILITIES = injectedBooleanUtilities;
        }

        if (injectedListUtilities == null) {
            this.LISTUTILITIES = new ListUtilities();
        } else {
            this.LISTUTILITIES = injectedListUtilities;
        }

        if (injectedStringUtilities == null) {
            this.STRINGUTILITIES = new StringUtilities();
        } else {
            this.STRINGUTILITIES = injectedStringUtilities;
        }

        if (injectedSystemUtilities == null) {
            this.SYSTEMUTILITIES = new SystemUtilities();
        } else {
            this.SYSTEMUTILITIES = injectedSystemUtilities;
        }

        if (injectedVersionLister == null) {
            this.VERSIONLISTER = new VersionLister(APPLICATIONPROPERTIES);
        } else {
            this.VERSIONLISTER = injectedVersionLister;
        }

        if (injectedConfigUtilities == null) {
            this.CONFIGUTILITIES = new ConfigUtilities(LOCALIZATIONMANAGER, BOOLEANUTILITIES, LISTUTILITIES, APPLICATIONPROPERTIES, STRINGUTILITIES, VERSIONLISTER);
        } else {
            this.CONFIGUTILITIES = injectedConfigUtilities;
        }

        if (injectedCurseCreateModpack == null) {
            this.CURSECREATEMODPACK = new CurseCreateModpack(LOCALIZATIONMANAGER, APPLICATIONPROPERTIES, VERSIONLISTER, BOOLEANUTILITIES, LISTUTILITIES, STRINGUTILITIES, CONFIGUTILITIES, SYSTEMUTILITIES);
        } else {
            this.CURSECREATEMODPACK = injectedCurseCreateModpack;
        }

        if (injectedConfigurationHandler == null) {
            this.CONFIGURATIONHANDLER = new ConfigurationHandler(LOCALIZATIONMANAGER, CURSECREATEMODPACK, VERSIONLISTER, APPLICATIONPROPERTIES, BOOLEANUTILITIES, LISTUTILITIES, STRINGUTILITIES, SYSTEMUTILITIES, CONFIGUTILITIES);
        } else {
            this.CONFIGURATIONHANDLER = injectedConfigurationHandler;
        }
    }

    /**
     * Walk the user through the generation of a new ServerPackCreator configuration file by asking them for input,
     * step-by-step, regarding their modpack. At the end of this method a fully configured serverpackcreator.conf file
     * is saved and any previously existing configuration file replaced by the new one.<br>
     * After every input, said input is displayed to the user, and they're asked whether they are satisfied with said
     * input. The user can then decide whether they would like to restart the entry of the field they just configured,
     * or agree and move to the next one.<br>
     * At the end of this method, the user will have a newly configured and created configuration file for ServerPackCreator.<br>
     * <br>
     * Most user-input is checked after entry to ensure the configuration is already in working-condition after completion
     * of this method.
     * @author whitebear60
     * @author Griefed
     */
    public void createConfigurationFile() {

        List<String> clientMods, copyDirs, encounteredErrors;

        clientMods = new ArrayList<>(100);
        copyDirs = new ArrayList<>(100);
        encounteredErrors = new ArrayList<>(100);

        String[] tmpClientMods, tmpCopyDirs;

        boolean includeServerInstallation,
                includeServerIcon,
                includeServerProperties,
                includeZipCreation;

        String modpackDir,
                javaPath,
                minecraftVersion,
                modLoader,
                modLoaderVersion,
                serverIconPath,
                serverPropertiesPath,
                tmpModpackDir,
                tmpServerIcon,
                tmpServerProperties,
                javaArgs,
                serverPackSuffix;

        Scanner reader = new Scanner(System.in);

        /* This log is meant to be read by the user, therefore we allow translation. */
        LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.start"), "-cgen"));
        do {
//--------------------------------------------------------------------------------------------MODPACK DIRECTORY---------
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.modpack.enter"));
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.modpack.example"));

            do {

                do {
                    System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.modpack.cli") + " ");
                    tmpModpackDir = reader.nextLine();
                } while (!CONFIGURATIONHANDLER.checkModpackDir(tmpModpackDir, encounteredErrors));

                /* This log is meant to be read by the user, therefore we allow translation. */
                LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), tmpModpackDir));
                LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.modpack.checkreturninfo"));

                System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.answer") + " ");

            } while (!BOOLEANUTILITIES.readBoolean());

            modpackDir = tmpModpackDir.replace("\\", "/");
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), modpackDir));
            System.out.println();

//-----------------------------------------------------------------------------------------CLIENTSIDE-ONLY MODS---------
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.clientmods.enter"));
            do {
                clientMods.clear();

                clientMods.addAll(LISTUTILITIES.readStringArray());

                /* This log is meant to be read by the user, therefore we allow translation. */
                LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), clientMods));

                if (clientMods.isEmpty()) {
                    clientMods = APPLICATIONPROPERTIES.getListFallbackMods();

                    /* This log is meant to be read by the user, therefore we allow translation. */
                    LOG.warn(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.warn.checkconfig.clientmods"));

                    for (String mod : clientMods) {
                        LOG.warn(String.format("    %s", mod));
                    }
                }

                /* This log is meant to be read by the user, therefore we allow translation. */
                LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.clientmods.checkreturninfo"));

                System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.answer") + " ");

            } while (!BOOLEANUTILITIES.readBoolean());

            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), clientMods));

            tmpClientMods = new String[clientMods.size()];
            clientMods.toArray(tmpClientMods);

            System.out.println();

//------------------------------------------------------------------DIRECTORIES OR FILES TO COPY TO SERVER PACK---------
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.copydirs.enter"));

            List<String> dirList = Arrays.asList(Objects.requireNonNull(new File(modpackDir).list((current, name) -> new File(current, name).isDirectory())));

            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.copydirs.dirsinmodpack"), dirList.toString().replace("[","").replace("]","")));
            do {
                do {
                    copyDirs.clear();
                    /* This log is meant to be read by the user, therefore we allow translation. */
                    LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.copydirs.specify"));
                    copyDirs.addAll(LISTUTILITIES.readStringArray());

                } while (!CONFIGURATIONHANDLER.checkCopyDirs(copyDirs, modpackDir, encounteredErrors));

                /* This log is meant to be read by the user, therefore we allow translation. */
                LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), copyDirs));
                LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.copydirs.checkreturninfo"));

                System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.answer") + " ");

            } while (!BOOLEANUTILITIES.readBoolean());

            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), copyDirs));

            tmpCopyDirs = new String[copyDirs.size()];
            copyDirs.toArray(tmpCopyDirs);

            System.out.println();

//---------------------------------------------------------------------------PATH TO THE CUSTOM SERVER-ICON.PNG---------
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.custom.icon.enter"));

            do {

                do {
                    /* This log is meant to be read by the user, therefore we allow translation. */
                    System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.custom.icon.path"));
                    tmpServerIcon = reader.nextLine();

                } while (!CONFIGURATIONHANDLER.checkIconAndProperties(tmpServerIcon));

                /* This log is meant to be read by the user, therefore we allow translation. */
                LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), tmpServerIcon));
                LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.custom.icon.end"));

                System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.answer") + " ");

            } while (!BOOLEANUTILITIES.readBoolean());

            serverIconPath = tmpServerIcon.replace("\\", "/");
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), serverIconPath));
            System.out.println();

//-------------------------------------------------------------------------PATH TO THE CUSTOM SERVER.PROPERTIES---------

            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.custom.properties.enter"));

            do {

                do {
                    /* This log is meant to be read by the user, therefore we allow translation. */
                    System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.custom.properties.path"));
                    tmpServerProperties = reader.nextLine();
                } while (!CONFIGURATIONHANDLER.checkIconAndProperties(tmpServerProperties));

                /* This log is meant to be read by the user, therefore we allow translation. */
                LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), tmpServerProperties));
                LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.custom.properties.end"));

                System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.answer") + " ");

            } while (!BOOLEANUTILITIES.readBoolean());

            serverPropertiesPath = tmpServerProperties.replace("\\", "/");
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), serverPropertiesPath));
            System.out.println();

//-------------------------------------------------------------WHETHER TO INCLUDE MODLOADER SERVER INSTALLATION---------
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.server.enter"));

            System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.server.include") + " ");
            includeServerInstallation = BOOLEANUTILITIES.readBoolean();

            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), includeServerInstallation));

//-------------------------------------------------------------------------------MINECRAFT VERSION MODPACK USES---------
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.minecraft.enter"));

            do {
                System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.minecraft.specify") + " ");
                minecraftVersion = reader.nextLine();

            } while (!CONFIGURATIONHANDLER.isMinecraftVersionCorrect(minecraftVersion));

            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), minecraftVersion));
            System.out.println();

//---------------------------------------------------------------------------------------MODLOADER MODPACK USES---------
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.modloader.enter"));

            do {
                System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.modloader.cli") + " ");
                modLoader = reader.nextLine();

            } while (!CONFIGURATIONHANDLER.checkModloader(modLoader));

            modLoader = CONFIGUTILITIES.getModLoaderCase(modLoader);

            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), modLoader));
            System.out.println();

//----------------------------------------------------------------------------VERSION OF MODLOADER MODPACK USES---------
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.modloaderversion.enter"), modLoader));

            do {
                System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.modloaderversion.cli") + " ");
                modLoaderVersion = reader.nextLine();

            } while (!CONFIGURATIONHANDLER.checkModloaderVersion(modLoader, modLoaderVersion, minecraftVersion));

            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), modLoaderVersion));
            System.out.println();

//------------------------------------------------------------------------------------PATH TO JAVA INSTALLATION---------
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.java.enter"));
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.java.enter2"));
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.java.example"));

            System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.java.cli") + " ");

            javaPath = CONFIGURATIONHANDLER.checkJavaPath(reader.nextLine());

            //noinspection RedundantStringFormatCall
            System.out.println(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.warn.getjavapath.set"), javaPath));



            System.out.println();

//------------------------------------------------------------WHETHER TO INCLUDE SERVER-ICON.PNG IN SERVER PACK---------
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.icon.enter"));

            System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.icon.cli") + " ");
            includeServerIcon = BOOLEANUTILITIES.readBoolean();

            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), includeServerIcon));
            System.out.println();

//----------------------------------------------------------WHETHER TO INCLUDE SERVER.PROPERTIES IN SERVER PACK---------
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.properties.enter"));

            System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.properties.cli") + " ");
            includeServerProperties = BOOLEANUTILITIES.readBoolean();

            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), includeServerProperties));
            System.out.println();

//----------------------------------------------------WHETHER TO INCLUDE CREATION OF ZIP-ARCHIVE OF SERVER PACK---------
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.zip.enter"));

            System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.zip.cli") + " ");
            includeZipCreation = BOOLEANUTILITIES.readBoolean();

            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.checkreturn"), includeZipCreation));

//-------------------------------------------------------------------------JAVA ARGS TO EXECUTE THE SERVER WITH---------

            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.javaargs.cli"));

            System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.javaargs.enter"));
            javaArgs = reader.nextLine();

            if (javaArgs.equals("")) {
                javaArgs = "empty";
            }

            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.printconfig.javaargs"), javaArgs));

//------------------------------------------------------------------------------SUFFIX TO APPEND TO SERVER PACK---------

            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.serverpacksuffix.cli"));

            System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.serverpacksuffix.enter"));
            serverPackSuffix = reader.nextLine();

//------------------------------------------------------------------------------PRINT CONFIG TO CONSOLE AND LOG---------
            CONFIGUTILITIES.printConfigurationModel(modpackDir,
                    clientMods,
                    copyDirs,
                    includeServerInstallation,
                    javaPath,
                    minecraftVersion,
                    modLoader,
                    modLoaderVersion,
                    includeServerIcon,
                    includeServerProperties,
                    includeZipCreation,
                    javaArgs,
                    serverPackSuffix,
                    serverIconPath,
                    serverPropertiesPath);

            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.config.enter"));

            System.out.print(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.answer") + " ");

        } while (!BOOLEANUTILITIES.readBoolean());

        reader.close();

//-----------------------------------------------------------------------------------------WRITE CONFIG TO FILE---------
        if (CONFIGUTILITIES.writeConfigToFile(
                modpackDir,
                Arrays.asList(tmpClientMods),
                Arrays.asList(tmpCopyDirs),
                serverIconPath,
                serverPropertiesPath,
                includeServerInstallation,
                javaPath,
                minecraftVersion,
                modLoader,
                modLoaderVersion,
                includeServerIcon,
                includeServerProperties,
                includeZipCreation,
                javaArgs,
                serverPackSuffix,
                APPLICATIONPROPERTIES.FILE_CONFIG
        )) {
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("configuration.log.info.config.written"));
        }
    }

}