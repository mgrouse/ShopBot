package com.github.mgrouse.shopbot.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.DataBaseTools.DBASE;


class DBaseTest
{
    DataBaseTools dBase;

    DBaseTest()
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
    }


    @Test
    void playerCRUD()
    {
	Boolean success;

	// Create and fill player1
	Player player1 = new Player();
	player1.setDiscordName("Michael");
	player1.setCurrCharDNDB_Id("12345678");


	// DBase create
	player1 = dBase.createPlayer(player1);

	assertNotNull(player1, "createPlayer ");
	assertEquals(1, player1.getId(), "Player1 Id ");
	assertEquals("Michael", player1.getDiscordName(), "DiscordName ");
	assertEquals("12345678", player1.getCurrCharDNDB_Id(), "CurrCharDNDB_Id ");
	assertEquals("0.00", player1.getCash().toString(), "Cash ");
	assertEquals("0.00", player1.getBill().toString(), "Bill ");

	// DBase read
	Player player2 = dBase.readPlayer("NotPresent");

	assertNull(player2, "Not Present");

	player2 = dBase.readPlayer("Michael");
	assertNotNull(player2, "readPlayer player2 ");
	assertEquals(1, player2.getId(), "Player2 Id ");
	assertEquals("Michael", player2.getDiscordName(), "DiscordName ");
	assertEquals("12345678", player2.getCurrCharDNDB_Id(), "CurrCharDNDB_Id ");
	assertEquals("0.00", player1.getCash().toString(), "Cash ");
	assertEquals("0.00", player1.getBill().toString(), "Bill ");

	// modify player1
	player1.setDiscordName("Grouse");
	player1.setCurrCharDNDB_Id("87654321");
	player1.setCash(new BigDecimal("12.34"));
	player1.setBill(new BigDecimal("56.78"));


	// DBase update player1
	success = dBase.updatePlayer(player1);
	assertTrue(success, "Update Player?");

	// DBase Read
	player1 = dBase.readPlayer("Grouse");

	assertNotNull(player1, "createPlayer shouuld return an object ");
	assertEquals(1, player1.getId(), "Player1 Id ");
	assertEquals("Grouse", player1.getDiscordName(), "DiscordName ");
	assertEquals("87654321", player1.getCurrCharDNDB_Id(), "CurrCharDNDB_Id ");
	assertEquals("12.34", player1.getCash().toString(), "Cash ");
	assertEquals("56.78", player1.getBill().toString(), "Bill ");

	// DBase delete player1
	dBase.destroyPlayer(player1);

	// DBase Read
	player1 = dBase.readPlayer("Grouse");

	// assert empty
	assertNull(player1, "Read after Destroy ");
    }


    @Test
    void CharacterCRUD()
    {
	Boolean success;

	// Create and fill character1
	PlayerCharacter char1 = new PlayerCharacter();
	// char1.setID(0);
	char1.setPlayerId(2);
	char1.setName("Corvus");
	char1.setDNDB_Num("12345678");
	char1.setAvatarURL("https://www.someone.com/pics/mage.png");

	// DBase create
	char1 = dBase.createCharacter(char1);

	assertNotNull(char1, "Create a Character ");
	assertEquals(1, char1.getID(), "Character ID ");
	assertEquals(2, char1.getPlayerID(), "Char1.getPlayerID() ");
	assertEquals("Corvus", char1.getName(), "Character name ");
	assertEquals("12345678", char1.getDNDB_Num(), "DNDB_Num ");
	assertEquals("https://www.someone.com/pics/mage.png", char1.getAvatarURL(), "Avatar URL ");

	// DBase read
	PlayerCharacter char2 = dBase.readCharacter("12345678");

	assertNotNull(char2, "readChar() ");

	// assert equal to char 1 values
	assertEquals(1, char2.getID(), "Character ID ");
	assertEquals(2, char2.getPlayerID(), "Char1.getPlayerID() ");
	assertEquals("Corvus", char2.getName(), "Character name ");
	assertEquals("12345678", char2.getDNDB_Num(), "DNDB_Num ");
	assertEquals("https://www.someone.com/pics/mage.png", char2.getAvatarURL(), "Avatar URL ");

	// modify Character1
	char1.setName("SilverMoon");
	char1.setAvatarURL("https://some really long URL ok this isn't really all that long");

	// DBase update Character1
	success = dBase.updateCharacter(char1);
	assertTrue(success, "Update Character? ");

	// DBase Read
	char1 = dBase.readCharacter("12345678");

	// assert equal to Character1
	assertEquals("SilverMoon", char1.getName());
	String sTemp = "https://some really long URL ok this isn't really all that long";
	assertEquals(sTemp, char1.getAvatarURL());

	// DBase delete Character1
	success = dBase.destroyCharacter(char1);
	assertTrue(success, "Delete Character? ");

	// DBase Read Character1
	char1 = dBase.readCharacter("12345678");

	// assert empty
	assertNull(char1, "Read Deleted");
    }

    @Test
    void testAssociatePC()
    {
	// Create and fill player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id("87654321");


	// Create and fill character1
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num("12345678");
	pc.setAvatarURL("https://www.someone.com/pics/mage.png");

	// save them
	player = dBase.createPlayer(player);
	pc = dBase.createCharacter(pc);

	// Assert not null
	assertNotNull(player, "Create a Player ");
	assertNotNull(pc, "Create a Character ");

	// associate them
	dBase.associatePlayerAndPC(player, pc);

	// update them
	Boolean isDone = dBase.updatePlayer(player);
	assertTrue(isDone, "Update Player");

	isDone = dBase.updateCharacter(pc);
	assertTrue(isDone, "Update Character");

	// read and check Player
	player = dBase.readPlayer("Michael");
	assertNotNull(player, "Read a Player ");
	assertEquals(1, player.getId(), "Player Id ");
	assertEquals("Michael", player.getDiscordName(), "DiscordName ");
	assertEquals("87654321", player.getCurrCharDNDB_Id(), "CurrCharDNDB_Id ");

	// read and check pc
	pc = dBase.readCharacter("12345678");
	assertNotNull(pc, "Read a Character ");
	assertEquals(1, pc.getID(), "Character ID ");
	assertEquals(1, pc.getPlayerID(), "pc.getPlayerID() ");
	assertEquals("Corvus", pc.getName(), "Character name ");
	assertEquals("12345678", pc.getDNDB_Num(), "DNDB_Num ");
	assertEquals("https://www.someone.com/pics/mage.png", pc.getAvatarURL(), "Avatar URL ");
    }

    @Test
    void testGetPCByName()
    {
	// Create and fill player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id("87654321");


	player = dBase.createPlayer(player);

	// Create and fill character1
	PlayerCharacter pc1 = new PlayerCharacter();
	pc1.setName("Corvus");
	pc1.setDNDB_Num("12345678");
	pc1.setAvatarURL("https://www.someone.com/pics/mage.png");

	pc1 = dBase.createCharacter(pc1);

	// Create and fill character2
	PlayerCharacter pc2 = new PlayerCharacter();
	pc2.setName("Edward");
	pc2.setDNDB_Num("45678123");
	pc2.setAvatarURL("https://www.someone.com/pics/cleric.png");

	pc2 = dBase.createCharacter(pc2);

	// Create and fill character3
	PlayerCharacter pc3 = new PlayerCharacter();
	pc3.setName("Rumil");
	pc3.setDNDB_Num("87654321");
	pc3.setAvatarURL("https://www.someone.com/pics/ranger.png");

	pc3 = dBase.createCharacter(pc3);

	// Associate all three
	dBase.associatePlayerAndPC(player, pc1);

	dBase.associatePlayerAndPC(player, pc2);

	dBase.associatePlayerAndPC(player, pc3);

	// update them
	dBase.updatePlayer(player);

	dBase.updateCharacter(pc1);
	dBase.updateCharacter(pc2);
	dBase.updateCharacter(pc3);

	// Call with correct names
	PlayerCharacter pcRead = dBase.getPCByPlayerNameAndPCName("Michael", "Corvus");

	// assert
	assertNotNull(pcRead, "getPCByPlayerNameAndPCName");
	assertEquals("Corvus", pcRead.getName(), "Character name ");

	// Call with incorrect Player Name
	pcRead = dBase.getPCByPlayerNameAndPCName("Andrew", "Corvus");

	// assert
	assertNull(pcRead, "Bad Player");

	// Call with incorrect PC Name
	pcRead = dBase.getPCByPlayerNameAndPCName("Michael", "Gandalf");

	// assert
	assertNull(pcRead, "Bad PC");

    }

    @Test
    void testPlayersActivePc()
    {
	// Create and fill player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id("");

	player = dBase.createPlayer(player);

	// getActPC should return null as we have not put one in yet
	PlayerCharacter active = dBase.getPlayersActivePc("Michael");
	assertNull(active, "No active PC");

	// Create and fill character
	PlayerCharacter pc = new PlayerCharacter();
	pc.setName("Corvus");
	pc.setDNDB_Num("87654321");
	pc.setAvatarURL("https://www.someone.com/pics/mage.png");

	pc = dBase.createCharacter(pc);

	// modify player
	player.setCurrCharDNDB_Id("87654321");

	// associate them
	dBase.associatePlayerAndPC(player, pc);

	// update them
	dBase.updatePlayer(player);
	dBase.updateCharacter(pc);

	// getActPC should return active PC as we put one in
	active = dBase.getPlayersActivePc("Michael");
	assertNotNull(active, "Active PC");

    }

    @Test
    void testGetAllCharactersByPlayerName()
    {
	// Create and fill player
	Player player = new Player();
	player.setDiscordName("Michael");
	player.setCurrCharDNDB_Id("87654321");


	player = dBase.createPlayer(player);

	// Create and fill character1
	PlayerCharacter pc1 = new PlayerCharacter();
	pc1.setName("Corvus");
	pc1.setDNDB_Num("12345678");
	pc1.setAvatarURL("https://www.someone.com/pics/mage.png");

	pc1 = dBase.createCharacter(pc1);

	// Create and fill character2
	PlayerCharacter pc2 = new PlayerCharacter();
	pc2.setName("Edward");
	pc2.setDNDB_Num("45678123");
	pc2.setAvatarURL("https://www.someone.com/pics/cleric.png");

	pc2 = dBase.createCharacter(pc2);

	// Create and fill character3
	PlayerCharacter pc3 = new PlayerCharacter();
	pc3.setName("Rumil");
	pc3.setDNDB_Num("87654321");
	pc3.setAvatarURL("https://www.someone.com/pics/ranger.png");

	pc3 = dBase.createCharacter(pc3);

	// Associate TWO of three
	dBase.associatePlayerAndPC(player, pc1);

	dBase.associatePlayerAndPC(player, pc2);

	// update them
	dBase.updatePlayer(player);

	dBase.updateCharacter(pc1);
	dBase.updateCharacter(pc2);

	// Call with correct Player name
	List<PlayerCharacter> pcList = dBase.getAllCharactersByPlayerName("Michael");

	// Assert
	assertNotNull(pcList, "getAllCharactersByPlayerName");

	assertEquals(2, pcList.size(), "# PC's ");

	assertEquals("Corvus", pcList.get(0).getName(), "PC #0 name ");
	assertEquals("Edward", pcList.get(1).getName(), "PC #1 name ");


	// Call with incorrect Player Name
	pcList = dBase.getAllCharactersByPlayerName("Andrew");

	// Assert
	assertNotNull(pcList, "getAllCharactersByPlayerName");
	assertEquals(0, pcList.size(), "# PC's");
    }

    @Test
    void testItemCRUD()
    {
	Boolean success;
	Item item;

	// Create and fill Item
	item = new Item();

	item.setName("Shortsword");
	item.setCategory("Weapon");
	item.setBuyAmt("10.00");
	item.setSellAmt("5.00");

	// DBase create
	item = dBase.createItem(item);

	assertNotNull(item, "Create a Item ");
	assertEquals(1, item.getId(), "Item ID ");
	assertEquals("Shortsword", item.getName(), "Item Name ");
	assertEquals("Weapon", item.getCategory(), "Item Category ");
	assertEquals("10.00", item.getBuyAmt().toString(), "Item BuyAmt ");
	assertEquals("5.00", item.getSellAmt().toString(), "Item SellAmt ");

	// read
	item = dBase.readItem("Shortsword");

	// Assert
	assertNotNull(item, "Read an Item ");
	assertEquals(1, item.getId(), "Item ID ");
	assertEquals("Shortsword", item.getName(), "Item Name ");
	assertEquals("Weapon", item.getCategory(), "Item Category ");
	assertEquals("10.00", item.getBuyAmt().toString(), "Item BuyAmt ");
	assertEquals("5.00", item.getSellAmt().toString(), "Item SellAmt ");

	// modify item
	item.setName("Short sword");
	item.setCategory("Light Weapon");
	item.setBuyAmt("11.20");
	item.setSellAmt("5.60");

	// DBase update item
	success = dBase.updateItem(item);
	assertTrue(success, "Item Update");

	// DBase Read
	item = dBase.readItem("Short sword");

	// Assert
	assertNotNull(item, "Read a Item ");
	assertEquals(1, item.getId(), "Item ID ");
	assertEquals("Short sword", item.getName(), "Item Name ");
	assertEquals("Light Weapon", item.getCategory(), "Item Category ");
	assertEquals("11.20", item.getBuyAmt().toString(), "Item BuyAmt ");
	assertEquals("5.60", item.getSellAmt().toString(), "Item SellAmt ");

	// DBase delete item
	success = dBase.destroyItem(item);
	assertTrue(success, "destroyItem(null)");

	// DBase Read
	Item temp = dBase.readItem("Short sword");

	// assert empty
	assertNull(temp, "Read a missing Item ");

	// test delete all
	item.setId(0);
	item = dBase.createItem(item);

	// Delete All
	dBase.deleteAllItems();

	item = dBase.readItem("Shortsword");

	assertNull(item, "Read after delete all Items ");
    }

    @Test
    void testGetAllItems()
    {
	// create and store 3 items
	// 1
	Item item1 = new Item();

	item1.setName("Shortsword");
	item1.setCategory("Weapons");
	item1.setBuyAmt("10.00");
	item1.setSellAmt("5.00");

	item1 = dBase.createItem(item1);

	// 2
	Item item2 = new Item();

	item2.setName("Studded Leather");
	item2.setCategory("Armor");
	item2.setBuyAmt("25.00");
	item2.setSellAmt("12.50");

	item2 = dBase.createItem(item2);

	// 3
	Item item3 = new Item();

	item3.setName("Arrows 20");
	item3.setCategory("Ammo");
	item3.setBuyAmt("2.00");
	item3.setSellAmt("1.00");

	item3 = dBase.createItem(item3);


	// List<Item> getAllItems()
	List<Item> items = dBase.getAllItems();

	// Assert
	Item temp = items.get(0);

	assertNotNull(temp, "Get[0] Item ");
	assertEquals(3, temp.getId(), "Item ID ");
	assertEquals("Arrows 20", temp.getName(), "Item Name ");
	assertEquals("Ammo", temp.getCategory(), "Item Category ");
	assertEquals("2.00", temp.getBuyAmt().toString(), "Item BuyAmt ");
	assertEquals("1.00", temp.getSellAmt().toString(), "Item SellAmt ");

	// Assert
	temp = items.get(1);

	assertNotNull(temp, "Get[1] Item ");
	assertEquals(1, temp.getId(), "Item ID ");
	assertEquals("Shortsword", temp.getName(), "Item Name ");
	assertEquals("Weapons", temp.getCategory(), "Item Category ");
	assertEquals("10.00", temp.getBuyAmt().toString(), "Item BuyAmt ");
	assertEquals("5.00", temp.getSellAmt().toString(), "Item SellAmt ");

	// Assert
	temp = items.get(2);

	assertNotNull(temp, "Get[2] Item ");
	assertEquals(2, temp.getId(), "Item ID ");
	assertEquals("Studded Leather", temp.getName(), "Item Name ");
	assertEquals("Armor", temp.getCategory(), "Item Category ");
	assertEquals("25.00", temp.getBuyAmt().toString(), "Item BuyAmt ");
	assertEquals("12.50", temp.getSellAmt().toString(), "Item SellAmt ");

    }

    @Test
    void testGetCategories()
    {
	// create and store 3 items
	// 1
	Item item1 = new Item();

	item1.setName("Shortsword");
	item1.setCategory("Weapons");
	item1.setBuyAmt("10.00");
	item1.setSellAmt("5.00");

	item1 = dBase.createItem(item1);

	// 2
	Item item2 = new Item();

	item2.setName("Studded Leather");
	item2.setCategory("Armor");
	item2.setBuyAmt("25.00");
	item2.setSellAmt("12.50");

	item2 = dBase.createItem(item2);

	// 3
	Item item3 = new Item();

	item3.setName("Arrows 20");
	item3.setCategory("Ammo");
	item3.setBuyAmt("2.00");
	item3.setSellAmt("1.00");

	item3 = dBase.createItem(item3);

	// List<String> getCategories()
	List<String> cats = dBase.getCategories();

	// Assert
	assertNotNull(cats, "Categories not empty");

	assertEquals("Ammo", cats.get(0));
	assertEquals("Armor", cats.get(1));
	assertEquals("Weapons", cats.get(2));

    }

    @Test
    void testGetItemsByCategory()
    {
	// create and store 3 items
	// 1
	Item item1 = new Item();

	item1.setName("Shortsword");
	item1.setCategory("Weapons");
	item1.setBuyAmt("10.00");
	item1.setSellAmt("5.00");

	item1 = dBase.createItem(item1);

	// 2
	Item item2 = new Item();

	item2.setName("Studded Leather");
	item2.setCategory("Armor");
	item2.setBuyAmt("25.00");
	item2.setSellAmt("12.50");

	item2 = dBase.createItem(item2);

	// 3
	Item item3 = new Item();

	item3.setName("Arrows 20");
	item3.setCategory("Ammo");
	item3.setBuyAmt("2.00");
	item3.setSellAmt("1.00");

	item3 = dBase.createItem(item3);

	// List<Item> getItemsByCategory(String category)
	List<Item> items = dBase.getItemsByCategory("Ammo");

	// Assert
	assertNotNull(items, "Items not empty");

	assertEquals(1, items.size(), "Only one Ammo");

	Item temp = items.get(0);

	assertEquals(3, temp.getId(), "Item ID ");
	assertEquals("Arrows 20", temp.getName(), "Item Name ");
	assertEquals("Ammo", temp.getCategory(), "Item Category ");
	assertEquals("2.00", temp.getBuyAmt().toString(), "Item BuyAmt ");
	assertEquals("1.00", temp.getSellAmt().toString(), "Item SellAmt ");
    }


}
