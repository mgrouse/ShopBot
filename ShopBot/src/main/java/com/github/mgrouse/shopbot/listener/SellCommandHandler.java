package com.github.mgrouse.shopbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Lot;
import com.github.mgrouse.shopbot.database.Lot.TransactionType;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class SellCommandHandler extends CommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(SellCommandHandler.class);

    private SlashCommandInteractionEvent m_event = null;


    public SellCommandHandler(DataBaseTools dBase)
    {
	m_dBase = dBase;
    }

    public void go(SlashCommandInteractionEvent event)
    {
	m_event = event;
	parse();
	display();
    }

    public void parse()
    {
	// get User's Discord Name (Not the Koni nick Name)
	String pName = m_event.getUser().getName();

	// req'd
	Integer amt1 = m_event.getOption("amt1").getAsInt();

	String itemName1 = m_event.getOption("item1").getAsString();

	// verify that args are submitted in pairs
	Lot lot = new Lot(amt1, itemName1, TransactionType.SELL);

	perform(pName, lot);
    }

    // package function for testing
    AppError perform(String userName, Lot lot)
    {
	AppError err = validatePlayerActivePcOwnsLot(userName, lot);

	if (AppError.NONE != err)
	{
	    m_message = err.message();
	    return err;
	}

	// store the lot after setting the player_id, Item_I
	lot.setPlayerId(m_player.getId());

	m_dBase.createLot(lot);

	m_message = "You are selling " + m_lot.getSize() + " " + m_lot.getItem().getName() + " for "
		+ m_lot.getValue().toString() + " gp.";

	m_message += "Use the /Item command when you have removed the items from your sheet.";
	return AppError.NONE;
    }


    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar

	m_event.getHook().sendMessage(m_message).queue();
    }
}
