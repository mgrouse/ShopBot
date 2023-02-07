package com.github.mgrouse.shopbot.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.DataBaseTools.DBASE;
import com.github.mgrouse.shopbot.database.Lot.TransactionType;


public class LotTest
{
    DataBaseTools dBase;

    Item item, item2;

    LotTest()
    {
	dBase = DataBaseTools.getInstance();
	DataBaseTools.init(DBASE.TEST);
    }

    @BeforeEach
    void beforeEach()
    {
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
    }

    @Test
    void lotCRUD()
    {
	Boolean success;

	// Create and fill a Lot

	// this is what it looks like at param checking time
	Lot lot = new Lot(1, "Shortsword", TransactionType.BUY);

	// this is what it looks like at validation time
	lot.setItemId(item.getId());
	lot.setItem(item);

	// DBase create
	lot = dBase.createLot(lot);

	// assert
	assertNotNull(lot, "createLot ");
	assertEquals(1, lot.getId(), "Lot Id ");
	assertEquals("Shortsword", lot.getName(), "Lot Name");
	assertEquals("BUY", lot.getType().toString(), "PURCHASE");

	assertEquals(0, lot.getPlayerId(), "PLayer ID");
	assertEquals(1, lot.getItemId(), "Item ID");
	assertNotNull(lot.getItem(), "the Item");
	assertEquals(1, lot.getSize(), "Size");
	assertEquals(0, lot.getNumOwned(), "Owned");
	assertEquals("10.00", lot.getValue().toString(), "Value");


	// read
	lot = dBase.readLastLot();

	// assert
	assertNotNull(lot, "createLot ");
	assertEquals(1, lot.getId(), "Lot Id ");
	assertEquals("Shortsword", lot.getName(), "Lot Name");
	assertEquals("BUY", lot.getType().toString(), "PURCHASE");

	assertEquals(0, lot.getPlayerId(), "PLayer ID");
	assertEquals(1, lot.getItemId(), "Item ID");
	assertNotNull(lot.getItem(), "the Item");
	assertEquals(1, lot.getSize(), "Size");
	assertEquals(0, lot.getNumOwned(), "Owned");
	assertEquals("10.00", lot.getValue().toString(), "Value");

	// read

	lot = dBase.readLot(1);

	// assert
	assertNotNull(lot, "createLot ");
	assertEquals(1, lot.getId(), "Lot Id ");
	assertEquals("Shortsword", lot.getName(), "Lot Name");
	assertEquals("BUY", lot.getType().toString(), "PURCHASE");

	assertEquals(0, lot.getPlayerId(), "PLayer ID");
	assertEquals(1, lot.getItemId(), "Item ID");
	assertNotNull(lot.getItem(), "the Item");
	assertEquals(1, lot.getSize(), "Size");
	assertEquals(0, lot.getNumOwned(), "Owned");
	assertEquals("10.00", lot.getValue().toString(), "Value");

	// modify
	lot.setSize(2);

	// update
	success = dBase.updateLot(lot);
	assertTrue(success, "Update == true");

	// read
	lot = dBase.readLot(1);

	// assert
	assertNotNull(lot, "createLot ");
	assertEquals(1, lot.getId(), "Lot Id ");
	assertEquals("Shortsword", lot.getName(), "Lot Name");
	assertEquals("BUY", lot.getType().toString(), "PURCHASE");

	assertEquals(0, lot.getPlayerId(), "PLayer ID");
	assertEquals(1, lot.getItemId(), "Item ID");
	assertNotNull(lot.getItem(), "the Item");
	assertEquals(2, lot.getSize(), "Size");
	assertEquals(0, lot.getNumOwned(), "Owned");
	assertEquals("20.00", lot.getValue().toString(), "Value");

	// Destroy
	success = dBase.destroyLot(lot);
	assertTrue(success, "destroy == true");

	// read
	lot = dBase.readLot(1);

	// assert
	assertNull(lot, "lot == null");

	// read
	lot = dBase.readLastLot();

	// assert
	assertNull(lot, "lot == null");
    }

}
