package com.github.mgrouse.shopbot.database;

import java.math.BigDecimal;


public class Player
{
    private Integer m_ID = 0;

    private String m_dndb_Id = "";

    private String m_discordName = "";

    private BigDecimal cash = new BigDecimal("0.00");

    private BigDecimal bill = new BigDecimal("0.00");

    public Player()
    {

    }

    // Having no access modifier, the following
    // 2 methods are package-private
    public Integer getID()
    {
	return m_ID;
    }

    public void setID(int id)
    {
	m_ID = id;
    }


    public String getCurrCharDNDB_Id()
    {
	return m_dndb_Id;
    }


    public void setCurrCharDNDB_Id(String id)
    {
	m_dndb_Id = id;
    }


    public String getDiscordName()
    {
	return m_discordName;
    }


    public void setDiscordName(String name)
    {
	m_discordName = name;
    }

    public BigDecimal getCash()
    {
	return cash;
    }

    public void setCash(BigDecimal cash)
    {
	this.cash = cash;
    }

    public BigDecimal getBill()
    {
	return bill;
    }

    public void setBill(BigDecimal bill)
    {
	this.bill = bill;
    }

    @Override
    public String toString()
    {
	return "Player [m_ID=" + m_ID + ", m_dndb_Id=" + m_dndb_Id + ", m_discordName=" + m_discordName + ", cash="
		+ cash + ", bill=" + bill + "]";
    }
}
