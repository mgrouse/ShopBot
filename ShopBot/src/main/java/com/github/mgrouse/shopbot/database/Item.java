package com.github.mgrouse.shopbot.database;

import com.github.mgrouse.shopbot.common.IItem;


public class Item implements IItem
{

    private Integer m_ID = 0;

    private String m_name = "";

    private String m_category = "";

    private Integer m_buyAmt = 0;

    private Integer m_sellAmt = 0;


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

    @Override
    public String getName()
    {
	return m_name;
    }

    @Override
    public void setName(String name)
    {
	m_name = name;
    }

    @Override
    public String getCategory()
    {
	return m_category;
    }

    @Override
    public void setCategory(String category)
    {
	m_category = category;
    }

    @Override
    public Integer getBuyAmt()
    {
	return m_buyAmt;
    }

    @Override
    public void setBuyAmt(Integer amt)
    {
	m_buyAmt = amt;
    }

    @Override
    public Integer getSellAmt()
    {
	return m_sellAmt;
    }

    @Override
    public void setSellAmt(Integer amt)
    {
	m_sellAmt = amt;
    }

}
