package de.griefed.serverpackcreator;

import de.griefed.serverpackcreator.i18n.LocalizationManager;
import net.fabricmc.installer.util.LauncherMeta;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

class ServerUtilities {
    private static final Logger appLogger = LogManager.getLogger(FilesSetup.class);

    /** Calls methods for generating download scripts for Mojang's Minecraft server depending on the specified versions and modloader.
     * @param modLoader String. The specified modloader determines the name under which Mojang's server jar will be downloaded as.
     * @param modpackDir String. /server_pack The directory where the scripts will be placed in.
     * @param minecraftVersion String. The version of the Minecraft server jar to download.
     */
    void generateDownloadScripts(String modLoader, String modpackDir, String minecraftVersion) {
        if (modLoader.equalsIgnoreCase("Fabric")) {
            fabricShell(modpackDir, minecraftVersion);
            fabricBatch(modpackDir, minecraftVersion);
        } else if (modLoader.equalsIgnoreCase("Forge")) {
            forgeShell(modpackDir, minecraftVersion);
            forgeBatch(modpackDir, minecraftVersion);
        } else {
            appLogger.error(String.format(LocalizationManager.getLocalizedString("configcheck.log.error.checkmodloader"), modLoader));
        }
    }

    /** Generates download scripts for Mojang's Minecraft server for Fabric,Linux.
     * @param modpackDir String. /server_pack The directory where the scripts will be placed in.
     * @param minecraftVersion String. The version of the Minecraft server jar to download.
     */
    private void fabricShell(String modpackDir, String minecraftVersion) {
        try {
            String downloadMinecraftServer = (new URL(
                    LauncherMeta
                            .getLauncherMeta()
                            .getVersion(minecraftVersion)
                            .getVersionMeta()
                            .downloads
                            .get("server")
                            .url))
                    .toString();
            String shFabric = String.format("#!/bin/bash\n#Download the Minecraft_server.jar for your modpack\n\nwget -O server.jar %s", downloadMinecraftServer);
            Path pathSh = Paths.get(String.format("%s/server_pack/download_minecraft-server.jar_fabric.sh", modpackDir));
            byte[] strToBytesSh = shFabric.getBytes();
            Files.write(pathSh, strToBytesSh);
            String readSh = Files.readAllLines(pathSh).get(0);
            appLogger.debug(String.format(LocalizationManager.getLocalizedString("serverutilities.log.debug.fabricshell"), readSh));
        } catch (IOException ex) {
            appLogger.error(LocalizationManager.getLocalizedString("serverutilities.log.error.fabricshell"), ex);
        }
        appLogger.info(LocalizationManager.getLocalizedString("serverutilities.log.info.fabricshell"));
    }

    /** Generates download scripts for Mojang's Minecraft server for Fabric,Windows.
     * @param modpackDir String. /server_pack The directory where the scripts will be placed in.
     * @param minecraftVersion String. The version of the Minecraft server jar to download.
     */
    private void fabricBatch(String modpackDir, String minecraftVersion) {
        try {
            String downloadMinecraftServer = (new URL(
                    LauncherMeta
                            .getLauncherMeta()
                            .getVersion(minecraftVersion)
                            .getVersionMeta()
                            .downloads
                            .get("server")
                            .url))
                    .toString();
            String batFabric = String.format("powershell -Command \"(New-Object Net.WebClient).DownloadFile('%s', 'server.jar')\"", downloadMinecraftServer);
            Path pathBat = Paths.get(String.format("%s/server_pack/download_minecraft-server.jar_fabric.bat", modpackDir));
            byte[] strToBytesBat = batFabric.getBytes();
            Files.write(pathBat, strToBytesBat);
            String readBat = Files.readAllLines(pathBat).get(0);
            appLogger.debug(String.format(LocalizationManager.getLocalizedString("serverutilities.log.debug.fabricbatch"), readBat));
        } catch (IOException ex) {
            appLogger.error(LocalizationManager.getLocalizedString("serverutilities.log.error.fabricbatch"), ex);
        }
        appLogger.info(LocalizationManager.getLocalizedString("serverutilities.log.info.fabricbatch"));
    }

