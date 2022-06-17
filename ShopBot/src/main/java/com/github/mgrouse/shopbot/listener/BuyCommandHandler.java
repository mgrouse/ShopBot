package com.github.mgrouse.shopbot.listener;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Item;
import com.github.mgrouse.shopbot.net.NetTools;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class BuyCommandHandler extends CommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(BuyCommandHandler.class);

    private SlashCommandInteractionEvent m_event = null;

    // protected DataBaseTools m_dBase;

    // protected String m_message = "";

    // protected Player m_player = null;

    // protected PlayerCharacter m_pc = null;

    private Item m_item = null;


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

//	// ArrayList of Lot
//	ArrayList<Lot> lots = new ArrayList<>();
//
//	// get the parameters from command line for now
//	Lot lot = null;
//	for (Integer i = 1; i <= 5; i++)
//	{
//	    lot = new Lot();
//	    lot.amt = m_event.getOption("amt" + i.toString()).getAsInt();
//
//	    lot.name = m_event.getOption("name" + i.toString()).getAsString();
//
//	    lots.add(lot);
//	}

	// req'd
	Integer amt1 = m_event.getOption("amt1").getAsInt();

	String itemName1 = m_event.getOption("item1").getAsString();

	// ===
	// not req'd may be missing
	Integer amt2 = m_event.getOption("amt2").getAsInt();

	String itemName2 = m_event.getOption("item2").getAsString();


	perform(userName, amt1, itemName1);
    }

    // package level for testing
    AppError perform(String userName, Integer amt, String itemName)
    {
	AppError err = validate(userName, amt, itemName);

	if (AppError.NONE != err)
	{
	    m_message = err.message();
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
	    return AppError.BUY_INSUFFICIENT_FUNDS;
	}

	// All is good
	// set PLayer.Cash and .Bill
	m_player.setCash(cash);
	m_player.setBill(bill);

	// update DB
	m_dBase.updatePlayer(m_player);

	m_message = "Your total is " + bill.toString()
		+ " gp. Please use /gold when you have deducted the gold from D&DB.";

	return AppError.NONE;
    }


    private AppError validate(String userName, Integer amt, String itemName)
    {
	if (amt < 1)
	{
	    return AppError.NO_SIZE;
	}

	// look up item(s) -- pass in List of names
	// get back list of Items.
	m_item = m_dBase.readItem(itemName);

	if (null == m_item)
	{
	    return AppError.UNKNOWN_ITEM;
	}

	// get user and PC if any and make sure they are still there
	return validatePlayerAndActivePC(userName);
    }

    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar
	m_event.getHook().sendMessage(m_message).queue();
    }

}

