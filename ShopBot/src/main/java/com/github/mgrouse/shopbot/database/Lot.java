package com.github.mgrouse.shopbot.database;

import java.math.BigDecimal;


public class Lot
{
    // id of this Lot in the DB
    private int m_id = 0;

    // player who has this in their transaction
    private int m_playerId = 0;

    // id of the rep. Item in the DB
    private int m_itemId = 0;

    // representative Item from the DB
    private Item m_item = null;

    // number of things
    private Integer m_size = 0;

    // number of things owned at start of transaction
    private Integer m_numOwned = 0;

    // name of the thing in the DB
    private String m_name = "";

    // Note: could be based on purchase amt or sell amt
    private BigDecimal m_value = new BigDecimal("0.00");

    // /buy == true or /sell == false
    private TransactionType m_type = null;


    public enum TransactionType
    {
	BUY, SELL;
    }

    protected Lot()
    {

    }

    public Lot(Integer size, String name, TransactionType type)
    {
	m_size = size;
	m_name = name;

	m_type = type;
    }

    public Lot(Integer owned, Integer size, String name)
    {
	m_numOwned = owned;
	m_size = size;
	m_name = name;

	m_type = TransactionType.SELL;
    }

    public int getId()
    {
	return m_id;
    }

    public void setId(int id)
    {
	this.m_id = id;
    }

    public int getItemId()
    {
	return m_itemId;
    }

    public void setItemId(int m_itemId)
    {
	this.m_itemId = m_itemId;
    }

    public int getPlayerId()
    {
	return m_playerId;
    }

    public void setPlayerId(int playerId)
    {
	this.m_playerId = playerId;
    }

    public Integer getSize()
    {
	return m_size;
    }

    public BigDecimal getSizeAsBD()
    {
	return new BigDecimal(getSize().toString());
    }

    public void setSize(Integer size)
    {
	this.m_size = size;
    }

    public Integer getNumOwned()
    {
	return m_numOwned;
    }

    public void setNumOwned(Integer owned)
    {
	this.m_numOwned = owned;
    }

    public Item getItem()
    {
	return m_item;
    }

    public void setItem(Item item)
    {
	if (null == item)
	{
	    return;
	}

	// Item
	this.m_item = item;

	// Name
	this.setName(item.getName());

	// Value
	if (m_type == TransactionType.BUY)
	{
	    m_value = getSizeAsBD().multiply(m_item.getBuyAmt());
	}
	else
	{
	    m_value = getSizeAsBD().multiply(m_item.getSellAmt());
	}
    }

    public String getName()
    {
	return m_name;
    }

    public void setName(String name)
    {
	this.m_name = name;
    }

    public BigDecimal getValue()
    {
	return m_value;
    }

    public TransactionType getType()
    {
	return m_type;
    }

    public void setType(TransactionType type)
    {
	this.m_type = type;
    }

//    public void setValue(String price)
//    {
//	this.m_value = new BigDecimal(price);
//    }
//
//    private void setValue(BigDecimal price)
//    {
//	this.m_value = price;
//    }

}
