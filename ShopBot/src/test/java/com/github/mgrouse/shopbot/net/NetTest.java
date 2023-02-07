package com.github.mgrouse.shopbot.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.Lot;
import com.github.mgrouse.shopbot.database.Lot.TransactionType;
import com.github.mgrouse.shopbot.database.PlayerCharacter;


public class NetTest
{
    // Switch to Druid
    private static final String AVATAR_URL = "https://www.dndbeyond.com/avatars/17/221/636377842071269252.jpeg?width=150&height=150&fit=crop&quality=95&auto=webp";
    private static final String DRUID_DNDB_NUM = "72792237";


    @Test
    void readDNDB()
    {

	// read Character
	PlayerCharacter druid = NetTools.getDndbPlayerCharacter(DRUID_DNDB_NUM);

	// assert
	assertNotNull(druid, "getDndbPlayerCharacter ");
	assertEquals(DRUID_DNDB_NUM, druid.getDNDB_Num(), "imported DNDB_NUM");
	assertEquals("Druid", druid.getName(), "imported PC Name");
	assertEquals(AVATAR_URL, druid.getAvatarURL(), "imported Avatar URL");

    }


    // must set currency to 1 each
    @Test
    void getDndbCurrencyTest()
    {
	BigDecimal gold = NetTools.getDndbCurrency(DRUID_DNDB_NUM);

	assertNotNull(gold);
	assertEquals("26.61", gold.toString(), " Gobo's Gold");
    }

    @Test
    void getDndbInventoryHasOneTest()
    {
	Inventory i = NetTools.getDndbInventory(DRUID_DNDB_NUM);

	Lot oneScimitar = new Lot(1, "Scimitar", TransactionType.SELL);

	Boolean hasScimitar = i.hasLot(oneScimitar);

	assertTrue(hasScimitar, "Has a Scimitar");
    }

    @Test
    void getDndbInventoryHasTwoTest()
    {
	Inventory i = NetTools.getDndbInventory(DRUID_DNDB_NUM);

	Lot twoScimitar = new Lot(2, "Scimitar", TransactionType.SELL);

	Boolean hasScimitars = i.hasLot(twoScimitar);

	assertFalse(hasScimitars, "Has two Scimitars");
    }

    @Test
    void getDndbInventoryDoesNotHaveOneTest()
    {
	Inventory i = NetTools.getDndbInventory(DRUID_DNDB_NUM);

	Lot oneMace = new Lot(1, "Mace", TransactionType.SELL);

	// Druid has no mace
	Boolean hasMace = i.hasLot(oneMace);

	assertFalse(hasMace, "Has no Mace");
    }

    @Test
    void getDndbInventoryRemovedOneTest()
    {
	Inventory i = NetTools.getDndbInventory(DRUID_DNDB_NUM);

	// Druid has no mace
	Lot oneMace = new Lot(1, 1, "Mace");

	Boolean hasMace = i.hasRemovedLot(oneMace);

	assertTrue(hasMace, "Has no Mace");
    }

    @Test
    void getDndbInventoryRemovedOneHasOneTest()
    {
	Inventory i = NetTools.getDndbInventory(DRUID_DNDB_NUM);

	// Druid has no mace
	Lot oneMace = new Lot(2, 1, "Scimitar");

	Boolean hasMace = i.hasRemovedLot(oneMace);

	assertTrue(hasMace, "Has one Scimitar");
    }
}
