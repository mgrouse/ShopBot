package com.github.mgrouse.shopbot.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

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
	assertEquals("11.61", gold.toString(), " Gobo's Gold");
    }
}
