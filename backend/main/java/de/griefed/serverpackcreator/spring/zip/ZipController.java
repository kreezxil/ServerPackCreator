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
package de.griefed.serverpackcreator.spring.zip;

import com.google.common.net.HttpHeaders;
import de.griefed.serverpackcreator.ConfigurationHandler;
import de.griefed.serverpackcreator.spring.NotificationResponse;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * RestController responsible for handling ZIP-archive uploads and server pack generation from the very same.
 * @author Griefed
 */
@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/api/v1/zip")
public class ZipController {

    private static final Logger LOG = LogManager.getLogger(ZipController.class);

    private final ZipService ZIPSERVICE;
    private final ConfigurationHandler CONFIGURATIONHANDLER;
    private final NotificationResponse NOTIFICATIONRESPONSE;

    /**
     * Constructor responsible for DI.
     * @author Griefed
     * @param injectedZipService Instance of {@link ZipService}.
     * @param injectedConfigurationHandler Instance of {@link ConfigurationHandler}.
     * @param injectedNotificationResponse Instance of {@link NotificationResponse}.
     */
    @Autowired
    public ZipController(ZipService injectedZipService, ConfigurationHandler injectedConfigurationHandler,
                         NotificationResponse injectedNotificationResponse) {

        this.ZIPSERVICE = injectedZipService;
        this.CONFIGURATIONHANDLER = injectedConfigurationHandler;
        this.NOTIFICATIONRESPONSE = injectedNotificationResponse;
    }

    /**
     * Upload a file and check whether it is a ServerPackCreator valid ZIP-archive.
     * @author Griefed
     * @param file {@link MultipartFile} The file uploaded to ServerPackCreator.
     * @return String List. A list on encountered errors, if any.
     * @throws IOException if an errors occurred saving or reading the file.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") final MultipartFile file) throws IOException {
        List<String> encounteredErrors = new ArrayList<>();

        Path pathToZip = ZIPSERVICE.saveUploadedFile(file);

        if (CONFIGURATIONHANDLER.checkZipArchive(Paths.get(pathToZip.toString().replace("\\","/")), encounteredErrors)) {

            FileUtils.deleteQuietly(new File(pathToZip.toString()));

            return ResponseEntity
                    .badRequest()
                    .header(
                            HttpHeaders.CONTENT_TYPE,
                            "application/json"
                    )
                    .body(
                            NOTIFICATIONRESPONSE.zipResponse(
                                    encounteredErrors,
                                    10000,
                                    "error",
                                    "negative",
                                    file.getOriginalFilename(),
                                    false
                            )
                    );
        }

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_TYPE,
                        "application/json"
                )
                .body(
                        NOTIFICATIONRESPONSE.zipResponse(
                                "ZIP-file checks passed. You may press Submit. :)",
                                5000,
                                "info",
                                "positive",
                                pathToZip.toFile().getName(),
                                true
                        )
                );
    }

    /**
     * Request the generation of a server pack from a previously uploaded ZIP-archive, which passed validation checks,
     * and from a barebones configuration, including:<br>
     * <code>clientMods</code><br>
     * <code>minecraftVersion</code><br>
     * <code>modLoader</code><br>
     * <code>modLoaderVersion</code><br>
     * <code>installModloaderServer</code>
     * @author Griefed
     * @param zipName {@link String} The name of the previously uploaded ZIP-archive.
     * @param clientMods {@link String} A comma separated list of clientside-only mods to exclude from the server pack.
     * @param minecraftVersion {@link String} The Minecraft version the modpack, and therefor the server pack, uses.
     * @param modLoader {@link String} The modloader the modpack, and therefor the server pack, uses.
     * @param modLoaderVersion {@link String} The modloader version the modpack, and therefor the server pack, uses.
     * @param installModloaderServer {@link Boolean} Whether to install the modloader server in the server pack.
     * @return {@link NotificationResponse} with information about the result.
     */
    @GetMapping("/{zipName}&{clientMods}&{minecraftVersion}&{modLoader}&{modLoaderVersion}&{installModloaderServer}")
    public ResponseEntity<String> requestGenerationFromZip(
            @PathVariable("zipName") String zipName,
            @PathVariable("clientMods") String clientMods,
            @PathVariable("minecraftVersion") String minecraftVersion,
            @PathVariable("modLoader") String modLoader,
            @PathVariable("modLoaderVersion") String modLoaderVersion,
            @PathVariable("installModloaderServer") boolean installModloaderServer
            ) {

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_TYPE,
                        "application/json"
        ).body(
                ZIPSERVICE.submitGenerationTask(
                        zipName + "&" +
                                clientMods + "&" +
                                minecraftVersion + "&" +
                                modLoader + "&" +
                                modLoaderVersion + "&" +
                                installModloaderServer
                )
                );

    }
}