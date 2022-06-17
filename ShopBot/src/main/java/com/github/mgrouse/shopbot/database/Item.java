package com.github.mgrouse.shopbot.database;

import java.math.BigDecimal;


public class Item implements Comparable<Item>
{

    private Integer m_Id = 0;

    private String m_name = "";

    private String m_category = "";

    private BigDecimal m_buyAmt = new BigDecimal("0.00");

    private BigDecimal m_sellAmt = new BigDecimal("0.00");


    public Item()
    {

    }


    public Integer getId()
    {
	return m_Id;
    }

    // Having no access modifier, the following
    // 2 methods are package-private
    void setId(int id)
    {
	m_Id = id;
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


    public BigDecimal getBuyAmt()
    {
	return m_buyAmt;
    }


    public void setBuyAmt(BigDecimal amt)
    {
	m_buyAmt = amt;
    }

    public void setBuyAmt(String amt)
    {
	m_buyAmt = new BigDecimal(amt);
    }

    public BigDecimal getSellAmt()
    {
	return m_sellAmt;
    }


    public void setSellAmt(BigDecimal amt)
    {
	m_sellAmt = amt;
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
