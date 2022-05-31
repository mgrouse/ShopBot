package com.github.mgrouse.shopbot.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.DataBaseTools.DBASE;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;
import com.github.mgrouse.shopbot.listener.RemoveCommandHandler.RemoveError;


public class RemoveCommandTest
{
    DataBaseTools dBase;

    RemoveCommandTest()
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
    void testRemoveNoPlayer()
    {
	// NO Player in DB

	RemoveCommandHandler rHandler = new RemoveCommandHandler(dBase);

	RemoveError err = rHandler.performRemove("Michael", "Corvus");

	assertEquals(RemoveError.NO_PLAYER, err, " No Player");
    }

    @Test
    void testRemoveNoPC()
    {
	// put a player in the DB
	Player p = new Player();
	p.setDiscordName("Michael");
	p.setCurrCharDNDB_Id("");

	dBase.createPlayer(p);


	RemoveCommandHandler rHandler = new RemoveCommandHandler(dBase);

	RemoveError err = rHandler.performRemove("Michael", "Corvus");

	assertEquals(RemoveError.NO_PC_DB, err, " No PC");
    }

    @Test
    void testRemoveAllGood()
    {
	// put a player in the DB
	Player p = new Player();
	p.setDiscordName("Michael");
	p.setCurrCharDNDB_Id("12345678");

	p = dBase.createPlayer(p);

	// put a PC in the DB
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num("12345678");

	pc = dBase.createCharacter(pc);

	// Associate them
	dBase.associatePlayerAndPC(p, pc);

	// Update them
	dBase.updatePlayer(p);
	dBase.updateCharacter(pc);

	// test
	RemoveCommandHandler rHandler = new RemoveCommandHandler(dBase);

	RemoveError err = rHandler.performRemove("Michael", "Corvus");

	// assert results of remove
	assertEquals(RemoveError.NONE, err, " All Good");

	// get the player
	p = dBase.readPlayer("Michael");

	// assert player Curr PC is cleared
	assertEquals("", p.getCurrCharDNDB_Id(), "Cleared Curr Char DNB");
    }
}
