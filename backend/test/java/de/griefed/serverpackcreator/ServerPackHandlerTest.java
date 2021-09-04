/* Copyright (C) 2021  Griefed
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
package de.griefed.serverpackcreator;

import de.griefed.serverpackcreator.curseforge.CurseCreateModpack;
import de.griefed.serverpackcreator.i18n.LocalizationManager;
import de.griefed.serverpackcreator.utilities.VersionLister;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * <strong>Table of tests</strong><p>
 * 1. {@link #ServerPackHandlerTest()}<br>
 * 2. {@link #getPropertiesFileTest()}<br>
 * 3. {@link #getIconFileTest()}<br>
 * 4. {@link #getForgeWindowsFileTest()}<br>
 * 5. {@link #getForgeLinuxFileTest()}<br>
 * 6. {@link #getFabricWindowsFileTest()}<br>
 * 7. {@link #getFabricLinuxFileTest()}<br>
 * 8. {@link #getSTART_FABRIC_SHELLTest()}<br>
 * 9. {@link #getSTART_FABRIC_BATCHTest()}<br>
 * 10.{@link #getSTART_FORGE_SHELLTest()}<br>
 * 11.{@link #getSTART_FORGE_BATCHTest()}<br>
 * 12.{@link #getObjectMapperTest()}<br>
 * 13.{@link #runTest()}<br>
 * 14.{@link #cleanupEnvironmentTest()}<br>
 * 15.{@link #copyStartScriptsFabricTest()}<br>
 * 16.{@link #copyStartScriptsForgeTest()}<br>
 * 17.{@link #copyFilesTest()}<br>
 * 18.{@link #copyFilesEmptyClientsTest()}<br>
 * 19.{@link #excludeClientModsTest()}<br>
 * 20.{@link #copyIconTest()}<br>
 * 21.{@link #copyPropertiesTest()}<br
 * 22.{@link #installServerFabricTest()}<br>
 * 23.{@link #installServerForgeTest()}<br>
 * 24.{@link #zipBuilderFabricTest()}<br>
 * 25.{@link #zipBuilderForgeTest()}<br>
 * 26.{@link #generateDownloadScriptsFabricTest()}<br>
 * 27.{@link #generateDownloadScriptsForgeTest()}<br>
 * 28.{@link #downloadFabricJarTest()}<br>
 * 29.{@link #downloadForgeJarTest()}<br>
 * 30.{@link #cleanUpServerPackForgeTest()}<br>
 * 31.{@link #cleanUpServerPackFabricTest()}<br>
 * 32.{@link #runConfigModelTest()}
 */
class ServerPackHandlerTest {

    private final ServerPackHandler SERVERPACKHANDLER;
    private final DefaultFiles DEFAULTFILES;
    private final CurseCreateModpack CURSECREATEMODPACK;
    private final LocalizationManager LOCALIZATIONMANAGER;
    private final ConfigurationHandler CONFIGURATIONHANDLER;
    private final AddonsHandler ADDONSHANDLER;
    private final VersionLister VERSIONLISTER;
    private Properties serverPackCreatorProperties;

