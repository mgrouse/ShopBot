package com.github.mgrouse.shopbot.listener;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.net.NetTools;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class GoldCommandHandler extends CommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(BuyCommandHandler.class);

    private SlashCommandInteractionEvent m_event = null;

    // Inherited
    // protected DataBaseTools m_dBase;

    // protected String m_message = "";

    // protested Player m_player = null;

    // protected PlayerCharacter m_pc = null;


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
    AppError perform(String userName)
    {
	AppError err = validate(userName);

	if (AppError.NONE != err)
	{
	    m_message = err.message();
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
	    m_message = AppError.GOLD_UNDER_PAYED.message();
	    return AppError.GOLD_UNDER_PAYED;
	}

	// if pcCurrentCash too low -
	if (comparison == -1)
	{
	    m_message = AppError.GOLD_OVER_PAYED.message();
	    return AppError.GOLD_OVER_PAYED;
	}

	// if comparison == 0, exact - "Enjoy your Items"
	m_message = "Enjoy your Items";
	return AppError.NONE;
    }


    private AppError validate(String userName)
    {
	AppError err = this.validatePlayerAndActivePC(userName);

	if (AppError.NONE != err)
	{
	    return err;
	}

	err = validatePlayerHasBill(m_player);

	return err;
    }

    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar
	m_event.getHook().sendMessage(m_message).queue();
    }
}
