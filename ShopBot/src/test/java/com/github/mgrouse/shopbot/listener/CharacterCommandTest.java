package com.github.mgrouse.shopbot.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.DataBaseTools.DBASE;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;


public class CharacterCommandTest
{
    DataBaseTools dBase;

    CharacterCommandTest()
    {
	dBase = DataBaseTools.getInstance();
	dBase.init(DBASE.TEST);
    }

    @BeforeEach
    void beforeEach()
    {
	dBase.deleteAllPlayers();
	dBase.deleteAllCharacters();
    }

    @Test
    void testCharacterCommandNoPC()
    {
	// put a player in DB

	// don't put a PC in DB

	// performChar(String playerName, String pcName)

	// read Player

	// Assert
    }

    @Test
    void testCharacterCommandWithPC()
    {
	// put a player in DB

	// Create and fill player1
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id("");


	// DBase create
	player = dBase.createPlayer(player);

	// put an PC in DB

	// Create and fill pc
	PlayerCharacter pc = new PlayerCharacter();

	// pc.setPlayerId(1);
	pc.setName("Corvus");
	pc.setDNDB_Num("12345678");
	pc.setAvatarURL("https://www.someone.com/pics/mage.png");

	// DBase create
	pc = dBase.createCharacter(pc);

	// associate them
	dBase.associatePlayerAndPC(player, pc);

	// update them
	dBase.updatePlayer(player);
	dBase.updateCharacter(pc);

	CharacterCommandHandler handler = new CharacterCommandHandler(dBase);

	// performChar(String playerName, String pcName)
	handler.performChar(player.getDiscordName(), pc.getName());

	// read and assert Player
	player = dBase.readPlayer("Michael");

	assertNotNull(player, "createPlayer");
	assertEquals(1, player.getID(), "Player1 Id ");
	assertEquals("Michael", player.getDiscordName(), "DiscordName ");
	assertEquals("12345678", player.getCurrCharDNDB_Id(), "CurrCharDNDB_Id ");

	// read and assert PC
	pc = dBase.readCharacter("12345678");

	assertNotNull(pc, "readChar() ");
	assertEquals(1, pc.getID(), "Character ID ");
	assertEquals(1, pc.getPlayerID(), "pc.getPlayerID() ");
	assertEquals("Corvus", pc.getName(), "Character name ");
	assertEquals("12345678", pc.getDNDB_Num(), "DNDB_Num ");
	assertEquals("https://www.someone.com/pics/mage.png", pc.getAvatarURL(), "Avatar URL ");


    }
}
