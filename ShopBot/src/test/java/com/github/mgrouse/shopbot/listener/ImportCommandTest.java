package com.github.mgrouse.shopbot.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.DataBaseTools.DBASE;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;


public class ImportCommandTest
{
    DataBaseTools dBase;

    ImportCommandTest()
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
    void testImportNoUser()
    {
	ImportCommandHandler handler = new ImportCommandHandler(dBase);

	// do the Import
	handler.performImport("GoldenScarab", "72248610");

	// look for the User
	Player p = dBase.readPlayer("GoldenScarab");

	assertNotNull(p);
	assertEquals(1, p.getID());
	assertEquals("", p.getCurrCharDNDB_Id());
	assertEquals(false, p.getIsInTransaction());

	// look for the character
	PlayerCharacter pc = dBase.readCharacter("72248610");

	assertNotNull(pc);
	assertEquals(1, pc.getID());
	assertEquals(1, pc.getPlayerID());
	String url = "https://www.dndbeyond.com/avatars/18/2/636378960438136837.jpeg?width=150&height=150&fit=crop&quality=95&auto=webp";
	assertEquals(url, pc.getAvatarURL());
	assertEquals("Gobo", pc.getName());
    }

    @Test
    void testImportWithUser()
    {
	// place a user in the DB
	Player p = new Player();
	p.setDiscordName("GoldenScarab");
	p.setCurrCharDNDB_Id("");
	p.setIsInTransaction(false);
	dBase.createPlayer(p);

	ImportCommandHandler handler = new ImportCommandHandler(dBase);

	// do the Import
	handler.performImport("GoldenScarab", "72248610");

	// look for the User
	p = dBase.readPlayer("GoldenScarab");

	assertNotNull(p);
	assertEquals(1, p.getID());
	assertEquals("", p.getCurrCharDNDB_Id());
	assertEquals(false, p.getIsInTransaction());

	// look for the character
	PlayerCharacter pc = dBase.readCharacter("72248610");

	assertNotNull(pc);
	assertEquals(1, pc.getID());
	assertEquals(1, pc.getPlayerID());
	String url = "https://www.dndbeyond.com/avatars/18/2/636378960438136837.jpeg?width=150&height=150&fit=crop&quality=95&auto=webp";
	assertEquals(url, pc.getAvatarURL());
	assertEquals("Gobo", pc.getName());
    }

}
