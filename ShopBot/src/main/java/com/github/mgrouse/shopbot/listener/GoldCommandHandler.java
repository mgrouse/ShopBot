package com.github.mgrouse.shopbot.listener;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.net.NetTools;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class GoldCommandHandler extends ShoppingCommand
{
    private static Logger m_logger = LoggerFactory.getLogger(BuyCommandHandler.class);

    private SlashCommandInteractionEvent m_event = null;

    // Inherited
    // protected DataBaseTools m_dBase;

    // protected String m_message = "";

    // protested Player m_player = null;

    // protected PlayerCharacter m_pc = null;

    enum GoldError
    {
	NONE, SHOPPING_ERR, NO_CASH, NO_BILL, UNDER_PAYMENT, OVER_PAYMENT;
    }

    public GoldCommandHandler(DataBaseTools dBase)
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

	perform(userName);
    }

    // package level for testing
    GoldError perform(String userName)
    {
	GoldError err = validate(userName);

	if (GoldError.NONE != err)
	{
	    return err;
	}

	// get PC "cash on hand" from DNDB
	BigDecimal pcCurrentCash = NetTools.getDndbCurrency(m_player.getCurrCharDNDB_Id());

	BigDecimal pcOldCash = m_player.getCash();

	BigDecimal pcBill = m_player.getBill();

	// pcChange = pcOldCash - pcBill
	BigDecimal pcChange = pcOldCash.subtract(pcBill);

	// pcChange == pcCurrentCash?
	int comparison = pcCurrentCash.compareTo(pcChange);

	// if pcCurrentCash too high -
	if (comparison == 1)
	{
	    m_message = "You did not pay your bill.";
	    return GoldError.UNDER_PAYMENT;
	}

	// if pcCurrentCash too low -
	if (comparison == -1)
	{
	    m_message = "You payed your bill, and then some, this looks funny.";
	    return GoldError.OVER_PAYMENT;
	}

	// if comparison == 0, exact - "Enjoy your Items"
	m_message = "Enjoy your Items";
	return GoldError.NONE;
    }


    private GoldError validate(String userName)
    {
	ShoppingError err = this.validatePlayerAndActivePC(userName);

	if (ShoppingError.NONE != err)
	{
	    // m_message already set by validatePlayerAndActivePC()
	    return GoldError.SHOPPING_ERR;
	}

	BigDecimal zero = new BigDecimal("0.00");

	// if PLayer.Bill == 0
	if (0 == m_player.getBill().compareTo(zero)) // 0 means numbers are same
	{
	    m_message = "You don't seem to have a bill to pay.";
	    return GoldError.NO_BILL;
	}

	// if PLayer.Cash == 0
	if (0 == m_player.getCash().compareTo(zero)) // 0 means numbers are same
	{
	    m_message = "You don't seem to have a cash record.";
	    return GoldError.NO_CASH;
	}

	return GoldError.NONE;
    }

    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar
	m_event.getHook().sendMessage(m_message).queue();
    }
}
