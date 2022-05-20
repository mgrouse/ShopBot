package com.github.mgrouse.shopbot.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.DataBaseTools.DBASE;


public class ItemReaderTest
{
    private final static String m_fileName = "items.csv";

    DataBaseTools dBase;

    ItemReaderTest()
    {
	dBase = DataBaseTools.getInstance();
	dBase.init(DBASE.TEST);
    }

    @BeforeEach
    void beforeEach()
    {
	dBase.deleteAllItems();
    }

    @Test
    void testPerformReadItems()
    {
	// Open Resource as Buffered Reader
	ClassLoader loader = ItemReader.class.getClassLoader();

	try
	{
	    // get RESOURCE config file as a BufferedReader
	    InputStream stream = loader.getResourceAsStream(m_fileName);
	    InputStreamReader sReader = new InputStreamReader(stream);
	    BufferedReader bReader = new BufferedReader(sReader);
	    ItemReader itemReader = new ItemReader(dBase);

	    // Call func
	    itemReader.performReadItems(bReader);

	    // look for Items in DB
	    Item item = dBase.readItem("Shortsword");

	    assertNotNull(item, "Read an Item ");
	    assertEquals(1, item.getID(), "Item ID ");
	    assertEquals("Shortsword", item.getName(), "Item Name ");
	    assertEquals("Weapons", item.getCategory(), "Item Category ");
	    assertEquals("10.00", item.getBuyAmt(), "Item BuyAmt ");
	    assertEquals("5.00", item.getSellAmt(), "Item SellAmt ");


	    item = dBase.readItem("Studded");
	    assertNotNull(item, "Read an Item ");
	    assertEquals(2, item.getID(), "Item ID ");
	    assertEquals("Studded Leather", item.getName(), "Item Name ");
	    assertEquals("Armor", item.getCategory(), "Item Category ");
	    assertEquals("25.00", item.getBuyAmt(), "Item BuyAmt ");
	    assertEquals("12.50", item.getSellAmt(), "Item SellAmt ");


	    item = dBase.readItem("Arrows");
	    assertNotNull(item, "Read an Item ");
	    assertEquals(3, item.getID(), "Item ID ");
	    assertEquals("Arrows 20", item.getName(), "Item Name ");
	    assertEquals("Ammo", item.getCategory(), "Item Category ");
	    assertEquals("2.00", item.getBuyAmt(), "Item BuyAmt ");
	    assertEquals("1.00", item.getSellAmt(), "Item SellAmt ");

	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}


    }
}
