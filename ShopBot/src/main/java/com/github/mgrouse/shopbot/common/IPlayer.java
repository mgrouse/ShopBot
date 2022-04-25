package com.github.mgrouse.shopbot.common;

public interface IPlayer
{
    // public Integer getID();

    public String getCurrCharDNDB_Id();

    public void setCurrCharDNDB_Id(String id);

    public String getDiscordName();

    public void setDiscordName(String name);

    public Boolean getIsInTransaction();

    public void setIsInTransaction(Boolean inTrans);

}
