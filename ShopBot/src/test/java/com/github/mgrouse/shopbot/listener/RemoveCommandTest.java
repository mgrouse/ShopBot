package com.github.mgrouse.shopbot.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.DataBaseTools.DBASE;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;
import com.github.mgrouse.shopbot.listener.CommandHandler.AppError;


public class RemoveCommandTest
{
    DataBaseTools dBase;

    RemoveCommandTest()
    {
	dBase = DataBaseTools.getInstance();
	DataBaseTools.init(DBASE.TEST);
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

	AppError err = rHandler.performRemove("Michael", "Corvus");

	assertEquals(AppError.NO_PLAYER, err, " No Player");
    }

    @Test
    void testRemoveNoPC()
    {
	// put a player in the DB
	Player p = new Player();
	p.setDiscordName("Michael");
	p.setActiveDNDB_Id("");

	dBase.createPlayer(p);

	// NO PC

	RemoveCommandHandler rHandler = new RemoveCommandHandler(dBase);

	AppError err = rHandler.performRemove("Michael", "Corvus");

	assertEquals(AppError.NO_PC, err, " No PC");
    }

    @Test
    void testRemoveAllGoodNotActivePC()
    {
	// put a player in the DB
	Player p = new Player();
	p.setDiscordName("Michael");
	p.setActiveDNDB_Id("12345678");
	p.setBill(new BigDecimal("20.00"));
	p.setCash(new BigDecimal("10.00"));

	p = dBase.createPlayer(p);

	// put a PC in the DB
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num("87654321");

	pc = dBase.createCharacter(pc);

	// Associate them
	dBase.associatePlayerAndPC(p, pc);

	// Update them
	dBase.updatePlayer(p);
	dBase.updateCharacter(pc);

	// test
	RemoveCommandHandler rHandler = new RemoveCommandHandler(dBase);

	AppError err = rHandler.performRemove("Michael", "Corvus");

	// assert results of remove
	assertEquals(AppError.NONE, err, " All Good");

	// get the player
	p = dBase.readPlayer("Michael");

	// assert player Curr PC is NOT cleared
	assertEquals("12345678", p.getActiveDNDB_Id(), "Cleared Curr Char DNB");

	// assert there is still a bill
	assertEquals("20.00", p.getBill().toString(), "Bill");
	assertEquals("10.00", p.getCash().toString(), "Cash");
    }

    @Test
    void testRemoveAllGoodActiveWithTransaction()
    {
	// put a player in the DB
	Player p = new Player();
	p.setDiscordName("Michael");
	p.setActiveDNDB_Id("12345678");
	p.setBill(new BigDecimal("20.00"));
	p.setCash(new BigDecimal("10.00"));

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

	AppError err = rHandler.performRemove("Michael", "Corvus");

	// assert results of remove
	assertEquals(AppError.NONE, err, " All Good");

	// get the player
	p = dBase.readPlayer("Michael");

	// assert player Curr PC is cleared
	assertEquals("", p.getActiveDNDB_Id(), "Cleared Curr Char DNB");

	// assert there is no bill
	assertEquals("0.00", p.getBill().toString(), "Bill");
	assertEquals("0.00", p.getCash().toString(), "Cash");
    }
}
