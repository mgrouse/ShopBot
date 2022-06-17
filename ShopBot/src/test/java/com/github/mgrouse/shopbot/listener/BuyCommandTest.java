package com.github.mgrouse.shopbot.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.DataBaseTools.DBASE;
import com.github.mgrouse.shopbot.database.Item;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;
import com.github.mgrouse.shopbot.listener.CommandHandler.AppError;


public class BuyCommandTest
{
    private static final String BROKE_DNDB = "00000000";
    private static final String GOBO_DNDB_NUM = "72248610";
    private static final String GOBO_AVATAT_URL = "https://www.dndbeyond.com/avatars/18/2/636378960438136837.jpeg?width=150&height=150&fit=crop&quality=95&auto=webp";

    DataBaseTools dBase;

    BuyCommandTest()
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
    }

    @Test
    void testBuyBadArgs()
    {
	BuyCommandHandler handler = new BuyCommandHandler(dBase);

	// 0 items
	AppError err = handler.perform("Michael", 0, "Shortsword");

	assertEquals(AppError.NO_SIZE, err, "performBuy");

	// bad item name
	err = handler.perform("Michael", 1, "XYZ");

	assertEquals(AppError.UNKNOWN_ITEM, err, "performBuy");
    }

    @Test
    void testBuyNoUser()
    {
	BuyCommandHandler handler = new BuyCommandHandler(dBase);

	// Did not set up player
	AppError err = handler.perform("Michael", 1, "Shortsword");

	assertEquals(AppError.NO_PLAYER, err, "performBuy");
    }

    @Test
    void testBuyNoActivePC()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id("");

	dBase.createPlayer(player);

	BuyCommandHandler handler = new BuyCommandHandler(dBase);

	AppError err = handler.perform("Michael", 1, "Shortsword");

	assertEquals(AppError.ACT_PC_NOT_SET, err, "performBuy");
    }

    @Test
    void testBuyPCNotInDB()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id(GOBO_DNDB_NUM);

	dBase.createPlayer(player);

	// not putting character in DB

	BuyCommandHandler handler = new BuyCommandHandler(dBase);

	AppError err = handler.perform("Michael", 1, "Shortsword");

	assertEquals(AppError.ACT_PC_DBASE_404, err, "performBuy");

    }

    @Test
    void testBuyPCNoDNDB()
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


	BuyCommandHandler handler = new BuyCommandHandler(dBase);

	AppError err = handler.perform("Michael", 1, "Shortsword");

	assertEquals(AppError.ACT_PC_DNDB_404, err, "performBuy");

    }

    @Test
    void testBuyAllInsufficientFunds()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id(GOBO_DNDB_NUM);

	dBase.createPlayer(player);

	// putting character in DB
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Gobo");
	pc.setDNDB_Num(GOBO_DNDB_NUM);

	dBase.createCharacter(pc);


	BuyCommandHandler handler = new BuyCommandHandler(dBase);

	// performChar(String playerName, String pcName)
	AppError err = handler.perform("Michael", 1, "Leather");

	assertEquals(AppError.BUY_INSUFFICIENT_FUNDS, err, "performBuy");
    }

    @Test
    void testBuyAllGood()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id(GOBO_DNDB_NUM);

	dBase.createPlayer(player);

	// putting character in DB
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Gobo");
	pc.setDNDB_Num(GOBO_DNDB_NUM);

	dBase.createCharacter(pc);


	BuyCommandHandler handler = new BuyCommandHandler(dBase);

	// performChar(String playerName, String pcName)
	AppError err = handler.perform("Michael", 1, "Shortsword");

	assertEquals(AppError.NONE, err, "performBuy");

	// look for the Cash/Bill that we expect
	player = dBase.readPlayer("Michael");

	assertEquals("20.00", player.getCash().toString(), "Cash");
	assertEquals("10.00", player.getBill().toString(), "Bill");

	// next version, look for Lot(s)
    }
}
