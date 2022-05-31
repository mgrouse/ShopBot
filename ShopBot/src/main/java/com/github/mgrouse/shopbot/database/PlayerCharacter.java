package com.github.mgrouse.shopbot.database;

import java.math.BigDecimal;


public class PlayerCharacter
{
    private Integer m_ID = 0;
    private Integer m_playerID = 0;

    private String m_dndbNum = "";

    private String m_name = "";

    private String m_avatarURL = "";

    private BigDecimal m_currency = new BigDecimal("0.0");


    public PlayerCharacter()
    {

    }

    // Having no access modifier, the following
    // 4 methods are package-private
    public Integer getID()
    {
	return m_ID;
    }

    public void setID(int id)
    {
	m_ID = id;
    }

    public Integer getPlayerID()
    {
	return m_playerID;
    }

    public void setPlayerId(int id)
    {
	m_playerID = id;
    }


    public String getDNDB_Num()
    {
	return m_dndbNum;
    }


    public void setDNDB_Num(String num)
    {
	m_dndbNum = num;
    }


    public String getName()
    {
	return m_name;
    }


    public void setName(String name)
    {
	m_name = name;
    }


    public String getAvatarURL()
    {
	return m_avatarURL;
    }


    public void setAvatarURL(String url)
    {
	m_avatarURL = url;
    }


    @Override
    public String toString()
    {
	return "Character: [ID:" + m_ID + ", PlayerID:" + m_playerID + ", DNDB_Num:" + m_dndbNum + ", Char_Name:"
		+ m_name + ", Avatar_URL:" + m_avatarURL + "]";
    }
}
