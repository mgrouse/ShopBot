package com.github.mgrouse.shopbot.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.DataBaseTools.DBASE;
import com.github.mgrouse.shopbot.database.Item;
import com.github.mgrouse.shopbot.database.Lot;
import com.github.mgrouse.shopbot.database.Lot.TransactionType;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;
import com.github.mgrouse.shopbot.listener.CommandHandler.AppError;


public class ItemCommandTest
{
    private static final String BROKE_DNDB = "00000000";
    private static final String DRUID_DNDB_NUM = "72792237";


    DataBaseTools dBase;

    Item item, item2, item3;

    ItemCommandTest()
    {
	dBase = DataBaseTools.getInstance();
	DataBaseTools.init(DBASE.TEST);
    }

    @BeforeEach
    void beforeEach()
    {
	dBase.deleteAllPlayers();
	dBase.deleteAllCharacters();
	dBase.deleteAllItems();
	dBase.deleteAllLots();

	// put an item in the DB
	item = new Item();

	item.setName("Shortsword");
	item.setCategory("Weapon");
	item.setBuyAmt("10.00");
	item.setSellAmt("5.00");

	item = dBase.createItem(item);

	item2 = new Item();

	item2.setName("Leather");
	item2.setCategory("Armor");
	item2.setBuyAmt("25.00");
	item2.setSellAmt("5.00");

	item2 = dBase.createItem(item2);


	item3 = new Item();

	item3.setName("Shortbow");
	item3.setCategory("Weapon");
	item3.setBuyAmt("15.00");
	item3.setSellAmt("7.50");

	item3 = dBase.createItem(item3);
    }

    @Test
    void testItemNoUser()
    {
	ItemCommandHandler handler = new ItemCommandHandler(dBase);

	// Did not set up player
	AppError err = handler.perform("Michael");

	assertEquals(AppError.NO_PLAYER, err, "performItem");
    }

    @Test
    void testItemNoActivePC()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setActiveDNDB_Id("");

	dBase.createPlayer(player);


	ItemCommandHandler handler = new ItemCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.ACT_PC_NOT_SET, err, "performItem");
    }

    @Test
    void testItemPCNotInDB()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setActiveDNDB_Id(DRUID_DNDB_NUM);

	dBase.createPlayer(player);

	// not putting PC in DB

	ItemCommandHandler handler = new ItemCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.ACT_PC_DBASE_404, err, "performItem");
    }

    @Test
    void testItemPCNoDNDB()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setActiveDNDB_Id(BROKE_DNDB);

	dBase.createPlayer(player);

	// putting character in DB with broken DNDB #
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num(BROKE_DNDB);

	dBase.createCharacter(pc);


	ItemCommandHandler handler = new ItemCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.ACT_PC_DNDB_404, err, "performItem");
    }

    @Test
    void testItemNoSellLotsFound()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setActiveDNDB_Id(DRUID_DNDB_NUM);

	dBase.createPlayer(player);

	// putting character in DB with broken DNDB #
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num(DRUID_DNDB_NUM);

	dBase.createCharacter(pc);

	// no lots for sale
	ItemCommandHandler handler = new ItemCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.ITEM_TRANSACTION_404, err, "performItem");
    }

    @Test
    void testItemPlayerStillHasItem()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setActiveDNDB_Id(DRUID_DNDB_NUM);

	player = dBase.createPlayer(player);

	// putting character in DB with broken DNDB #
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num(DRUID_DNDB_NUM);

	dBase.createCharacter(pc);

	// place sell lot in DB that Druid still has
	// Druid Had two bows and sold one and has NOT removed it
	Lot lot = new Lot(1, "Shortbow", TransactionType.SELL);

	lot.setItem(item3);
	lot.setItemId(item3.getId());
	lot.setNumOwned(1);
	lot.setPlayerId(player.getId());

	dBase.createLot(lot);


	ItemCommandHandler handler = new ItemCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.ITEM_NOT_REMOVED, err, "performItem");
    }

    @Test
    void testItemEverythingIsFineItemGone()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setActiveDNDB_Id(DRUID_DNDB_NUM);

	player = dBase.createPlayer(player);

	// putting character in DB with broken DNDB #
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num(DRUID_DNDB_NUM);

	dBase.createCharacter(pc);

	// place sell lots in DB but make it one Druid doesn't not have any of
	Lot lot = new Lot(1, "Shortsword", TransactionType.SELL);

	lot.setItem(item);
	lot.setItemId(item.getId());
	lot.setNumOwned(1);
	lot.setPlayerId(player.getId());

	dBase.createLot(lot);


	ItemCommandHandler handler = new ItemCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.NONE, err, "performItem");
    }

    @Test
    void testItemEverythingIsFineSomeItemSold()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setActiveDNDB_Id(DRUID_DNDB_NUM);

	player = dBase.createPlayer(player);

	// putting character in DB with broken DNDB #
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num(DRUID_DNDB_NUM);

	dBase.createCharacter(pc);

	// place sell lots in DB but
	// make it one Druid sold some of but still has one.

	Lot lot = new Lot(1, "Shortbow", TransactionType.SELL);

	lot.setItem(item3);
	lot.setItemId(item3.getId());
	lot.setNumOwned(2);
	lot.setPlayerId(player.getId());

	dBase.createLot(lot);


	ItemCommandHandler handler = new ItemCommandHandler(dBase);

	AppError err = handler.perform("Michael");

	assertEquals(AppError.NONE, err, "performItem");
    }
}
