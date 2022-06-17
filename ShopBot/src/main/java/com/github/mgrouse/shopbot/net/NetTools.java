package com.github.mgrouse.shopbot.net;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.PlayerCharacter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class NetTools
{

    private static final String DnDBJsonCharacter = "https://character-service.dndbeyond.com/character/v3/character/";

    private static Logger m_logger = LoggerFactory.getLogger(NetTools.class);


    public static Boolean isDNDBCharacter(String dndbNum)
    {
	Boolean retVal = false;

	try
	{
	    URL url = new URL(DnDBJsonCharacter + dndbNum);

	    InputStreamReader isReader = new InputStreamReader(url.openStream());

	    JsonElement root = JsonParser.parseReader(isReader);

	    // May be an array, may be an object.
	    JsonObject rootObj = root.getAsJsonObject();

	    JsonObject data = rootObj.get("data").getAsJsonObject();

	    if (null != data)
	    {
		retVal = true;
	    }

	}
	catch (Exception e)
	{
	    // e.printStackTrace();
	}
	finally
	{

	}
	return retVal;
    }


    public static PlayerCharacter getDndbPlayerCharacter(String dndbNum)
    {
	PlayerCharacter retVal = null;

	try
	{
	    URL url = new URL(DnDBJsonCharacter + dndbNum);

	    InputStreamReader isReader = new InputStreamReader(url.openStream());

	    JsonElement root = JsonParser.parseReader(isReader);

	    // May be an array, may be an object.
	    JsonObject rootObj = root.getAsJsonObject();

	    JsonObject data = rootObj.get("data").getAsJsonObject();

	    if (null != data)
	    {
		retVal = new PlayerCharacter();

		retVal.setDNDB_Num(dndbNum);

		retVal.setAvatarURL(data.get("avatarUrl").getAsString());

		retVal.setName(data.get("name").getAsString());
	    }

	}
	catch (Exception e)
	{
	    m_logger.error("Attempt to import: " + dndbNum, e);
	}
	finally
	{

	}

	return retVal;
    }


    public static BigDecimal getDndbCurrency(String dndbNum)
    {
	BigDecimal retVal = null;

	try
	{
	    URL url = new URL(DnDBJsonCharacter + dndbNum);

	    InputStreamReader isReader = new InputStreamReader(url.openStream());

	    JsonElement root = JsonParser.parseReader(isReader);

	    // May be an array, may be an object.
	    JsonObject rootObj = root.getAsJsonObject();

	    JsonObject data = rootObj.get("data").getAsJsonObject();

	    if (null != data)
	    {
		JsonObject money = data.get("currencies").getAsJsonObject();

		// convert money into Big Decimal
		retVal = new MoneyPouch(money).asBigDecimal();
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

	return retVal;
    }


    public static Inventory getDndbInventory(String dndbNum)
    {
	Inventory retVal = new Inventory();

	try
	{
	    URL url = new URL(DnDBJsonCharacter + dndbNum);

	    InputStreamReader isReader = new InputStreamReader(url.openStream());

	    JsonElement root = JsonParser.parseReader(isReader);

	    // May be an array, may be an object.
	    JsonObject rootObj = root.getAsJsonObject();

	    JsonObject data = rootObj.get("data").getAsJsonObject();

	    if (null != data)
	    {
		JsonArray inventory = data.get("inventory").getAsJsonArray();

		retVal.load(inventory);
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

	return retVal;
    }
}

