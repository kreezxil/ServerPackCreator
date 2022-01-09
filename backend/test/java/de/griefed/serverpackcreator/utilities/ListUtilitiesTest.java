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
package de.griefed.serverpackcreator.utilities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUtilitiesTest {

    private final ListUtilities LISTUTILITIES;

    ListUtilitiesTest() {
        this.LISTUTILITIES = new ListUtilities();
    }

    @Test
    void encapsulateListElementsTest() {
        List<String> clientMods = new ArrayList<>(Arrays.asList(
                "A[mbient]Sounds",
                "Back[Tools",
                "Bett[er[][]Advancement",
                "Bett   erPing",
                "cheri[ ]shed",
                "ClientT&/$weaks",
                "Control§!%(?=)ling",
                "Defau/()&=?ltOptions",
                "durabi!§/&?lity",
                "DynamicS[]urroundings",
                "itemz\\oom",
                "jei-/($?professions",
                "jeiinteg}][ration",
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
        System.out.println(LISTUTILITIES.encapsulateListElements(clientMods));
        Assertions.assertNotNull(LISTUTILITIES.encapsulateListElements(clientMods));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"A[mbient]Sounds\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"Back[Tools\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"Bett[er[][]Advancement\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"Bett   erPing\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"cheri[ ]shed\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"ClientT&/$weaks\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"Control§!%(?=)ling\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"Defau/()&=?ltOptions\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"durabi!§/&?lity\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"DynamicS[]urroundings\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"itemz/oom\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"jei-/($?professions\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"jeiinteg}][ration\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"JustEnoughResources\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"MouseTweaks\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"Neat\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"OldJavaWarning\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"PackMenu\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"preciseblockplacing\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"SimpleDiscordRichPresence\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"SpawnerFix\""));
        Assertions.assertTrue(LISTUTILITIES.encapsulateListElements(clientMods).contains("\"WorldNameRandomizer\""));
    }
}