    ServerPackHandlerTest() {
        try {
            FileUtils.copyFile(new File("backend/main/resources/serverpackcreator.properties"),new File("serverpackcreator.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (InputStream inputStream = new FileInputStream("serverpackcreator.properties")) {
            this.serverPackCreatorProperties = new Properties();
            this.serverPackCreatorProperties.load(inputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        LOCALIZATIONMANAGER = new LocalizationManager(serverPackCreatorProperties);
        LOCALIZATIONMANAGER.init();
        DEFAULTFILES = new DefaultFiles(LOCALIZATIONMANAGER, serverPackCreatorProperties);
        VERSIONLISTER = new VersionLister(serverPackCreatorProperties);
        ADDONSHANDLER = new AddonsHandler(LOCALIZATIONMANAGER, serverPackCreatorProperties);
        CURSECREATEMODPACK = new CurseCreateModpack(LOCALIZATIONMANAGER, serverPackCreatorProperties);
        CONFIGURATIONHANDLER = new ConfigurationHandler(LOCALIZATIONMANAGER, CURSECREATEMODPACK, VERSIONLISTER, serverPackCreatorProperties);
        SERVERPACKHANDLER = new ServerPackHandler(LOCALIZATIONMANAGER, CURSECREATEMODPACK, ADDONSHANDLER, CONFIGURATIONHANDLER, serverPackCreatorProperties, VERSIONLISTER);

    }

    @Test
    void getPropertiesFileTest() {
        File file = SERVERPACKHANDLER.getPropertiesFile();
        Assertions.assertNotNull(file);
    }

    @Test
    void getIconFileTest() {
        File file = SERVERPACKHANDLER.getIconFile();
        Assertions.assertNotNull(file);
    }

    @Test
    void getForgeWindowsFileTest() {
        File file = SERVERPACKHANDLER.getForgeWindowsFile();
        Assertions.assertNotNull(file);
    }

    @Test
    void getForgeLinuxFileTest() {
        File file = SERVERPACKHANDLER.getForgeLinuxFile();
        Assertions.assertNotNull(file);
    }

    @Test
    void getFabricWindowsFileTest() {
        File file = SERVERPACKHANDLER.getFabricWindowsFile();
        Assertions.assertNotNull(file);
    }

    @Test
    void getFabricLinuxFileTest() {
        File file = SERVERPACKHANDLER.getFabricLinuxFile();
        Assertions.assertNotNull(file);
    }

    @Test
    void getSTART_FABRIC_SHELLTest() {
        String START_FABRIC_SHELL = "#!/usr/bin/env bash\njava %s -jar fabric-server-launch.jar";
        Assertions.assertEquals(START_FABRIC_SHELL, SERVERPACKHANDLER.getSTART_FABRIC_SHELL());
    }

    @Test
    void getSTART_FABRIC_BATCHTest() {
        String START_FABRIC_BATCH = "java %s -jar fabric-server-launch.jar\npause";
        Assertions.assertEquals(START_FABRIC_BATCH, SERVERPACKHANDLER.getSTART_FABRIC_BATCH());
    }

    @Test
    void getSTART_FORGE_SHELLTest() {
        String START_FORGE_SHELL  = "#!/usr/bin/env bash\njava %s -jar forge.jar --nogui";
        Assertions.assertEquals(START_FORGE_SHELL, SERVERPACKHANDLER.getSTART_FORGE_SHELL());
    }

    @Test
    void getSTART_FORGE_BATCHTest() {
        String START_FORGE_BATCH  = "java %s -jar forge.jar --nogui\npause";
        Assertions.assertEquals(START_FORGE_BATCH, SERVERPACKHANDLER.getSTART_FORGE_BATCH());
    }

    @Test
    void getObjectMapperTest() {
        Assertions.assertNotNull(SERVERPACKHANDLER.getObjectMapper());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void runTest() throws IOException {
        DEFAULTFILES.filesSetup();
        ADDONSHANDLER.initializeAddons();
        Files.copy(Paths.get("./backend/test/resources/testresources/serverpackcreator.conf"), Paths.get("serverpackcreator.conf"), REPLACE_EXISTING);
        ConfigurationModel configurationModel = new ConfigurationModel();
        SERVERPACKHANDLER.run(CONFIGURATIONHANDLER.getConfigFile(), configurationModel);
        Assertions.assertTrue(new File("server-packs/forge_tests/libraries").isDirectory());
        Assertions.assertTrue(new File("server-packs/forge_tests/config").isDirectory());
        Assertions.assertTrue(new File("server-packs/forge_tests/defaultconfigs").isDirectory());
        Assertions.assertTrue(new File("server-packs/forge_tests/mods").isDirectory());
        Assertions.assertTrue(new File("server-packs/forge_tests/scripts").isDirectory());
        Assertions.assertTrue(new File("server-packs/forge_tests/seeds").isDirectory());

        Assertions.assertTrue(new File("server-packs/forge_tests/1.16.5.json").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/minecraft_server.1.16.5.jar").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/download_minecraft-server.jar.bat").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/download_minecraft-server.jar.sh").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/forge.jar").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/server.properties").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/server-icon.png").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/start-forge.bat").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/start-forge.sh").exists());

        if (new File("server-packs/forge_tests/libraries").isDirectory()) {
            Path pathToBeDeleted = Paths.get("server-packs/forge_tests/libraries");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (new File("server-packs/forge_tests/config").isDirectory()) {
            Path pathToBeDeleted = Paths.get("server-packs/forge_tests/config");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (new File("server-packs/forge_tests/defaultconfigs").isDirectory()) {
            Path pathToBeDeleted = Paths.get("server-packs/forge_tests/defaultconfigs");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (new File("server-packs/forge_tests/mods").isDirectory()) {
            Path pathToBeDeleted = Paths.get("server-packs/forge_tests/mods");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (new File("server-packs/forge_tests/scripts").isDirectory()) {
            Path pathToBeDeleted = Paths.get("server-packs/forge_tests/scripts");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (new File("server-packs/forge_tests/seeds").isDirectory()) {
            Path pathToBeDeleted = Paths.get("server-packs/forge_tests/seeds");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (new File("server_files").isDirectory()) {
            Path pathToBeDeleted = Paths.get("server_files");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        new File("server-packs/forge_tests/1.16.5.json").delete();
        new File("server-packs/forge_tests/minecraft_server.1.16.5.jar").delete();
        new File("server-packs/forge_tests/download_minecraft-server.jar_forge.bat").delete();
        new File("server-packs/forge_tests/download_minecraft-server.jar_forge.sh").delete();
        new File("server-packs/forge_tests/forge.jar").delete();
        new File("server-packs/forge_tests/server.properties").delete();
        new File("server-packs/forge_tests/server-icon.png").delete();
        new File("server-packs/forge_tests/start-forge.bat").delete();
        new File("server-packs/forge_tests/start-forge.sh").delete();
        new File("./serverpackcreator.conf").delete();
        Files.copy(Paths.get("./backend/test/resources/testresources/server_pack.zip"), Paths.get("server-packs/forge_tests_server_pack.zip"), REPLACE_EXISTING);
    }

    @Test
    void cleanupEnvironmentTest() throws IOException {
        Files.createDirectories(Paths.get("server-packs/forge_tests"));
        String modpackDir = "server-packs/forge_tests";
        Files.copy(Paths.get("./backend/test/resources/testresources/server_pack.zip"), Paths.get("server-packs/forge_tests_server_pack.zip"), REPLACE_EXISTING);
        SERVERPACKHANDLER.cleanupEnvironment(modpackDir);
        Assertions.assertFalse(new File("\"server-packs/forge_tests_server_pack.zip\"").exists());
        Assertions.assertFalse(new File("server-packs/forge_tests").isDirectory());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void copyStartScriptsFabricTest() throws IOException {
        DEFAULTFILES.filesSetup();
        String modpackDir = "./backend/test/resources/fabric_tests";
        String destination = modpackDir.substring(modpackDir.lastIndexOf("/") + 1);
        String modLoader = "Fabric";
        DEFAULTFILES.filesSetup();
        SERVERPACKHANDLER.createStartScripts(modpackDir, modLoader, true, "zvh54u89kagsphvz8a45u3g9j");
        Assertions.assertTrue(new File(String.format("server-packs/%s/start-fabric.bat", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/start-fabric.sh", destination)).exists());
        new File(String.format("server-packs/%s/start-fabric.bat", destination)).delete();
        new File(String.format("server-packs/%s/start-fabric.sh", destination)).delete();
        String delete = "server_files";
        if (new File(delete).isDirectory()) {
            Path pathToBeDeleted = Paths.get(delete);
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        new File("./serverpackcreator.conf").delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void copyStartScriptsForgeTest() throws IOException {
        DEFAULTFILES.filesSetup();
        String modpackDir = "./backend/test/resources/forge_tests";
        String destination = modpackDir.substring(modpackDir.lastIndexOf("/") + 1);
        String modLoader = "Forge";
        DEFAULTFILES.filesSetup();
        SERVERPACKHANDLER.createStartScripts(modpackDir, modLoader, true, "zvh54u89kagsphvz8a45u3g9j");
        Assertions.assertTrue(new File(String.format("server-packs/%s/start-forge.bat", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/start-forge.sh", destination)).exists());
        new File(String.format("server-packs/%s/start-forge.bat", destination)).delete();
        new File(String.format("server-packs/%s/start-forge.sh", destination)).delete();
        String delete = "server_files";
        if (new File(delete).isDirectory()) {
            Path pathToBeDeleted = Paths.get(delete);
            Files.walk(pathToBeDeleted)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        new File("./serverpackcreator.conf").delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void copyFilesTest() throws IOException {
        String modpackDir = "./backend/test/resources/forge_tests";
        String destination = modpackDir.substring(modpackDir.lastIndexOf("/") + 1);
        List<String> clientMods = new ArrayList<>(Arrays.asList(
                "AmbientSounds",
                "BackTools",
                "BetterAdvancement",
                "BetterPing",
                "cherished",
                "ClientTweaks",
                "Controlling",
                "DefaultOptions",
                "durability",
                "DynamicSurroundings",
                "itemzoom",
                "jei-professions",
                "jeiintegration",
                "JustEnoughResources",
                "MouseTweaks",
                "Neat",
                "OldJavaWarning",
                "PackMenu",
                "preciseblockplacing",
                "SimpleDiscordRichPresence",
                "SpawnerFix",
                "TipTheScales",
                "WorldNameRandomizer"
        ));
        List<String> copyDirs = new ArrayList<>(Arrays.asList(
                "config",
                "mods",
                "scripts",
                "seeds",
                "defaultconfigs"
        ));
        SERVERPACKHANDLER.copyFiles(modpackDir, copyDirs, clientMods, "1.16.5");
        Assertions.assertTrue(new File(String.format("server-packs/%s/config", destination)).isDirectory());
        Assertions.assertTrue(new File(String.format("server-packs/%s/mods", destination)).isDirectory());
        Assertions.assertTrue(new File(String.format("server-packs/%s/scripts", destination)).isDirectory());
        Assertions.assertTrue(new File(String.format("server-packs/%s/seeds", destination)).isDirectory());
        Assertions.assertTrue(new File(String.format("server-packs/%s/defaultconfigs", destination)).isDirectory());
        Assertions.assertTrue(new File(String.format("server-packs/%s/config/testfile.txt", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/defaultconfigs/testfile.txt", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/mods/testmod.jar", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/scripts/testscript.zs", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/seeds/testjson.json", destination)).exists());
        for (String dir : copyDirs) {
            String deleteMe = (String.format("server-packs/%s/%s", destination, dir));
            if (new File(deleteMe).isDirectory()) {
                Path pathToBeDeleted = Paths.get(deleteMe);
                Files.walk(pathToBeDeleted)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void copyFilesEmptyClientsTest() throws IOException {
        String modpackDir = "./backend/test/resources/forge_tests";
        String destination = modpackDir.substring(modpackDir.lastIndexOf("/") + 1);
        List<String> clientMods = new ArrayList<>();
        List<String> copyDirs = new ArrayList<>(Arrays.asList(
                "config",
                "mods",
                "scripts",
                "seeds",
                "defaultconfigs"
        ));
        SERVERPACKHANDLER.copyFiles(modpackDir, copyDirs, clientMods, "1.16.5");
        Assertions.assertTrue(new File(String.format("server-packs/%s/config", destination)).isDirectory());
        Assertions.assertTrue(new File(String.format("server-packs/%s/mods", destination)).isDirectory());
        Assertions.assertTrue(new File(String.format("server-packs/%s/scripts", destination)).isDirectory());
        Assertions.assertTrue(new File(String.format("server-packs/%s/seeds", destination)).isDirectory());
        Assertions.assertTrue(new File(String.format("server-packs/%s/defaultconfigs", destination)).isDirectory());
        Assertions.assertTrue(new File(String.format("server-packs/%s/config/testfile.txt", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/defaultconfigs/testfile.txt", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/mods/testmod.jar", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/scripts/testscript.zs", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/seeds/testjson.json", destination)).exists());
        for (String dir : copyDirs) {
            String deleteMe = (String.format("server-packs/%s/%s", destination, dir));
            if (new File(deleteMe).isDirectory()) {
                Path pathToBeDeleted = Paths.get(deleteMe);
                Files.walk(pathToBeDeleted)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
    }

    @Test
    void excludeClientModsTest() {
        List<String> clientMods = Arrays.asList(
                "aaaaa","bbbbb","ccccc","fffff","ggggg","hhhhh","iiiii","jjjjj","kkkkk","lllll",
                "nnnnn","ppppp","qqqqq","rrrrr","uuuuu","vvvvv","wwwww","xxxxx","yyyyy"
        );
        List<String> result = SERVERPACKHANDLER.excludeClientMods("backend/test/resources/forge_tests/mods", clientMods, "1.16.5");
        System.out.println(result);
        Assertions.assertFalse(result.toString().contains("aaaaa"));
        Assertions.assertFalse(result.toString().contains("bbbbb"));
        Assertions.assertFalse(result.toString().contains("ccccc"));
        Assertions.assertFalse(result.toString().contains("fffff"));
        Assertions.assertFalse(result.toString().contains("ggggg"));
        Assertions.assertFalse(result.toString().contains("hhhhh"));
        Assertions.assertFalse(result.toString().contains("iiiii"));
        Assertions.assertFalse(result.toString().contains("jjjjj"));
        Assertions.assertFalse(result.toString().contains("kkkkk"));
        Assertions.assertFalse(result.toString().contains("lllll"));
        Assertions.assertFalse(result.toString().contains("nnnnn"));
        Assertions.assertFalse(result.toString().contains("ppppp"));
        Assertions.assertFalse(result.toString().contains("qqqqq"));
        Assertions.assertFalse(result.toString().contains("rrrrr"));
        Assertions.assertFalse(result.toString().contains("uuuuu"));
        Assertions.assertFalse(result.toString().contains("vvvvv"));
        Assertions.assertFalse(result.toString().contains("wwwww"));
        Assertions.assertFalse(result.toString().contains("xxxxx"));
        Assertions.assertFalse(result.toString().contains("yyyyy"));
        Assertions.assertTrue(result.toString().contains("zzzzz"));

        Assertions.assertTrue(result.toString().contains("testmod"));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void copyIconTest() throws IOException {
        DEFAULTFILES.filesSetup();
        String modpackDir = "./backend/test/resources/forge_tests";
        String destination = modpackDir.substring(modpackDir.lastIndexOf("/") + 1);
        SERVERPACKHANDLER.copyIcon(modpackDir);
        Assertions.assertTrue(new File(String.format("server-packs/%s/server-icon.png", destination)).exists());
        new File(String.format("server-packs/%s/server-icon.png", destination)).delete();
        String delete = "server_files";
        if (new File(delete).isDirectory()) {
            Path pathToBeDeleted = Paths.get(delete);
            Files.walk(pathToBeDeleted)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        new File("serverpackcreator.conf").delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void copyPropertiesTest() throws IOException {
        DEFAULTFILES.filesSetup();
        String modpackDir = "./backend/test/resources/forge_tests";
        String destination = modpackDir.substring(modpackDir.lastIndexOf("/") + 1);
        SERVERPACKHANDLER.copyProperties(modpackDir);
        Assertions.assertTrue(new File(String.format("server-packs/%s/server.properties", destination  )).exists());
        new File(String.format("server-packs/%s/server.properties", destination)).delete();
        String delete = "server_files";
        if (new File(delete).isDirectory()) {
            Path pathToBeDeleted = Paths.get(delete);
            Files.walk(pathToBeDeleted)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        new File("serverpackcreator.conf").delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void installServerFabricTest() throws IOException {
        Files.createDirectories(Paths.get("server-packs/fabric_tests"));
        String modLoader = "Fabric";
        String modpackDir = "./backend/test/resources/fabric_tests";
        String destination = modpackDir.substring(modpackDir.lastIndexOf("/") + 1);
        String minecraftVersion = "1.16.5";
        String modLoaderVersion = "0.11.6";
        String javaPath;
        String autoJavaPath = System.getProperty("java.home").replace("\\", "/") + "/bin/java";
        if (autoJavaPath.startsWith("C:")) {
            autoJavaPath = String.format("%s.exe", autoJavaPath);
        }
        if (new File("/usr/bin/java").exists()) {
            javaPath = "/usr/bin/java";
        } else {
            javaPath = autoJavaPath;
        }
        //CREATESERVERPACK.downloadFabricJar(modpackDir);
        SERVERPACKHANDLER.installServer(modLoader, modpackDir, minecraftVersion, modLoaderVersion, javaPath);
        Assertions.assertTrue(new File(String.format("server-packs/%s/fabric-server-launch.jar", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/server.jar", destination)).exists());
        new File(String.format("server-packs/%s/fabric-server-launch.jar", destination)).delete();
        new File(String.format("server-packs/%s/server.jar", destination)).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void installServerForgeTest() throws IOException {
        Files.createDirectories(Paths.get("server-packs/forge_tests"));
        String modLoader = "Forge";
        String modpackDir = "./backend/test/resources/forge_tests";
        String destination = modpackDir.substring(modpackDir.lastIndexOf("/") + 1);
        String minecraftVersion = "1.16.5";
        String modLoaderVersion = "36.1.2";
        String javaPath;
        String autoJavaPath = System.getProperty("java.home").replace("\\", "/") + "/bin/java";
        if (autoJavaPath.startsWith("C:")) {
            autoJavaPath = String.format("%s.exe", autoJavaPath);
        }
        if (new File("/usr/bin/java").exists()) {
            javaPath = "/usr/bin/java";
        } else {
            javaPath = autoJavaPath;
        }
        //CREATESERVERPACK.downloadForgeJar(minecraftVersion, modLoaderVersion, modpackDir);
        SERVERPACKHANDLER.installServer(modLoader, modpackDir, minecraftVersion, modLoaderVersion, javaPath);
        Assertions.assertTrue(new File(String.format("server-packs/%s/1.16.5.json", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/forge.jar", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/minecraft_server.1.16.5.jar", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/download_minecraft-server.jar.bat", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/download_minecraft-server.jar.sh", destination)).exists());
        new File(String.format("server-packs/%s/1.16.5.json", destination)).delete();
        new File(String.format("server-packs/%s/forge.jar", destination)).delete();
        new File(String.format("server-packs/%s/forge-installer.jar", destination)).delete();
        new File(String.format("server-packs/%s/forge-installer.jar.log", destination)).delete();
        new File(String.format("server-packs/%s/minecraft_server.1.16.5.jar", destination)).delete();
        new File(String.format("server-packs/%s/download_minecraft-server.jar_forge.bat", destination)).delete();
        new File(String.format("server-packs/%s/download_minecraft-server.jar_forge.sh", destination)).delete();
        String delete = String.format("server-packs/%s/libraries", destination);
        if (new File(delete).isDirectory()) {
            Path pathToBeDeleted = Paths.get(delete);
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

    @Test
    void zipBuilderFabricTest() throws IOException {
        Files.createDirectories(Paths.get("server-packs/fabric_tests"));
        String minecraftVersion = "1.16.5";
        String modpackDir = "backend/test/resources/fabric_tests";
        SERVERPACKHANDLER.zipBuilder(modpackDir, minecraftVersion, Boolean.TRUE);
        Assertions.assertTrue(new File("server-packs/fabric_tests_server_pack.zip").exists());
        Files.copy(Paths.get("./backend/test/resources/testresources/server_pack.zip"), Paths.get("./backend/test/resources/fabric_tests/server_pack.zip"), REPLACE_EXISTING);
    }

    @Test
    void zipBuilderForgeTest() throws IOException {
        Files.createDirectories(Paths.get("server-packs/forge_tests"));
        String minecraftVersion = "1.16.5";
        String modpackDir = "./backend/test/resources/forge_tests";
        SERVERPACKHANDLER.zipBuilder(modpackDir, minecraftVersion, Boolean.TRUE);
        Assertions.assertTrue(new File("server-packs/forge_tests_server_pack.zip").exists());
        Files.copy(Paths.get("./backend/test/resources/testresources/server_pack.zip"), Paths.get("./backend/test/resources/forge_tests/server_pack.zip"), REPLACE_EXISTING);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void generateDownloadScriptsFabricTest() throws IOException {
        Files.createDirectories(Paths.get("server-packs/fabric_tests"));
        String modLoader = "Fabric";
        String modpackDir = "./backend/test/resources/fabric_tests";
        String destination = modpackDir.substring(modpackDir.lastIndexOf("/") + 1);
        String minecraftVersion = "1.16.5";
        SERVERPACKHANDLER.generateDownloadScripts(modLoader, modpackDir, minecraftVersion);
        Assertions.assertTrue(new File(String.format("server-packs/%s/download_minecraft-server.jar.bat", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/download_minecraft-server.jar.sh", destination)).exists());
        new File(String.format("server-packs/%s/download_minecraft-server.jar_fabric.bat", destination)).delete();
        new File(String.format("server-packs/%s/download_minecraft-server.jar_fabric.sh", destination)).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void generateDownloadScriptsForgeTest() throws IOException {
        Files.createDirectories(Paths.get("server-packs/forge_tests"));
        String modLoader = "Forge";
        String modpackDir = "./backend/test/resources/forge_tests";
        String destination = modpackDir.substring(modpackDir.lastIndexOf("/") + 1);
        String minecraftVersion = "1.16.5";
        SERVERPACKHANDLER.generateDownloadScripts(modLoader, modpackDir, minecraftVersion);
        Assertions.assertTrue(new File(String.format("server-packs/%s/download_minecraft-server.jar.bat", destination)).exists());
        Assertions.assertTrue(new File(String.format("server-packs/%s/download_minecraft-server.jar.sh", destination)).exists());
        new File(String.format("server-packs/%s/download_minecraft-server.jar_forge.bat", destination)).delete();
        new File(String.format("server-packs/%s/download_minecraft-server.jar_forge.sh", destination)).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void downloadFabricJarTest() throws IOException {
        String modpackDir = "./backend/test/resources/fabric_tests";
        String destination = modpackDir.substring(modpackDir.lastIndexOf("/") + 1);
        Files.createDirectories(Paths.get("server-packs/fabric_tests"));
        boolean result = SERVERPACKHANDLER.downloadFabricJar(modpackDir);
        Assertions.assertTrue(result);
        Assertions.assertTrue(new File(String.format("server-packs/%s/fabric-installer.jar", destination)).exists());
        new File(String.format("server-packs/%s/fabric-installer.jar", destination)).delete();
        new File(String.format("server-packs/%s/fabric-installer.xml", destination)).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void downloadForgeJarTest() throws IOException {
        String modLoaderVersion = "36.1.2";
        String modpackDir = "./backend/test/resources/forge_tests";
        String destination = modpackDir.substring(modpackDir.lastIndexOf("/") + 1);
        Files.createDirectories(Paths.get("server-packs/forge_tests"));
        String minecraftVersion = "1.16.5";
        boolean result = SERVERPACKHANDLER.downloadForgeJar(minecraftVersion, modLoaderVersion, modpackDir);
        Assertions.assertTrue(result);
        Assertions.assertTrue(new File(String.format("server-packs/%s/forge-installer.jar", destination)).exists());
        new File(String.format("server-packs/%s/forge-installer.jar", destination)).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void cleanUpServerPackForgeTest() throws IOException {
        String modLoader = "Forge";
        String modpackDir = "./backend/test/resources/forge_tests";
        String destination = modpackDir.substring(modpackDir.lastIndexOf("/") + 1);
        String minecraftVersion = "1.16.5";
        String modLoaderVersion = "36.1.2";
        new File("server-packs/forge_tests/forge-1.16.5-36.1.2.jar").createNewFile();
        SERVERPACKHANDLER.cleanUpServerPack(
                new File(String.format("server-packs/%s/forge-installer.jar", destination)),
                new File(String.format("server-packs/%s/forge-%s-%s.jar", destination, minecraftVersion, modLoaderVersion)),
                modLoader,
                modpackDir,
                minecraftVersion,
                modLoaderVersion);
        Assertions.assertFalse(new File("server-packs/forge_tests/forge-1.16.5-36.1.2.jar").exists());
        Assertions.assertFalse(new File("server-packs/forge_tests/forge-installer.jar").exists());
    }

    @Test
    void cleanUpServerPackFabricTest() {
        String modLoader = "Fabric";
        String modpackDir = "./backend/test/resources/fabric_tests";
        String destination = modpackDir.substring(modpackDir.lastIndexOf("/") + 1);
        String minecraftVersion = "1.16.5";
        String modLoaderVersion = "36.1.2";
        SERVERPACKHANDLER.cleanUpServerPack(
                new File(String.format("server-packs/%s/fabric-installer.jar", destination)),
                new File(String.format("server-packs/%s/forge-%s-%s.jar", destination, minecraftVersion, modLoaderVersion)),
                modLoader,
                modpackDir,
                minecraftVersion,
                modLoaderVersion);
        Assertions.assertFalse(new File("server-packs/forge_tests/fabric-installer.jar").exists());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void runConfigModelTest() throws IOException {
        List<String> clientMods = new ArrayList<>(Arrays.asList(
                "AmbientSounds",
                "BackTools",
                "BetterAdvancement",
                "BetterPing",
                "cherished",
                "ClientTweaks",
                "Controlling",
                "DefaultOptions",
                "durability",
                "DynamicSurroundings",
                "itemzoom",
                "jei-professions",
                "jeiintegration",
                "JustEnoughResources",
                "MouseTweaks",
                "Neat",
                "OldJavaWarning",
                "PackMenu",
                "preciseblockplacing",
                "SimpleDiscordRichPresence",
                "SpawnerFix",
                "TipTheScales",
                "WorldNameRandomizer"
        ));
        List<String> copyDirs = new ArrayList<>(Arrays.asList(
                "config",
                "mods",
                "scripts",
                "seeds",
                "defaultconfigs"
        ));
        ConfigurationModel configurationModel = new ConfigurationModel();
        configurationModel.setModpackDir("./backend/test/resources/forge_tests");
        configurationModel.setClientMods(clientMods);
        configurationModel.setCopyDirs(copyDirs);
        configurationModel.setJavaPath("");
        configurationModel.setIncludeServerInstallation(true);
        configurationModel.setIncludeServerIcon(true);
        configurationModel.setIncludeServerProperties(true);
        configurationModel.setIncludeStartScripts(true);
        configurationModel.setIncludeZipCreation(true);
        configurationModel.setModLoader("Forge");
        configurationModel.setModLoaderVersion("36.1.2");
        configurationModel.setMinecraftVersion("1.16.5");
        configurationModel.setJavaArgs("");
        DEFAULTFILES.filesSetup();
        ADDONSHANDLER.initializeAddons();
        SERVERPACKHANDLER.run(configurationModel);
        Assertions.assertTrue(new File("server-packs/forge_tests/libraries").isDirectory());
        Assertions.assertTrue(new File("server-packs/forge_tests/config").isDirectory());
        Assertions.assertTrue(new File("server-packs/forge_tests/defaultconfigs").isDirectory());
        Assertions.assertTrue(new File("server-packs/forge_tests/mods").isDirectory());
        Assertions.assertTrue(new File("server-packs/forge_tests/scripts").isDirectory());
        Assertions.assertTrue(new File("server-packs/forge_tests/seeds").isDirectory());

        Assertions.assertTrue(new File("server-packs/forge_tests/1.16.5.json").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/minecraft_server.1.16.5.jar").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/download_minecraft-server.jar.bat").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/download_minecraft-server.jar.sh").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/forge.jar").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/server.properties").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/server-icon.png").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/start-forge.bat").exists());
        Assertions.assertTrue(new File("server-packs/forge_tests/start-forge.sh").exists());

        if (new File("server-packs/forge_tests/libraries").isDirectory()) {
            Path pathToBeDeleted = Paths.get("server-packs/forge_tests/libraries");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (new File("server-packs/forge_tests/config").isDirectory()) {
            Path pathToBeDeleted = Paths.get("server-packs/forge_tests/config");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (new File("server-packs/forge_tests/defaultconfigs").isDirectory()) {
            Path pathToBeDeleted = Paths.get("server-packs/forge_tests/defaultconfigs");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (new File("server-packs/forge_tests/mods").isDirectory()) {
            Path pathToBeDeleted = Paths.get("server-packs/forge_tests/mods");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (new File("server-packs/forge_tests/scripts").isDirectory()) {
            Path pathToBeDeleted = Paths.get("server-packs/forge_tests/scripts");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (new File("server-packs/forge_tests/seeds").isDirectory()) {
            Path pathToBeDeleted = Paths.get("server-packs/forge_tests/seeds");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        if (new File("server_files").isDirectory()) {
            Path pathToBeDeleted = Paths.get("server_files");
            Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        new File("server-packs/forge_tests/1.16.5.json").delete();
        new File("server-packs/forge_tests/minecraft_server.1.16.5.jar").delete();
        new File("server-packs/forge_tests/download_minecraft-server.jar_forge.bat").delete();
        new File("server-packs/forge_tests/download_minecraft-server.jar_forge.sh").delete();
        new File("server-packs/forge_tests/forge.jar").delete();
        new File("server-packs/forge_tests/server.properties").delete();
        new File("server-packs/forge_tests/server-icon.png").delete();
        new File("server-packs/forge_tests/start-forge.bat").delete();
        new File("server-packs/forge_tests/start-forge.sh").delete();
        new File("./serverpackcreator.conf").delete();
        Files.copy(Paths.get("./backend/test/resources/testresources/server_pack.zip"), Paths.get("server-packs/forge_tests_server_pack.zip"), REPLACE_EXISTING);
    }
}
