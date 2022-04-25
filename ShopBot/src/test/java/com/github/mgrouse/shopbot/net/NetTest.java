package com.github.mgrouse.shopbot.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.github.mgrouse.shopbot.database.PlayerCharacter;


public class NetTest
{
    private static final String AVATAR_URL = "https://www.dndbeyond.com/avatars/24943/984/1581111423-70565720.jpeg?width=150&height=150&fit=crop&quality=95&auto=webp";
    private static final String DNDB_NUM = "70565720";

    @Test
    void readDNDB()
    {

	// read Character 69033575
	PlayerCharacter edward = NetTools.getDndbPlayerCharacter(DNDB_NUM);

	// assert
	assertNotNull(edward, "getDndbPlayerCharacter ");
	assertEquals("70565720", edward.getDNDB_Num(), "imported DNDB_NUM");
	assertEquals("Edward Garretson of Cormyr", edward.getName(), "imported PC Name");
	assertEquals(AVATAR_URL, edward.getAvatarURL(), "imported Avatar URL");

    }

    @Test
    void importPlayersFirstCharacter()
    {

    }
}
