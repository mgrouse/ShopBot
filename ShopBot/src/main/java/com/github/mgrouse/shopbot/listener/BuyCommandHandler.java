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


    public BuyCommandHandler(DataBaseTools dBase)
    {
	m_dBase = dBase;
    }

    public void go(SlashCommandInteractionEvent event)
    {
	parse();
	display();
    }

    private void parse()
    {
	// get User's Discord Name (Not the Koni nick Name)
	String userName = m_event.getUser().getName();

	// get the parameters from command line for now
	Integer amt = m_event.getOption("amt").getAsInt();

	String itemName = m_event.getOption("name").getAsString();

	// Check non-req args are not null?
	if (amt < 1)
	{
	    m_message = "You cannot buy 0 of something. Please leave my shop.";
	    return;
	}

	m_player = m_dBase.readPlayer(userName);

	if (null == m_player)
	{
	    m_message = "You have no Characters registered with ShopBot.";
	    return;
	}

	if (m_player.getCurrCharDNDB_Id() == "")
	{
	    m_message = "You have no active Character. Use the /Character command.";
	    return;
	}

	m_pc = m_dBase.readCharacter(m_player.getCurrCharDNDB_Id());

	if (null == m_pc)
	{
	    m_message = "Error: You seem to have an active PC: " + m_player.getCurrCharDNDB_Id()
		    + " registered but not found.";
	    return;
	}

	perform(userName, amt, itemName);
    }

    // make a test case
    // package level for testing
    void perform(String userName, Integer amt, String itemName)
    {
	// look up item(s) -- pass in List of names
	// get back list of Items.
	Item thing = m_dBase.readItem(itemName);

	if (null == thing)
	{
	    m_message = "I do not know what a " + itemName + " is.";
	    return;
	}

	// add up price
	BigDecimal price = thing.getBuyAmt().multiply(new BigDecimal(amt));

	// check PC's gold amt
	BigDecimal cash = NetTools.getDndbCurrency(m_player.getCurrCharDNDB_Id());

	int difference = cash.compareTo(price);

	// if not enough let them know (-1 means cash < price)
	if (-1 == difference)
	{
	    m_message = "Your total of " + price.toString() + " gp. Is more than you have";
	}
	else
	{
	    // All is good
	    // set player.inTransaction

	    m_message = "Your total is " + price.toString()
		    + " gp. Please use /Gold when you have deducted the gold from D&DB";
	}

    }


    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar
	m_event.getHook().sendMessage(m_message).queue();
    }

}

