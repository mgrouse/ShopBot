package com.github.mgrouse.shopbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;
import com.github.mgrouse.shopbot.net.NetTools;


public class ShoppingCommand
{
    private static Logger m_logger = LoggerFactory.getLogger(BuyCommandHandler.class);

    protected DataBaseTools m_dBase;

    protected String m_message = "";

    protected Player m_player = null;

    protected PlayerCharacter m_pc = null;


    enum ShoppingError
    {
	NONE, NO_PLAYER, NO_ACT_PC, NO_DDNDB, DB_ERR;
    }


    protected ShoppingError validatePlayerAndActivePC(String userName)
    {
	// get user and PC if any and make sure they are still there
	m_player = m_dBase.readPlayer(userName);

	if (null == m_player)
	{
	    m_message = "You have no Characters registered with ShopBot.";
	    return ShoppingError.NO_PLAYER;
	}

	// Active PC?
	if (m_player.getCurrCharDNDB_Id().isEmpty())
	{
	    m_message = "You have no active Character. Use the /Character command.";
	    return ShoppingError.NO_ACT_PC;
	}

	// still on DDNDB?
	if (!NetTools.isDNDBCharacter(m_player.getCurrCharDNDB_Id()))
	{
	    m_message = "Your active character cannot be found on DDNDB. Use the /Character command to set an active character.";
	    return ShoppingError.NO_DDNDB;
	}

	// in the DB
	m_pc = m_dBase.readCharacter(m_player.getCurrCharDNDB_Id());

	if (null == m_pc)
	{
	    // We should NEVER get here. this is on us.
	    m_message = "Error: You seem to have an active PC: " + m_player.getCurrCharDNDB_Id()
		    + " registered but not found.";
	    return ShoppingError.DB_ERR;
	}

	return ShoppingError.NONE;
    }
}
