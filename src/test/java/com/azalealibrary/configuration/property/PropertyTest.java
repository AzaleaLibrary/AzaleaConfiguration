package com.azalealibrary.configuration.property;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropertyTest {

    Property<Integer> aNumber;
    Property<String> aString;
    Property<Player> aPlayer;

    Player player;

    @BeforeEach
    void setUp() {
        ServerMock server = MockBukkit.mock();
        player = server.addPlayer();

        aNumber = new Property<>(PropertyType.INTEGER, 10, "aNumber", "", true, AssignmentPolicy.create(i -> i > 0));
        aString = new Property<>(PropertyType.STRING, "text", "aString", "", true, AssignmentPolicy.create(String::isEmpty));
        aPlayer = new Property<>(PropertyType.PLAYER, null, "aPlayer", "", false);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void onExecute() {
    }

    @Test
    void onSuggest() {
    }

    @Test
    void serialize() {
    }

    @Test
    void deserialize() {
    }
}