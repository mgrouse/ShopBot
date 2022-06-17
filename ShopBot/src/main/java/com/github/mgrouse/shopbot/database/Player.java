package com.github.mgrouse.shopbot.database;

import java.math.BigDecimal;


public class Player
{
    private Integer m_Id = 0;

    private String m_dndb_Id = "";

    private String m_discordName = "";

    private BigDecimal cash = new BigDecimal("0.00");

    private BigDecimal bill = new BigDecimal("0.00");

    public Player()
    {

    }

    // Having no access modifier, the following
    // 2 methods are package-private
    public Integer getId()
    {
	return m_Id;
    }

    public void setId(int id)
    {
	m_Id = id;
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

    public Boolean hasCash()
    {
	// 1 means cash is bigger than 0.00
	return (this.getCash().compareTo(new BigDecimal("0.00")) == 1);
    }

    public BigDecimal getBill()
    {
	return bill;
    }

    public void setBill(BigDecimal bill)
    {
	this.bill = bill;
    }

    public Boolean hasBill()
    {
	// 1 means bill is bigger than 0.00
	return (this.getBill().compareTo(new BigDecimal("0.00")) == 1);
    }

    public Boolean hasTransaction()
    {
	return hasBill() && hasCash();
    }

    public void clearTransaction()
    {
	BigDecimal zero = new BigDecimal("0.00");

	this.setBill(zero);
	this.setCash(zero);
    }

    @Override
    public String toString()
    {
	return "Player [m_ID=" + m_Id + ", m_dndb_Id=" + m_dndb_Id + ", m_discordName=" + m_discordName + ", cash="
		+ cash + ", bill=" + bill + "]";
    }
}