    /** Generates download scripts for Mojang's Minecraft server for Forge,Linux.
     * @param modpackDir String. /server_pack The directory where the scripts will be placed in.
     * @param minecraftVersion String. The version of the Minecraft server jar to download.
     */
    private void forgeShell(String modpackDir, String minecraftVersion) {
        try {
            String downloadMinecraftServer = (new URL(
                    LauncherMeta
                            .getLauncherMeta()
                            .getVersion(minecraftVersion)
                            .getVersionMeta()
                            .downloads
                            .get("server")
                            .url))
                    .toString();
            String shForge = String.format("#!/bin/bash\n# Download the Minecraft_server.jar for your modpack\n\nwget -O minecraft_server.%s.jar %s", minecraftVersion, downloadMinecraftServer);
            Path pathSh = Paths.get(String.format("%s/server_pack/download_minecraft-server.jar_forge.sh", modpackDir));
            byte[] strToBytesSh = shForge.getBytes();
            Files.write(pathSh, strToBytesSh);
            String readSh = Files.readAllLines(pathSh).get(0);
            appLogger.debug(String.format(LocalizationManager.getLocalizedString("serverutilities.log.debug.forgeshell"), readSh));
        } catch (IOException ex) {
            appLogger.error(LocalizationManager.getLocalizedString("serverutilities.log.error.forgeshell"), ex);
        }
        appLogger.info(LocalizationManager.getLocalizedString("serverutilities.log.info.forgeshell"));
    }

    /** Generates download scripts for Mojang's Minecraft server for Forge,Windows.
     * @param modpackDir String. /server_pack The directory where the scripts will be placed in.
     * @param minecraftVersion String. The version of the Minecraft server jar to download.
     */
    private void forgeBatch(String modpackDir, String minecraftVersion) {
        try {
            String downloadMinecraftServer = (new URL(
                    LauncherMeta
                            .getLauncherMeta()
                            .getVersion(minecraftVersion)
                            .getVersionMeta()
                            .downloads
                            .get("server")
                            .url))
                    .toString();
            String batForge = String.format("powershell -Command \"(New-Object Net.WebClient).DownloadFile('%s', 'minecraft_server.%s.jar')\"", downloadMinecraftServer, minecraftVersion);
            Path pathBat = Paths.get(String.format("%s/server_pack/download_minecraft-server.jar_forge.bat", modpackDir));
            byte[] strToBytesBat = batForge.getBytes();
            Files.write(pathBat, strToBytesBat);
            String readBat = Files.readAllLines(pathBat).get(0);
            appLogger.debug(String.format(LocalizationManager.getLocalizedString("serverutilities.log.debug.forgebatch"), readBat));
        } catch (IOException ex) {
            appLogger.error(LocalizationManager.getLocalizedString("serverutilities.log.error.forgebatch"), ex);
        }
        appLogger.info(LocalizationManager.getLocalizedString("serverutilities.log.info.forgebatch"));
    }

    /** Downloads the specified version of Fabric.
     * @param modpackDir String. /server_pack The directory where the Fabric installer will be placed in.
     * @return Boolean. Returns true if the download was successful. False if not.
     */
    boolean downloadFabricJar(String modpackDir) {
        boolean downloaded = false;
        try {
            appLogger.info(LocalizationManager.getLocalizedString("serverutilities.log.info.downloadfabricjar.enter"));
            String latestFabricInstaller = latestFabricInstaller(modpackDir);
            URL downloadFabric = new URL(String.format("https://maven.fabricmc.net/net/fabricmc/fabric-installer/%s/fabric-installer-%s.jar", latestFabricInstaller, latestFabricInstaller));

            ReadableByteChannel readableByteChannel = Channels.newChannel(downloadFabric.openStream());
            FileOutputStream downloadFabricFileOutputStream = new FileOutputStream(String.format("%s/server_pack/fabric-installer.jar", modpackDir));
            FileChannel downloadFabricFileChannel = downloadFabricFileOutputStream.getChannel();
            downloadFabricFileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            downloadFabricFileOutputStream.flush();
            downloadFabricFileOutputStream.close();
            readableByteChannel.close();
            downloadFabricFileChannel.close();

        } catch (IOException e) {
            appLogger.error(LocalizationManager.getLocalizedString("serverutilities.log.error.downloadfabricjar.download"), e);
            if (new File(String.format("%s/server_pack/fabric-installer.jar", modpackDir)).exists()) {
                try {
                    Files.delete(Paths.get(String.format("%s/server_pack/fabric-installer.jar", modpackDir)));
                } catch (IOException ex) {
                    appLogger.error(LocalizationManager.getLocalizedString("serverutilities.log.error.downloadfabricjar.delete"), ex);
                }
            }
        }
        if (new File(String.format("%s/server_pack/fabric-installer.jar", modpackDir)).exists()) {
            downloaded = true;
        }
        return downloaded;
    }

