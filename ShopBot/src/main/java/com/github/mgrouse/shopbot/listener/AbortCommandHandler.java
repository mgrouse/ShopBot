package com.github.mgrouse.shopbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class AbortCommandHandler extends CommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(ImportCommandHandler.class);

    private SlashCommandInteractionEvent m_event = null;


    // protected DataBaseTools m_dBase;

    // protected String m_message = "";

    // protected Player m_player = null;

    // protected PlayerCharacter m_pc = null;


    public AbortCommandHandler(DataBaseTools dBase)
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
	String playerName = m_event.getUser().getName();

	performAbort(playerName);
    }

    AppError performAbort(String playerName)
    {
	AppError err = validatePlayer(playerName);

	if (AppError.NONE != err)
	{
	    m_message = err.message();
	    return err;
	}


	// if there is a Sell Transaction
	if (sellLotsExist(playerName))
	{
	    m_dBase.deleteSellLotsByPlayer(playerName);
	    m_message = "Sell Transaction has been aborted. ";
	}


	// if there is a Buy Transaction
	if (m_player.hasBill())
	{
	    // clear and update
	    m_player.clearTransaction();
	    m_dBase.updatePlayer(m_player);

	    m_message = "Buy Transaction has been aborted. ";
	}

	return AppError.NONE;
    }


//    private AppError validate(String playerName)
//    {
//	AppError err = validatePlayer(playerName);
//
//	if (AppError.NONE != err)
//	{
//	    return err;
//	}
//
//	return validatePlayerHasBill(m_player);
//    }

    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar

	m_event.getHook().sendMessage(m_message).queue();
    }
}
