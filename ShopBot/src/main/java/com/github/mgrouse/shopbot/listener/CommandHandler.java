package com.github.mgrouse.shopbot.listener;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;
import com.github.mgrouse.shopbot.net.NetTools;


public class CommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(CommandHandler.class);

    protected DataBaseTools m_dBase;

    protected String m_message = "";

    protected Player m_player = null;

    protected PlayerCharacter m_pc = null;


    public enum AppError
    {
	NONE("Eveerything is Fiiiiiinnnne"), //
	DNDB_404("Character with Id: <DNDB_NUM> was not found at DND Beyond."),
	NO_PLAYER("You have no characters in the ShopBot system."), //
	NO_PC("The Character: <PC_NAME> was not found"), //
	ACT_PC_NOT_SET("You have no active character listed. Use the /character command."),
	ACT_PC_DNDB_404("Your active character cannot be found on DND Beyond."),
	ACT_PC_DBASE_404("Error: You seem to have an active PC: <DNDB_NUM> registered but not found in Data Base."),
	IN_TRANSACTION("You cannot switch PCs in the middle of a transaction"),
	BUY_NO_AMT("You cannot buy 0 of something."), //
	BUY_UNKNOWN_ITEM("I do not know what a <ITEM_NAME> is."),
	BUY_INSUFFICIENT_FUNDS("Your total bill of: <BILL> gp. Is more than you have."),
	GOLD_UNDER_PAYED("You did not pay your bill."),
	GOLD_OVER_PAYED("This looks funny, you payed your bill, and then some."),
	GOLD_NO_BILL("You don't seem to have a bill to pay."), //
	GOLD_NO_CASH("You don't seem to have a cash record.");

	private String m_message = "";

	AppError(String message)
	{
	    this.m_message = message;
	}

	public String message()
	{
	    return m_message;
	}
    }

    // Do we need anything more than a bill to abort a transaction?
    // should we issue an error if there is no Act PC, Bill, Cash?
    protected AppError validatePlayerHasBill(Player playerName)
    {
	BigDecimal zero = new BigDecimal("0.00");

	// if PLayer.Bill == 0
	if (0 == m_player.getBill().compareTo(zero)) // 0 means numbers are same
	{
	    return AppError.GOLD_NO_BILL;
	}

	// if PLayer.Cash == 0
	if (0 == m_player.getCash().compareTo(zero)) // 0 means numbers are same
	{
	    return AppError.GOLD_NO_CASH;
	}

	return AppError.NONE;
    }

    protected AppError validatePlayer(String playerName)
    {
	// look to see if there is a Player
	m_player = m_dBase.readPlayer(playerName);

	if (null == m_player)
	{
	    return AppError.NO_PLAYER;
	}

	return AppError.NONE;
    }

    protected AppError validatePCOnDNDB(String dndb_Num)
    {
	// still on DDNDB?
	if (!NetTools.isDNDBCharacter(dndb_Num))
	{
	    return AppError.DNDB_404;
	}
	return AppError.NONE;
    }

    protected AppError validatePlayerAndNamedPC(String playerName, String pcName)
    {
	// look to see if there is a Player
	m_player = m_dBase.readPlayer(playerName);

	if (null == m_player)
	{
	    return AppError.NO_PLAYER;
	}

	// look to see if there is a PC
	m_pc = m_dBase.getPCByPlayerNameAndPCName(playerName, pcName);

	if (null == m_pc)
	{
	    return AppError.NO_PC;
	}

	return AppError.NONE;
    }

    protected AppError validatePlayerAndActivePC(String playerName)
    {
	// get user and PC if any and make sure they are still there
	m_player = m_dBase.readPlayer(playerName);

	if (null == m_player)
	{
	    return AppError.NO_PLAYER;
	}

	// Active PC?
	if (m_player.getCurrCharDNDB_Id().isEmpty())
	{
	    return AppError.ACT_PC_NOT_SET;
	}

	// still on DDNDB?
	if (!NetTools.isDNDBCharacter(m_player.getCurrCharDNDB_Id()))
	{
	    return AppError.ACT_PC_DNDB_404;
	}

	// in the DB
	m_pc = m_dBase.readCharacter(m_player.getCurrCharDNDB_Id());

	if (null == m_pc)
	{
	    return AppError.ACT_PC_DBASE_404;
	}

	return AppError.NONE;
    }
}
