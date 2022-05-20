package com.github.mgrouse.shopbot.database;

import java.math.BigDecimal;


public class Item implements Comparable<Item>
{

    private Integer m_ID = 0;

    private String m_name = "";

    private String m_category = "";

    private BigDecimal m_buyAmt = new BigDecimal("0.00");

    private BigDecimal m_sellAmt = new BigDecimal("0.00");


    public Item()
    {

    }

    // Having no access modifier, the following
    // 2 methods are package-private
    Integer getID()
    {
	return m_ID;
    }

    void setID(int id)
    {
	m_ID = id;
    }


    public String getName()
    {
	return m_name;
    }


    public void setName(String name)
    {
	m_name = name;
    }


    public String getCategory()
    {
	return m_category;
    }


    public void setCategory(String category)
    {
	m_category = category;
    }


    public String getBuyAmt()
    {
	return m_buyAmt.toString();
    }


    public void setBuyAmt(String amt)
    {
	m_buyAmt = new BigDecimal(amt);
    }


    public String getSellAmt()
    {
	return m_sellAmt.toString();
    }


    public void setSellAmt(String amt)
    {
	m_sellAmt = new BigDecimal(amt);
    }

    @Override
    public int compareTo(Item o)
    {
	return this.getName().compareTo(o.getName());
    }


}
