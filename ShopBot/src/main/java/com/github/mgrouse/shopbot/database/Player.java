package com.github.mgrouse.shopbot.database;

public class Player
{
    private Integer m_ID = 0;

    private String m_dndb_Id = "";

    private String m_discordName = "";

    private Boolean m_isInTransaction = false;

    public Player()
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


    public Boolean getIsInTransaction()
    {
	return m_isInTransaction;
    }


    public void setIsInTransaction(Boolean inTrans)
    {
	m_isInTransaction = inTrans;
    }


    @Override
    public String toString()
    {
	return "Player: [ID:" + m_ID + ", Curr_Char_ID:" + m_dndb_Id + ", Discord_Name:" + m_discordName
		+ ", InTransaction:" + m_isInTransaction + "]";
    }
}
