package com.github.mgrouse.shopbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Lot;
import com.github.mgrouse.shopbot.net.Inventory;
import com.github.mgrouse.shopbot.net.NetTools;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class ItemCommandHandler extends CommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(ItemCommandHandler.class);

    private SlashCommandInteractionEvent m_event = null;


    public ItemCommandHandler(DataBaseTools dBase)
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

	perform(pName);
    }

    // package function for testing
    AppError perform(String pName)
    {
	AppError err = validate(pName);

	if (err != AppError.NONE)
	{
	    m_message = err.message();
	    return err;
	}

	// get PC's Inventory
	Inventory inv = NetTools.getDndbInventory(m_pc.getDNDB_Num());

	Boolean removed = false;
	// for each Lot
	for (Lot lot : m_lots)
	{
	    // make sure that each lot has been removed
	    removed = inv.hasRemovedLot(lot);

	    if (!removed)
	    {
		m_message = AppError.ITEM_NOT_REMOVED.message();
		return AppError.ITEM_NOT_REMOVED;
	    }
	}

	// OK we made it! remove the lots from the database.
	m_dBase.deleteSellLotsByPlayer(pName);

	m_message = "Enjoy your gold.";

	return AppError.NONE;
    }

    private AppError validate(String pName)
    {
	// validate user/active pc
	AppError err = validatePlayerAndActivePC(pName);

	if (err != AppError.NONE)
	{
	    return err;
	}

	// Validate Lots for user/active pc
	return validateSellLotsExist(pName);
    }


    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar

	m_event.getHook().sendMessage(m_message).queue();
    }
}
