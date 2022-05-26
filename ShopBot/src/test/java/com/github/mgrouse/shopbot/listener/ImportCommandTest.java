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
    private static final String GOBO_DNDB_NUM = "72248610";
    private static final String GOBO_AVATAT_URL = "https://www.dndbeyond.com/avatars/18/2/636378960438136837.jpeg?width=150&height=150&fit=crop&quality=95&auto=webp";

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
    void testImportNoDNDB()
    {
	ImportCommandHandler handler = new ImportCommandHandler(dBase);

	// do the Import
	ImportError err = handler.performImport("GoldenScarab", "00000000");
	assertEquals(err, ImportError.NO_PC_404, "performImport");
    }

    @Test
    void testImportNoUser()
    {
	ImportCommandHandler handler = new ImportCommandHandler(dBase);

	// do the Import
	ImportError err = handler.performImport("GoldenScarab", GOBO_DNDB_NUM);
	assertEquals(err, ImportError.NONE, "performImport");

	// look for the User
	Player p = dBase.readPlayer("GoldenScarab");

	assertNotNull(p);
	assertEquals(1, p.getID());
	assertEquals("", p.getCurrCharDNDB_Id());


	// look for the character
	PlayerCharacter pc = dBase.readCharacter(GOBO_DNDB_NUM);

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

	dBase.createPlayer(p);

	ImportCommandHandler handler = new ImportCommandHandler(dBase);

	// do the Import
	ImportError err = handler.performImport("GoldenScarab", GOBO_DNDB_NUM);
	assertEquals(err, ImportError.NONE, "performImport");

	// look for the User
	p = dBase.readPlayer("GoldenScarab");

	assertNotNull(p);
	assertEquals(1, p.getID());
	assertEquals("", p.getCurrCharDNDB_Id());


	// look for the character
	PlayerCharacter pc = dBase.readCharacter(GOBO_DNDB_NUM);

	assertNotNull(pc);
	assertEquals(1, pc.getID());
	assertEquals(1, pc.getPlayerID());
	assertEquals(GOBO_AVATAT_URL, pc.getAvatarURL());
	assertEquals("Gobo", pc.getName());
    }

}
