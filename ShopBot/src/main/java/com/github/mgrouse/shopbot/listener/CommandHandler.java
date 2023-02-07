package com.github.mgrouse.shopbot.listener;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Item;
import com.github.mgrouse.shopbot.database.Lot;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;
import com.github.mgrouse.shopbot.net.Inventory;
import com.github.mgrouse.shopbot.net.NetTools;


public class CommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(CommandHandler.class);

    protected DataBaseTools m_dBase;

    protected String m_message = "";

    protected Player m_player = null;

    protected PlayerCharacter m_pc = null;


    // this very bad fix this (unite) as soon as 2.0
    protected Lot m_lot = null;

    protected List<Lot> m_lots = null;


    public enum AppError
    {
	NONE("Everything is Fiiiiiinnnne"), //
	DNDB_404("Character with Id: <DNDB_NUM> was not found at DND Beyond."), //
	NO_PLAYER("You have not imported any PCs into the ShopBot system."), //
	NO_PC("The Character: <PC_NAME> was not found"), //
	ACT_PC_NOT_SET("You have no active character listed. Use the /character command."), //
	ACT_PC_DNDB_404("Your active character cannot be found on DND Beyond."), //
	ACT_PC_DBASE_404("Error: You seem to have an active PC: <DNDB_NUM> registered but not found in Data Base."), //
	GENERIC_ALREADY_IN_TRANSACTION(
		"You cannot perform the current command becausee you are in the middle of a transaction."), //
	NO_SWITCH_IN_TRANSACTION(
		"You cannot switch PCs in the middle of a transaction. Please finish or /abort that transaction before switching the Active PC."), //
	NO_SIZE("You cannot exchange 0 of something."), //
	UNKNOWN_ITEM("I do not know what a <ITEM_NAME> is."),
	BUY_SELL_ALREADY_IN_TRANSACTION(
		"Your active character is already in the middle of a transaction. Please finish or /abort that transaction before beginning another."), //
	BUY_INSUFFICIENT_FUNDS("Your total bill of: <BILL> gp. Is more than you have."),
	GOLD_UNDER_PAYED("You did not pay your bill."),
	GOLD_OVER_PAYED("This looks funny, you payed your bill, and then some."),
	GOLD_NO_BILL("You don't seem to have a bill to pay."), //
	GOLD_NO_CASH("You don't seem to have a cash record."),
	SELL_NOT_OWNED("You do not seem to own enough <ITEM_NAME>."),
	ITEM_TRANSACTION_404("You do not seem to have a record of a sale transaction."),
	ITEM_NOT_REMOVED("You do not seem to have handed over <SIZE> <ITEM_NAME>.");


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

    protected AppError validateActivePCIfAny()
    {
	// Its perfectly fine if there is no Active PC

	// If there is one listed...
	if (!m_player.getActiveDNDB_Id().isEmpty())
	{
	    // Check to see if its in the DB
	    m_pc = m_dBase.getPlayersActivePc(m_player.getDiscordName());

	    if (null == m_pc)
	    {
		return AppError.ACT_PC_DBASE_404;
	    }

	    // Also check to see if its on DNDB
	    AppError err = validatePCOnDNDB(m_pc.getDNDB_Num());

	    if (AppError.NONE != err)
	    {
		return AppError.ACT_PC_DNDB_404;
	    }
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
	if (m_player.getActiveDNDB_Id().isEmpty())
	{
	    return AppError.ACT_PC_NOT_SET;
	}

	// still on DDNDB?
	if (!NetTools.isDNDBCharacter(m_player.getActiveDNDB_Id()))
	{
	    return AppError.ACT_PC_DNDB_404;
	}

	// in the DB
	m_pc = m_dBase.readCharacter(m_player.getActiveDNDB_Id());

	if (null == m_pc)
	{
	    return AppError.ACT_PC_DBASE_404;
	}

	return AppError.NONE;
    }


    protected AppError validatePlayerActivePcOwnsLot(String playerName, Lot lot)
    {
	// validate an fill ion lot
	AppError err = validateLot(lot);

	if (err != AppError.NONE)
	{
	    return err;
	}

	// validate the player and ActPC exist in DB and DNDB
	err = validatePlayerAndActivePC(playerName);

	if (err != AppError.NONE)
	{
	    return err;
	}

	// validate ActPC Owns Lot
	return validateActivePcOwnsLot(m_player, lot);
    }


    protected AppError validateActivePcOwnsLot(Player player, Lot lot)
    {
	Inventory inv = NetTools.getDndbInventory(player.getActiveDNDB_Id());

	Boolean hasIt = inv.hasLot(lot);

	if (false == hasIt)
	{
	    return AppError.SELL_NOT_OWNED;
	}

	lot.setPlayerId(player.getId());
	m_lot = lot;

	return AppError.NONE;
    }


    protected AppError validateActivePcAffordLot(String playerName, Lot lot)
    {
	return AppError.NONE;
    }

    protected AppError validateLot(Lot lot)
    {
	// validate Lot - amt and Item
	if (lot.getSize() < 1)
	{
	    return AppError.NO_SIZE;
	}

	// look up item(s) -- pass in List of names
	// get back list of Items.
	Item tempItem = m_dBase.readItem(lot.getName());

	if (null == tempItem)
	{
	    return AppError.UNKNOWN_ITEM;
	}

	lot.setItemId(tempItem.getId());
	lot.setItem(tempItem);

	return AppError.NONE;
    }

    protected AppError validatePlayerNotInTransaction(String playerName)
    {
	Boolean found = false;

	// look for buy
	// retVal = m_dBase.buyLotsExistByPlayer(playerName);
//	if (found)
//	{
//	    return AppError.GENERIC_ALREADY_IN_TRANSACTION;
//	}

	// in 2.0 the above will replace the below

	// look for buy
	Player player = m_dBase.readPlayer(playerName);


	if ((null != player) && (player.hasTransaction()))
	{
	    return AppError.GENERIC_ALREADY_IN_TRANSACTION;
	}


	// look for sell
	found = m_dBase.sellLotsExistByPlayer(playerName);

	if (found)
	{
	    return AppError.GENERIC_ALREADY_IN_TRANSACTION;
	}

	return AppError.NONE;
    }

    protected AppError validateSellLotsExist(String playerName)
    {
	m_lots = m_dBase.getSellLotsByPlayer(playerName);

	if (m_lots.size() == 0)
	{
	    return AppError.ITEM_TRANSACTION_404;
	}

	return AppError.NONE;
    }

//    protected AppError validateBuyLotsExist(String playerName)
//    {
//
//    }


    protected Boolean sellLotsExist(String playerName)
    {
	m_lots = m_dBase.getSellLotsByPlayer(playerName);

	return m_lots.size() > 0;
    }

    protected Boolean buyLotsExist(String playerName)
    {
	m_lots = m_dBase.getBuyLotsByPlayer(playerName);

	return m_lots.size() > 0;
    }

}
