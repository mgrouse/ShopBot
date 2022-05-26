package com.github.mgrouse.shopbot.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.DataBaseTools.DBASE;
import com.github.mgrouse.shopbot.database.Item;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;
import com.github.mgrouse.shopbot.listener.BuyCommandHandler.BuyError;


public class BuyCommandTest
{
    private static final String BROKE_DNDB = "00000000";
    private static final String GOBO_DNDB_NUM = "72248610";
    private static final String GOBO_AVATAT_URL = "https://www.dndbeyond.com/avatars/18/2/636378960438136837.jpeg?width=150&height=150&fit=crop&quality=95&auto=webp";

    DataBaseTools dBase;

    BuyCommandTest()
    {
	dBase = DataBaseTools.getInstance();
	dBase.init(DBASE.TEST);

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
    }

    @Test
    void testBuyBadArgs()
    {
	BuyCommandHandler handler = new BuyCommandHandler(dBase);

	// 0 items
	BuyError err = handler.perform("Michael", 0, "Shortsword");

	assertEquals(BuyError.NO_AMT, err, "performBuy");

	// bad item name
	err = handler.perform("Michael", 1, "XYZ");

	assertEquals(BuyError.UNKNOWN_ITEM, err, "performBuy");
    }

    @Test
    void testBuyNoUser()
    {
	BuyCommandHandler handler = new BuyCommandHandler(dBase);

	// Did not set up player
	BuyError err = handler.perform("Michael", 1, "Shortsword");

	assertEquals(BuyError.NO_PLAYER, err, "performBuy");
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

	BuyError err = handler.perform("Michael", 1, "Shortsword");

	assertEquals(BuyError.NO_ACT_PC, err, "performBuy");
    }

    @Test
    void testBuyNotInDB()
    {
	// make player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id(GOBO_DNDB_NUM);

	dBase.createPlayer(player);

	// not putting character in DB

	BuyCommandHandler handler = new BuyCommandHandler(dBase);

	BuyError err = handler.perform("Michael", 1, "Shortsword");

	assertEquals(BuyError.DB_ERR, err, "performBuy");

    }

    @Test
    void testBuyNoDNDB()
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

	BuyError err = handler.perform("Michael", 1, "Shortsword");

	assertEquals(BuyError.NO_DDNDB, err, "performBuy");

    }

//    @Test
//    void testBuyAllGood()
//    {
//	// make player
//	Player player = new Player();
//	player.setDiscordName("Michael");
//	player.setCurrCharDNDB_Id(GOBO_DNDB_NUM);
//
//	dBase.createPlayer(player);
//
//	// putting character in DB with broken DNDB #
//	PlayerCharacter pc = new PlayerCharacter();
//	pc.setName("Gobo");
//	pc.setDNDB_Num(GOBO_DNDB_NUM);
//
//	dBase.createCharacter(pc);
//
//
//	BuyCommandHandler handler = new BuyCommandHandler(dBase);
//
//	// performChar(String playerName, String pcName)
//	BuyError err = handler.perform("Michael", 1, "Shortsword");
//
//	assertEquals(BuyError.INSUFFICIENT_FUNDS, err, "performBuy");
//    }
}
