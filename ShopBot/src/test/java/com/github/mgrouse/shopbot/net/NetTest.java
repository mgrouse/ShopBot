package com.github.mgrouse.shopbot.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.PlayerCharacter;


public class NetTest
{
    private static final String AVATAR_URL = "https://www.dndbeyond.com/avatars/18/2/636378960438136837.jpeg?width=150&height=150&fit=crop&quality=95&auto=webp";
    private static final String GOBO_DNDB_NUM = "72248610";


    @Test
    void readDNDB()
    {

	// read Character
	PlayerCharacter gobo = NetTools.getDndbPlayerCharacter(GOBO_DNDB_NUM);

	// assert
	assertNotNull(gobo, "getDndbPlayerCharacter ");
	assertEquals("72248610", gobo.getDNDB_Num(), "imported DNDB_NUM");
	assertEquals("Gobo", gobo.getName(), "imported PC Name");
	assertEquals(AVATAR_URL, gobo.getAvatarURL(), "imported Avatar URL");

    }

    @Test
    void importPlayersFirstCharacter()
    {

    }
//    // must set currency to 1 each
//    @Test
//    void getDndbCurrencyTest()
//    {
//	BigDecimal gold = NetTools.getDndbCurrency(GOBO_DNDB_NUM);
//
//	assertNotNull(gold);
//	assertEquals("11.61", gold.toString(), " Gobo's Gold");
//    }
}
