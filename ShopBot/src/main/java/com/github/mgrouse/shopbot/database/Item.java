package com.github.mgrouse.shopbot.database;

public class Item
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


    public Integer getBuyAmt()
    {
	return m_buyAmt;
    }


    public void setBuyAmt(Integer amt)
    {
	m_buyAmt = amt;
    }


    public Integer getSellAmt()
    {
	return m_sellAmt;
    }


    public void setSellAmt(Integer amt)
    {
	m_sellAmt = amt;
    }

}
