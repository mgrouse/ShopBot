package com.github.mgrouse.shopbot.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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


public class SellCommandTest
{
    private static final String BROKE_DNDB = "00000000";
    private static final String RANGER_DNDB_NUM = "72792591";

    DataBaseTools dBase;

    SellCommandTest()
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
	dBase.deleteAllLots();

	// put an item in the DB
	Item item = new Item();

	item.setName("Shortsword");
	item.setCategory("Weapon");
	item.setBuyAmt("10.00");
	item.setSellAmt("5.00");

	item = dBase.createItem(item);

	Item item2 = new Item();

	item2.setName("Leather");
	item2.setCategory("Armor");
	item2.setBuyAmt("25.00");
	item2.setSellAmt("5.00");

	item2 = dBase.createItem(item2);


	Item item3 = new Item();

	item3.setName("Shortbow");
	item3.setCategory("Weapon");
	item3.setBuyAmt("15.00");
	item3.setSellAmt("7.50");

	item3 = dBase.createItem(item3);
    }

    @Test
    void testSellBadArgs()
    {
	SellCommandHandler handler = new SellCommandHandler(dBase);

	// 0 items
	Lot lot = new Lot(0, "Shortsword", TransactionType.SELL);

	AppError err = handler.perform("Michael", lot);

	assertEquals(AppError.NO_SIZE, err, "performSell");

	// bad item name
	lot = new Lot(1, "XYZ", TransactionType.SELL);

	err = handler.perform("Michael", lot);

	assertEquals(AppError.UNKNOWN_ITEM, err, "performSell");

    }

    @Test
    void testSellNoUser()
    {
	SellCommandHandler handler = new SellCommandHandler(dBase);

	Lot lot = new Lot(1, "Shortsword", TransactionType.SELL);

	// Did not set up player
	AppError err = handler.perform("Michael", lot);

	assertEquals(AppError.NO_PLAYER, err, "performSell");
    }

    @Test
    void testSellNoActivePC()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id("");

	dBase.createPlayer(player);

	Lot lot = new Lot(1, "Shortsword", TransactionType.SELL);

	SellCommandHandler handler = new SellCommandHandler(dBase);

	AppError err = handler.perform("Michael", lot);

	assertEquals(AppError.ACT_PC_NOT_SET, err, "performSell");
    }

    @Test
    void testSellPCNotInDB()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id(RANGER_DNDB_NUM);

	dBase.createPlayer(player);

	// not putting character in DB

	Lot lot = new Lot(1, "Shortsword", TransactionType.SELL);

	SellCommandHandler handler = new SellCommandHandler(dBase);

	AppError err = handler.perform("Michael", lot);

	assertEquals(AppError.ACT_PC_DBASE_404, err, "performBuy");
    }

    @Test
    void testSellPCNoDNDB()
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

	Lot lot = new Lot(1, "Shortsword", TransactionType.SELL);

	SellCommandHandler handler = new SellCommandHandler(dBase);

	AppError err = handler.perform("Michael", lot);

	assertEquals(AppError.ACT_PC_DNDB_404, err, "performBuy");

    }

    @Test
    void testSellAllInsufficientFunds()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id(RANGER_DNDB_NUM);

	dBase.createPlayer(player);

	// putting character in DB
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Ranger");
	pc.setDNDB_Num(RANGER_DNDB_NUM);

	dBase.createCharacter(pc);

	// owns 2 shortswords not 3
	Lot lot = new Lot(3, "Shortsword", TransactionType.SELL);

	SellCommandHandler handler = new SellCommandHandler(dBase);

	// performChar(String playerName, String pcName)
	AppError err = handler.perform("Michael", lot);

	assertEquals(AppError.SELL_NOT_OWNED, err, "performBuy");

	// ====

	// owns no Shortbows
	lot = new Lot(1, "Shortbow", TransactionType.SELL);

	handler = new SellCommandHandler(dBase);

	// performChar(String playerName, String pcName)
	err = handler.perform("Michael", lot);

	assertEquals(AppError.SELL_NOT_OWNED, err, "performBuy");
    }

    @Test
    void testSellAllGood()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id(RANGER_DNDB_NUM);

	dBase.createPlayer(player);

	// putting character in DB
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Ranger");
	pc.setDNDB_Num(RANGER_DNDB_NUM);

	dBase.createCharacter(pc);

	Lot lot = new Lot(2, "Shortsword", TransactionType.SELL);

	SellCommandHandler handler = new SellCommandHandler(dBase);

	// performChar(String playerName, String pcName)
	AppError err = handler.perform("Michael", lot);

	assertEquals(AppError.NONE, err, "performSell");

	// look for the Lot in the DB
	lot = dBase.readLastLot();

	// Assert
	assertNotNull(lot, "lot should be there");
	assertEquals(1, lot.getId(), "ID == 1?");
	assertEquals(1, lot.getPlayerId(), "PlayerID == 1?");
	assertEquals(1, lot.getItemId(), "ItemID == 1?");
	assertNotNull(lot.getItem(), "Item != null?");
	assertEquals(2, lot.getSize(), "Size == 2?");
	assertEquals(2, lot.getNumOwned(), "NumOwned == 2?");
	assertEquals("Shortsword", lot.getName(), "Name == Shortsword?");
	assertEquals("10.00", lot.getValue().toString(), "Value == 10.00?");
	assertEquals(TransactionType.SELL, lot.getType(), "TransactionType == Sell?");
    }

}
