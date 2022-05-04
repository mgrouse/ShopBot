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


//    ------------------------
//    private Integer m_platinum = 0;
//
//    private Integer m_gold = 0;
//
//    private Integer m_electrum = 0;
//
//    private Integer m_silver = 0;
//
//    private Integer m_copper = 0;


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


//    @Override
//    public BigDecimal getCurrency()
//    {
//	return m_currency;
//    }
//
//    @Override
//    public void setCurrency(BigDecimal currency)
//    {
//	m_currency = currency;
//    }
//
//    @Override
//    public Integer getPlatinum()
//    {
//	return m_platinum;
//    }
//
//    @Override
//    public void setPlatinum(Integer platinum)
//    {
//	this.m_platinum = platinum;
//    }
//
//    @Override
//    public Integer getGold()
//    {
//	return m_gold;
//    }
//
//    @Override
//    public void setGold(Integer gold)
//    {
//	m_gold = gold;
//    }
//
//    @Override
//    public Integer getElectrum()
//    {
//	return m_electrum;
//    }
//
//    @Override
//    public void setElectrum(Integer electrum)
//    {
//	this.m_electrum = electrum;
//    }
//
//    @Override
//    public Integer getSilver()
//    {
//	return m_silver;
//    }
//
//    @Override
//    public void setSilver(Integer silver)
//    {
//	m_silver = silver;
//    }
//
//    @Override
//    public Integer getCopper()
//    {
//	return m_copper;
//    }
//
//    @Override
//    public void setCopper(Integer copper)
//    {
//	m_copper = copper;
//    }

}
