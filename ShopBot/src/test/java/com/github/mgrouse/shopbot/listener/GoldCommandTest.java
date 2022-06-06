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


public class GoldCommandTest
{
    private static final String BROKE_DNDB = "00000000";
    private static final String GOBO_DNDB_NUM = "72248610";
    private static final String GOBO_AVATAT_URL = "https://www.dndbeyond.com/avatars/18/2/636378960438136837.jpeg?width=150&height=150&fit=crop&quality=95&auto=webp";

    DataBaseTools dBase;

    GoldCommandTest()
    {
	dBase = DataBaseTools.getInstance();
	DataBaseTools.init(DBASE.TEST);

	// Place Items in DB
    }

    @BeforeEach
    void beforeEach()
    {
	dBase.deleteAllPlayers();
	dBase.deleteAllCharacters();
	dBase.deleteAllItems();
    }

    @Test
    void testGoldNoUser()
    {
	GoldCommandHandler handler = new GoldCommandHandler(dBase);

	// Did not set up player
	AppError err = handler.perform("Michael");

	assertEquals(AppError.NO_PLAYER, err, "performBuy");
    }

    @Test
    void testGoldNoActivePC()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id("");

	dBase.createPlayer(player);


	GoldCommandHandler handler = new GoldCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.ACT_PC_NOT_SET, err, "performBuy");
    }

    @Test
    void testGoldPCNotInDB()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id(GOBO_DNDB_NUM);

	dBase.createPlayer(player);

	// not putting PC in DB

	GoldCommandHandler handler = new GoldCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.ACT_PC_DBASE_404, err, "performBuy");
    }

    @Test
    void testGoldPCNoDNDB()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id(BROKE_DNDB);

	dBase.createPlayer(player);

	// putting character in DB with broken DNDB #
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num(BROKE_DNDB);

	dBase.createCharacter(pc);


	GoldCommandHandler handler = new GoldCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.ACT_PC_DNDB_404, err, "performBuy");
    }

    @Test
    void testGoldNoBill()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id(GOBO_DNDB_NUM);
	// no bill

	dBase.createPlayer(player);


	// putting character in DB
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Gobo");
	pc.setDNDB_Num(GOBO_DNDB_NUM);

	dBase.createCharacter(pc);


	GoldCommandHandler handler = new GoldCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.GOLD_NO_BILL, err, "performBuy");
    }

    @Test
    void testGoldNoCashRecord()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id(GOBO_DNDB_NUM);
	player.setBill(new BigDecimal("10.00"));
	// no cash

	dBase.createPlayer(player);


	// putting character in DB
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Gobo");
	pc.setDNDB_Num(GOBO_DNDB_NUM);

	dBase.createCharacter(pc);


	GoldCommandHandler handler = new GoldCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.GOLD_NO_CASH, err, "performBuy");
    }

    @Test
    void testGoldUnderpayment()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id(GOBO_DNDB_NUM);
	player.setCash(new BigDecimal("20.00"));
	player.setBill(new BigDecimal("12.00"));
	// =======================================
	// According to DB Gobo should have 8

	// Gobo has 10.00 in pocket, looks like he under payed.

	dBase.createPlayer(player);


	// putting character in DB
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Gobo");
	pc.setDNDB_Num(GOBO_DNDB_NUM);

	dBase.createCharacter(pc);


	GoldCommandHandler handler = new GoldCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.GOLD_UNDER_PAYED, err, "performBuy");
    }

    @Test
    void testGoldOverPayment()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id(GOBO_DNDB_NUM);
	player.setCash(new BigDecimal("20.00"));
	player.setBill(new BigDecimal("8.00"));
	// =======================================
	// According to DB Gobo should have 12

	// Gobo has 10.00 in pocket, looks like he over payed.

	dBase.createPlayer(player);


	// putting character in DB
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Gobo");
	pc.setDNDB_Num(GOBO_DNDB_NUM);

	dBase.createCharacter(pc);


	GoldCommandHandler handler = new GoldCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.GOLD_OVER_PAYED, err, "performBuy");
    }

    @Test
    void testGoldPayment()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id(GOBO_DNDB_NUM);
	player.setCash(new BigDecimal("20.00"));
	player.setBill(new BigDecimal("10.00"));
	// ========
	// 10.00

	// Gobo has 10.00 in pocket

	dBase.createPlayer(player);


	// putting character in DB with broken DNDB #
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Gobo");
	pc.setDNDB_Num(GOBO_DNDB_NUM);

	dBase.createCharacter(pc);


	GoldCommandHandler handler = new GoldCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.NONE, err, "performBuy");
    }

}
