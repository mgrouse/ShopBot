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


public class AbortCommandTest
{
    DataBaseTools dBase;

    AbortCommandTest()
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
    void testAbortNoPlayer()
    {
	// NO Player in DB

	AbortCommandHandler aHandler = new AbortCommandHandler(dBase);

	AppError err = aHandler.performAbort("Michael");

	assertEquals(AppError.NO_PLAYER, err, " No Player");
    }

    @Test
    void testAbortNoPC()
    {
	// put a player in the DB
	Player p = new Player();
	p.setDiscordName("Michael");
	p.setActiveDNDB_Id("");
	p.setCash(new BigDecimal("20.00"));
	p.setBill(new BigDecimal("10.00"));

	dBase.createPlayer(p);

	// no PC in the DB

	AbortCommandHandler aHandler = new AbortCommandHandler(dBase);

	AppError err = aHandler.performAbort("Michael");

	assertEquals(AppError.NONE, err, " No PC");
    }


    void testAbortNoTransaction()
    {
	// put a player in the DB
	Player p = new Player();
	p.setDiscordName("Michael");
	p.setActiveDNDB_Id("12345678");

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
	AbortCommandHandler aHandler = new AbortCommandHandler(dBase);

	AppError err = aHandler.performAbort("Michael");

	assertEquals(AppError.GOLD_NO_BILL, err, " No PC");
    }


    @Test
    void testRemoveAllGood()
    {
	// put a player in the DB
	Player p = new Player();
	p.setDiscordName("Michael");
	p.setActiveDNDB_Id("12345678");
	p.setCash(new BigDecimal("20.00"));
	p.setBill(new BigDecimal("10.00"));

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
	AbortCommandHandler aHandler = new AbortCommandHandler(dBase);

	AppError err = aHandler.performAbort("Michael");

	assertEquals(AppError.NONE, err, " No PC");

	// get the player
	p = dBase.readPlayer("Michael");

	// assert player Cash and Bill are cleared
	assertEquals("0.00", p.getBill().toString(), "Cleared Bill");
	assertEquals("0.00", p.getCash().toString(), "Cleared Cash");
    }
}
