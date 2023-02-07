package com.github.mgrouse.shopbot.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.DataBaseTools.DBASE;
import com.github.mgrouse.shopbot.database.Item;
import com.github.mgrouse.shopbot.database.Lot;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;
import com.github.mgrouse.shopbot.listener.CommandHandler.AppError;


public class StatusCommandTest
{
    private static final String BROKE_DNDB = "00000000";
    private static final String GOBO_DNDB_NUM = "72248610";

    Item item1, item2;

    DataBaseTools dBase;

    StatusCommandTest()
    {
	dBase = DataBaseTools.getInstance();
	DataBaseTools.init(DBASE.TEST);

	dBase.deleteAllItems();

	// put an item in the DB
	item1 = new Item();

	item1.setName("Shortsword");
	item1.setCategory("Weapon");
	item1.setBuyAmt("10.00");
	item1.setSellAmt("5.00");

	item1 = dBase.createItem(item1);

	item2 = new Item();

	item2.setName("Leather");
	item2.setCategory("Armor");
	item2.setBuyAmt("25.00");
	item2.setSellAmt("5.00");

	item2 = dBase.createItem(item2);
    }

    @BeforeEach
    void beforeEach()
    {
	dBase.deleteAllPlayers();
	dBase.deleteAllCharacters();
	dBase.deleteAllLots();
    }

    @Test
    void testStatusNoPlayer()
    {
	StatusCommandHandler handler = new StatusCommandHandler(dBase);

	// Did not set up player

	AppError err = handler.performStatus("Michael");

	assertEquals(AppError.NO_PLAYER, err, "performStatus");

    }

    @Test
    void testStatusPlayer()
    {
	StatusCommandHandler handler = new StatusCommandHandler(dBase);

	// set up player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setActiveDNDB_Id("");

	player = dBase.createPlayer(player);

	// No PC Record etc.

	AppError err = handler.performStatus("Michael");

	assertEquals(AppError.NONE, err, "performStatus");
	assertEquals("You have 0 PCs in the system.\n", handler.getMessage(), "There was an err so should be '' .");
    }

    @Test
    void testStatusPlayerActPCRecordOnly()
    {
	StatusCommandHandler handler = new StatusCommandHandler(dBase);

	// set up player
	Player player = new Player();
	player.setDiscordName("Michael");
	// Set Active PC record
	player.setActiveDNDB_Id(BROKE_DNDB);

	player = dBase.createPlayer(player);

	// PC not in DB or DNDB

	AppError err = handler.performStatus("Michael");

	assertEquals(AppError.ACT_PC_DBASE_404, err, "performStatus");
    }

    @Test
    void testStatusPlayerActPCRecordInDB()
    {
	StatusCommandHandler handler = new StatusCommandHandler(dBase);

	// set up player
	Player player = new Player();
	player.setDiscordName("Michael");
	// Set Active PC record
	player.setActiveDNDB_Id(BROKE_DNDB);

	player = dBase.createPlayer(player);

	// putting character in DB with broken DNDB #
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num(BROKE_DNDB);

	pc = dBase.createCharacter(pc);

	dBase.associatePlayerAndPC(player, pc);

	// not on DNDB


	AppError err = handler.performStatus("Michael");

	assertEquals(AppError.ACT_PC_DNDB_404, err, "performStatus");
    }

    @Test
    void testStatusPlayerActPCFullNoTransaction()
    {
	StatusCommandHandler handler = new StatusCommandHandler(dBase);

	// set up player
	Player player = new Player();
	player.setDiscordName("Michael");
	// Set Active PC record
	player.setActiveDNDB_Id(GOBO_DNDB_NUM);

	player = dBase.createPlayer(player);

	// putting character in DB with broken DNDB #
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num(GOBO_DNDB_NUM);

	pc = dBase.createCharacter(pc);

	dBase.associatePlayerAndPC(player, pc);

	// GOBO_DNDB_NUM is on DNDB

	// No Transaction

	AppError err = handler.performStatus("Michael");

	assertEquals(AppError.NONE, err, "performStatus");

	assertEquals("You have 1 PCs in the system.\n" + "Corvus: (Active) Found on D&DB(72248610).\n",
		handler.getMessage(), "Should be Good.");
    }

    @Test
    void testStatusPlayerActPCFullTransactionBuy()
    {
	StatusCommandHandler handler = new StatusCommandHandler(dBase);

	// set up player
	Player player = new Player();
	player.setDiscordName("Michael");
	// Set Active PC record
	player.setActiveDNDB_Id(GOBO_DNDB_NUM);

	player = dBase.createPlayer(player);

	// putting character in DB with broken DNDB #
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num(GOBO_DNDB_NUM);

	pc = dBase.createCharacter(pc);

	dBase.associatePlayerAndPC(player, pc);

	// GOBO_DNDB_NUM is on DNDB

	// Transaction buy
	player.setCash(new BigDecimal("20.00"));
	player.setBill(new BigDecimal("10.00"));

	dBase.updatePlayer(player);

	AppError err = handler.performStatus("Michael");

	assertEquals(AppError.NONE, err, "performStatus");

	assertEquals(
		"You have 1 PCs in the system.\nCorvus: (Active) Found on D&DB(72248610).\nYou are currently in the middle of a BUY transaction:\nYou have a 'Cash Record' of:20.00 gp.\nYou have a 'Bill' of:10.00 gp.\n",
		handler.getMessage(), "Should Be Good .");
    }

    @Test
    void testStatusPlayerActPCFullTransactionSell()
    {
	StatusCommandHandler handler = new StatusCommandHandler(dBase);

	// set up player
	Player player = new Player();
	player.setDiscordName("Michael");
	// Set Active PC record
	player.setActiveDNDB_Id(GOBO_DNDB_NUM);

	player = dBase.createPlayer(player);

	// putting character in DB with broken DNDB #
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num(GOBO_DNDB_NUM);

	pc = dBase.createCharacter(pc);

	dBase.associatePlayerAndPC(player, pc);

	// GOBO_DNDB_NUM is on DNDB

	// Transaction sell
	// Create and fill a Lot

	// this is what it looks like at param checking time
	Lot lot = new Lot(2, 1, "Shortsword");

	// this is what it looks like at validation time
	lot.setItemId(item1.getId());
	lot.setItem(item1);

	lot.setPlayerId(1);

	// DBase create
	lot = dBase.createLot(lot);

	AppError err = handler.performStatus("Michael");

	assertEquals(AppError.NONE, err, "performStatus");

	assertEquals(
		"You have 1 PCs in the system.\n" + "Corvus: (Active) Found on D&DB(72248610).\n"
			+ "You are currently in the middle of a SELL transaction.\n"
			+ "Item: Shortsword, Number Owned: 2, Number to Sell: 1.\n",
		handler.getMessage(), "Should Be Good .");
    }
}
