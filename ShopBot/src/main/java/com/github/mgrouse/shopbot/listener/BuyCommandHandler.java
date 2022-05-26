package com.github.mgrouse.shopbot.listener;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Item;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;
import com.github.mgrouse.shopbot.net.NetTools;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class BuyCommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(BuyCommandHandler.class);

    protected DataBaseTools m_dBase;

    protected SlashCommandInteractionEvent m_event = null;

    protected String m_message = "";

    private Player m_player = null;

    private PlayerCharacter m_pc = null;

    private Item m_item = null;


    enum BuyError
    {
	NONE, NO_AMT, UNKNOWN_ITEM, NO_PLAYER, NO_ACT_PC, INSUFFICIENT_FUNDS, NO_DDNDB, DB_ERR;
    }

    public BuyCommandHandler(DataBaseTools dBase)
    {
	m_dBase = dBase;
    }

    public void go(SlashCommandInteractionEvent event)
    {
	m_event = event;
	parse();
	display();
    }

    private void parse()
    {
	// get User's Discord Name (Not the Koni nick Name)
	String userName = m_event.getUser().getName();

	// get the parameters from command line for now
	Integer amt = m_event.getOption("amt").getAsInt();

	String itemName = m_event.getOption("item").getAsString();

	// Check non-req args are not null?

	perform(userName, amt, itemName);
    }

    // package level for testing
    BuyError perform(String userName, Integer amt, String itemName)
    {
	BuyError err = validate(userName, amt, itemName);

	if (BuyError.NONE != err)
	{
	    return err;
	}

	// add up price
	BigDecimal bill = m_item.getBuyAmt().multiply(new BigDecimal(amt));

	// check PC's gold amt
	BigDecimal cash = NetTools.getDndbCurrency(m_player.getCurrCharDNDB_Id());

	int difference = cash.compareTo(bill);

	// if not enough let them know (-1 means cash < price)
	if (-1 == difference)
	{
	    m_message = "Your total of " + bill.toString() + " gp. Is more than you have";
	    return BuyError.INSUFFICIENT_FUNDS;
	}

	// All is good
	// set PLayer.Cash and .Bill
	m_player.setCash(cash);
	m_player.setBill(bill);

	// update DB
	m_dBase.updatePlayer(m_player);

	m_message = "Your total is " + bill.toString()
		+ " gp. Please use /Gold when you have deducted the gold from D&DB";

	return BuyError.NONE;
    }

    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar
	m_event.getHook().sendMessage(m_message).queue();
    }


    private BuyError validate(String userName, Integer amt, String itemName)
    {
	if (amt < 1)
	{
	    m_message = "You cannot buy 0 of something. Please leave my shop.";
	    return BuyError.NO_AMT;
	}

	// look up item(s) -- pass in List of names
	// get back list of Items.
	m_item = m_dBase.readItem(itemName);

	if (null == m_item)
	{
	    m_message = "I do not know what a " + itemName + " is.";
	    return BuyError.UNKNOWN_ITEM;
	}

	// get user and PC if any and make sure they are still there
	m_player = m_dBase.readPlayer(userName);

	if (null == m_player)
	{
	    m_message = "You have no Characters registered with ShopBot.";
	    return BuyError.NO_PLAYER;
	}

	// Active PC?
	if (m_player.getCurrCharDNDB_Id().isEmpty())
	{
	    m_message = "You have no active Character. Use the /Character command.";
	    return BuyError.NO_ACT_PC;
	}

	// still on DDNDB?
	if (!NetTools.isDNDBCharacter(m_player.getCurrCharDNDB_Id()))
	{
	    m_message = "Your active character cannot be found on DDNDB. Use the /Character command to set an active character.";
	    return BuyError.NO_DDNDB;
	}

	// in the DB
	m_pc = m_dBase.readCharacter(m_player.getCurrCharDNDB_Id());

	if (null == m_pc)
	{
	    // We should NEVER get here. this is on us.
	    m_message = "Error: You seem to have an active PC: " + m_player.getCurrCharDNDB_Id()
		    + " registered but not found.";
	    return BuyError.DB_ERR;
	}

	return BuyError.NONE;
    }


}

