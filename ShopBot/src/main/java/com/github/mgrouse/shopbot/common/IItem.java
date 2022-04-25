package com.github.mgrouse.shopbot.common;

public interface IItem
{
    // public Integer getID();

    public String getName();

    public void setName(String name);

    public String getCategory();

    public void setCategory(String cateegory);

    public Integer getBuyAmt();

    public void setBuyAmt(Integer amt);

    public Integer getSellAmt();

    public void setSellAmt(Integer amt);
}
