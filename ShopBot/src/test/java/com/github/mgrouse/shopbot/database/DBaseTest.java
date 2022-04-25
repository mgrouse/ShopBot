package com.github.mgrouse.shopbot.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.DataBaseTools.DBASE;


class DBaseTest
{
    @Test
    void playerCRUD()
    {
	DataBaseTools dBase = DataBaseTools.getInstance();

	dBase.init(DBASE.TEST);

	dBase.deleteAllPlayers();


	// Create and fill player1
	Player player1 = new Player();
	player1.setDiscordName("Michael");
	player1.setCurrCharDNDB_Id("12345678");
	player1.setIsInTransaction(false);

	// Attempt to create null
	Player temp = dBase.createPlayer(null);
	assertNull(temp);

	// DBase create
	player1 = dBase.createPlayer(player1);

	assertNotNull(player1, "createPlayer ");
	assertEquals(1, player1.getID(), "Player1 Id ");
	assertEquals("Michael", player1.getDiscordName(), "DiscordName ");
	assertEquals("12345678", player1.getCurrCharDNDB_Id(), "CurrCharDNDB_Id ");
	assertEquals(false, player1.getIsInTransaction(), "player1.inTransaction ");

	// attempt to read null or empty
	temp = dBase.readPlayer(null);
	assertNull(temp, "read Null");

	temp = dBase.readPlayer("");
	assertNull(temp, "read '' ");

	// DBase read
	Player player2 = dBase.readPlayer("NotPresent");

	assertNull(player2, "Not Present");

	player2 = dBase.readPlayer("Michael");
	assertNotNull(player2, "readPlayer player2 ");
	assertEquals(1, player2.getID(), "Player2 Id ");
	assertEquals("Michael", player2.getDiscordName(), "DiscordName ");
	assertEquals("12345678", player2.getCurrCharDNDB_Id(), "CurrCharDNDB_Id ");
	assertEquals(false, player2.getIsInTransaction(), "player2.inTransaction ");

	// modify player1
	player1.setDiscordName("Grouse");
	player1.setCurrCharDNDB_Id("87654321");
	player1.setIsInTransaction(true);

	// attempt to update null
	Boolean success = dBase.updatePlayer(null);
	assertFalse(success, "Update Player?");

	// DBase update player1
	success = dBase.updatePlayer(player1);
	assertTrue(success, "Update Player?");

	// DBase Read
	player1 = dBase.readPlayer("Grouse");

	assertNotNull(player1, "createPlayer shouuld return an object ");
	assertEquals(1, player1.getID(), "Player1 Id ");
	assertEquals("Grouse", player1.getDiscordName(), "DiscordName ");
	assertEquals("87654321", player1.getCurrCharDNDB_Id(), "CurrCharDNDB_Id ");
	assertEquals(true, player1.getIsInTransaction(), "player1.inTransaction ");

	// attempt to delete null
	dBase.destroyPlayer(null);

	// DBase delete player1
	dBase.destroyPlayer(player1);

	// DBase Read
	player1 = dBase.readPlayer("Grouse");

	// assert empty
	assertNull(player1, "Read after Destroy ");

	dBase.close();
    }


    @Test
    void CharacterCRUD()
    {
	DataBaseTools dBase = DataBaseTools.getInstance();

	dBase.init(DBASE.TEST);

	dBase.deleteAllCharacters();

	// Create and fill character1
	PlayerCharacter char1 = new PlayerCharacter();
	// char1.setID(0);
	char1.setPlayerId(2);
	char1.setName("Corvus");
	char1.setDNDB_Num("12345678");
	char1.setAvatarURL("https://www.someone.com/pics/mage.png");

	// attempt to create null
	PlayerCharacter cTemp = dBase.createCharacter(null);

	assertNull(cTemp, "Create Character(null) ");

	// DBase create
	char1 = dBase.createCharacter(char1);

	assertNotNull(char1, "Create a Character ");
	assertEquals(1, char1.getID(), "Character ID ");
	assertEquals(2, char1.getPlayerID(), "Char1.getPlayerID() ");
	assertEquals("Corvus", char1.getName(), "Character name ");
	assertEquals("12345678", char1.getDNDB_Num(), "DNDB_Num ");
	assertEquals("https://www.someone.com/pics/mage.png", char1.getAvatarURL(), "Avatar URL ");

	// DBase read
	PlayerCharacter char2 = dBase.readCharacter("12345678");

	assertNotNull(char2, "readChar() ");

	// assert equal to char 1 values
	assertEquals(1, char2.getID(), "Character ID ");
	assertEquals(2, char2.getPlayerID(), "Char1.getPlayerID() ");
	assertEquals("Corvus", char2.getName(), "Character name ");
	assertEquals("12345678", char2.getDNDB_Num(), "DNDB_Num ");
	assertEquals("https://www.someone.com/pics/mage.png", char2.getAvatarURL(), "Avatar URL ");

	// modify Character1
	char1.setName("SilverMoon");
	char1.setAvatarURL("https://some really long URL ok this isn't really all that long");

	// attempt to update null
	Boolean success = dBase.updatePlayer(null);
	assertFalse(success, "Update Null?");

	// DBase update Character1
	success = dBase.updateCharacter(char1);
	assertTrue(success, "Update Character? ");

	// DBase Read
	char1 = dBase.readCharacter("12345678");

	// assert equal to Character1
	assertEquals("SilverMoon", char1.getName());
	String sTemp = "https://some really long URL ok this isn't really all that long";
	assertEquals(sTemp, char1.getAvatarURL());

	// DBase delete Character1
	success = dBase.destroyCharacter(char1);
	assertTrue(success, "Delete Character? ");

	// DBase Read Character1
	char1 = dBase.readCharacter("12345678");

	// assert empty
	assertNull(char1, "Read Deleted");

	dBase.close();
    }

    @Test
    void testGetPC()
    {
	DataBaseTools dBase = DataBaseTools.getInstance();

	dBase.init(DBASE.TEST);

	dBase.deleteAllPlayers();
	dBase.deleteAllCharacters();

	// Create and fill player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id("87654321");
	player.setIsInTransaction(false);

	// Create and fill character1
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num("12345678");
	pc.setAvatarURL("https://www.someone.com/pics/mage.png");

	// save them
	player = dBase.createPlayer(player);
	pc = dBase.createCharacter(pc);

	// associate them
	dBase.associatePlayerAndPC(player, pc);

	// update them
	dBase.updatePlayer(player);
	dBase.updateCharacter(pc);

	// read and check Player
	player = dBase.readPlayer("Michael");
	assertNotNull(player, "Create a Player ");
	assertEquals(1, player.getID(), "Player Id ");
	assertEquals("Michael", player.getDiscordName(), "DiscordName ");
	assertEquals("87654321", player.getCurrCharDNDB_Id(), "CurrCharDNDB_Id ");
	assertEquals(false, player.getIsInTransaction(), "player1.inTransaction ");

	// read and check pc
	assertNotNull(pc, "Create a Character ");
	assertEquals(1, pc.getID(), "Character ID ");
	assertEquals(1, pc.getPlayerID(), "pc.getPlayerID() ");
	assertEquals("Corvus", pc.getName(), "Character name ");
	assertEquals("12345678", pc.getDNDB_Num(), "DNDB_Num ");
	assertEquals("https://www.someone.com/pics/mage.png", pc.getAvatarURL(), "Avatar URL ");

    }
}