    /** Returns the latest installer version for the Fabric installer to be used in ServerSetup.installServer.
     * @param modpackDir String. /server_pack The directory where the Fabric installer will be placed in.
     * @return Boolean. Returns true if the download was successful. False if not.
     */
    private String latestFabricInstaller(String modpackDir) {
        String result;
        try {
            URL downloadFabricXml = new URL("https://maven.fabricmc.net/net/fabricmc/fabric-installer/maven-metadata.xml");

            ReadableByteChannel downloadFabricXmlReadableByteChannel = Channels.newChannel(downloadFabricXml.openStream());
            FileOutputStream downloadFabricXmlFileOutputStream = new FileOutputStream(String.format("%s/server_pack/fabric-installer.xml", modpackDir));
            FileChannel downloadFabricXmlFileChannel = downloadFabricXmlFileOutputStream.getChannel();
            downloadFabricXmlFileOutputStream.getChannel().transferFrom(downloadFabricXmlReadableByteChannel, 0, Long.MAX_VALUE);

            downloadFabricXmlFileOutputStream.flush();
            downloadFabricXmlFileOutputStream.close();
            downloadFabricXmlReadableByteChannel.close();
            downloadFabricXmlFileChannel.close();

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document fabricXml = builder.parse(new File(String.format("%s/server_pack/fabric-installer.xml",modpackDir)));
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            result = (String) xpath.evaluate("/metadata/versioning/release", fabricXml, XPathConstants.STRING);
            appLogger.info(LocalizationManager.getLocalizedString("serverutilities.log.info.latestfabricinstaller"));
        } catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException ex) {
            appLogger.error(LocalizationManager.getLocalizedString("serverutilities.log.error.latestfabricinstaller"), ex);
            result = "0.7.2";
        }
        return result;
    }

    /** Downloads the specified version of the Forge installer to be used in ServerSetup.installServer.
     * @param minecraftVersion String. The Minecraft version corresponding to the Forge version. Minecraft version and Forge version build a pair.
     * @param modLoaderVersion String. The Forge version corresponding to the Minecraft version. Minecraft version and Forge version build a pair.
     * @param modpackDir String. /server_pack The directory where the Forge installer will be placed in.
     * @return Boolean. Returns true if the download was successful. False if not.
     */
    boolean downloadForgeJar(String minecraftVersion, String modLoaderVersion, String modpackDir) {
        boolean downloaded = false;
        try {
            appLogger.info(LocalizationManager.getLocalizedString("serverutilities.log.info.downloadforgejar.enter"));
            URL downloadForge = new URL(String.format("https://files.minecraftforge.net/maven/net/minecraftforge/forge/%s-%s/forge-%s-%s-installer.jar", minecraftVersion, modLoaderVersion, minecraftVersion, modLoaderVersion));

            ReadableByteChannel readableByteChannel = Channels.newChannel(downloadForge.openStream());
            FileOutputStream downloadForgeFileOutputStream = new FileOutputStream(String.format("%s/server_pack/forge-installer.jar", modpackDir));
            FileChannel downloadForgeFileChannel = downloadForgeFileOutputStream.getChannel();
            downloadForgeFileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            downloadForgeFileOutputStream.flush();
            downloadForgeFileOutputStream.close();
            readableByteChannel.close();
            downloadForgeFileChannel.close();

        } catch (IOException e) {
            appLogger.error(LocalizationManager.getLocalizedString("serverutilities.log.error.downloadforgejar.download"), e);
            if (new File(String.format("%s/server_pack/forge-installer.jar", modpackDir)).exists()) {
                if (new File(String.format("%s/server_pack/forge-installer.jar", modpackDir)).delete()) {
                    appLogger.error(LocalizationManager.getLocalizedString("serverutilities.log.debug.downloadforgejar"));
                }
            }
        }
        if (new File(String.format("%s/server_pack/forge-installer.jar", modpackDir)).exists()) {
            downloaded = true;
        }
        return downloaded;
    }

    /** Deletes Mojang's minecraft_server.jar from the zip-archive so users do not accidentally upload a file containing software from Mojang.
     * With help from https://stackoverflow.com/questions/5244963/delete-files-from-a-zip-archive-without-decompressing-in-java-or-maybe-python and https://bugs.openjdk.java.net/browse/JDK-8186227
     * @param modLoader String. Determines the name of the file to delete.
     * @param modpackDir String. /server_pack The directory in which the file will be deleted.
     */
    void deleteMinecraftJar(String modLoader, String modpackDir) {
        if (modLoader.equalsIgnoreCase("Forge")) {
            appLogger.info(LocalizationManager.getLocalizedString("serverutilities.log.info.deleteminecraftjar.enter"));

            Map<String, String> zip_properties = new HashMap<>();
            zip_properties.put("create", "false");
            zip_properties.put("encoding", "UTF-8");

            Path serverpackZip = Paths.get(String.format("%s/server_pack.zip", modpackDir));
            URI zipUri = URI.create("jar:" + serverpackZip.toUri());

            try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, zip_properties)) {
                Path pathInZipfile = zipfs.getPath("minecraft_server.1.16.5.jar");
                Files.delete(pathInZipfile);
                appLogger.info(LocalizationManager.getLocalizedString("serverutilities.log.info.deleteminecraftjar.success"));
            } catch (IOException ex) {
                appLogger.error(LocalizationManager.getLocalizedString("serverutilities.log.error.deleteminecraftjar.delete"), ex);
            }
        } else if (modLoader.equalsIgnoreCase("Fabric")) {
            appLogger.info(LocalizationManager.getLocalizedString("serverutilities.log.info.deleteminecraftjar.enter"));

            Map<String, String> zip_properties = new HashMap<>();
            zip_properties.put("create", "false");
            zip_properties.put("encoding", "UTF-8");

            Path serverpackZip = Paths.get(String.format("%s/server_pack.zip", modpackDir));
            URI zipUri = URI.create(String.format("jar:%s", serverpackZip.toUri()));

            try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, zip_properties)) {
                Path pathInZipfile = zipfs.getPath("server.jar");
                Files.delete(pathInZipfile);
                appLogger.info(LocalizationManager.getLocalizedString("serverutilities.log.info.deleteminecraftjar.success"));
            } catch (IOException ex) {
                appLogger.error(LocalizationManager.getLocalizedString("serverutilities.log.error.deleteminecraftjar.delete"), ex);
            }
        } else {
            appLogger.error(String.format(LocalizationManager.getLocalizedString("configcheck.log.error.checkmodloader"), modLoader));
        }
    }

    /** Deletes remnant files from Fabric/Forge installation no longer needed.
     * @param fabricInstaller File. Fabric installer to be deleted.
     * @param forgeInstaller File. Forge installer to be deleted.
     * @param modLoader String. Whether Forge or Fabric files are to be deleted.
     * @param modpackDir String. /server_pack The directory where files are to be deleted.
     * @param minecraftVersion String. Needed for renaming the Forge server jar to work with launch scripts provided by serverpackcreator.
     * @param modLoaderVersion String. Needed for renaming the Forge server jar to work with launch scripts provided by serverpackcreator.
     */
    void cleanUpServerPack(File fabricInstaller, File forgeInstaller, String modLoader, String modpackDir, String minecraftVersion, String modLoaderVersion) {
        appLogger.info(LocalizationManager.getLocalizedString("serverutilities.log.info.cleanupserverpack.enter"));
        if (modLoader.equalsIgnoreCase("Fabric")) {
            File fabricXML = new File(String.format("%s/server_pack/fabric-installer.xml", modpackDir));
            boolean isXmlDeleted = fabricXML.delete();
            boolean isInstallerDeleted = fabricInstaller.delete();
            if (isXmlDeleted)
            { appLogger.info(String.format(LocalizationManager.getLocalizedString("serverutilities.log.info.cleanupserverpack.deleted"), fabricXML.getName())); }
            else
            { appLogger.error(String.format(LocalizationManager.getLocalizedString("serverutilities.log.error.cleanupserverpack.delete"), fabricXML.getName())); }

            if (isInstallerDeleted)
            { appLogger.info(String.format(LocalizationManager.getLocalizedString("serverutilities.log.info.cleanupserverpack.deleted"), fabricInstaller.getName())); }
            else
            { appLogger.error(String.format(LocalizationManager.getLocalizedString("serverutilities.log.error.cleanupserverpack.delete"), fabricInstaller.getName())); }

        } else if (modLoader.equalsIgnoreCase("Forge")) {
            try {
                Files.copy(
                        Paths.get(String.format("%s/server_pack/forge-%s-%s.jar", modpackDir, minecraftVersion, modLoaderVersion)),
                        Paths.get(String.format("%s/server_pack/forge.jar", modpackDir)),
                        REPLACE_EXISTING);
                boolean isOldJarDeleted = (new File(
                        String.format("%s/server_pack/forge-%s-%s.jar", modpackDir, minecraftVersion, modLoaderVersion))).delete();
                boolean isInstallerDeleted = forgeInstaller.delete();

                if ((isOldJarDeleted) && (new File(String.format("%s/server_pack/forge.jar", modpackDir)).exists()))
                { appLogger.info(LocalizationManager.getLocalizedString("serverutilities.log.info.cleanupserverpack.rename")); }
                else
                { appLogger.error(LocalizationManager.getLocalizedString("serverutilities.log.error.cleanupserverpack.rename")); }

                if (isInstallerDeleted)
                { appLogger.info(String.format(LocalizationManager.getLocalizedString("serverutilities.log.info.cleanupserverpack.deleted"), forgeInstaller.getName())); }
                else
                { appLogger.error(String.format(LocalizationManager.getLocalizedString("serverutilities.log.error.cleanupserverpack.delete"), forgeInstaller.getName())); }

            } catch (IOException ex) {
                appLogger.error(LocalizationManager.getLocalizedString("serverutilities.log.error.cleanupserverpack"), ex);
            }
        } else {
            appLogger.error(String.format(LocalizationManager.getLocalizedString("configcheck.log.error.checkmodloader"), modLoader));
        }
    }
}