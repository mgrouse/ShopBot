package com.github.mgrouse.shopbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class CharacterCommandHandler extends CommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(ImportCommandHandler.class);

    private SlashCommandInteractionEvent m_event = null;

    // protected DataBaseTools m_dBase;

    // protected Player m_player = null;

    // protected PlayerCharacter m_pc = null;

    // protected String m_message = "";


    public CharacterCommandHandler(DataBaseTools dBase)
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
	User user = m_event.getUser();

	String playerName = user.getName();

	// get pc name from event args
	String pcName = m_event.getOption("name").getAsString();

	performChar(playerName, pcName);
    }

    // package level function for testing
    AppError performChar(String playerName, String pcName)
    {
	AppError err = validate(playerName, pcName);

	if (AppError.NONE != err)
	{
	    m_message = err.message();
	    return err;
	}

	// set player.current char = pc.dnb
	m_player.setActiveDNDB_Id(m_pc.getDNDB_Num());

	// update player in B
	m_dBase.updatePlayer(m_player);

	m_message = pcName + " is ready to shop.";

	return AppError.NONE;
    }

    private AppError validate(String playerName, String pcName)
    {
	AppError err = validatePlayerAndNamedPC(playerName, pcName);

	if (AppError.NONE != err)
	{
	    m_message = err.message();
	    return err;
	}

	// if there is an open transaction
	err = validatePlayerNotInTransaction(playerName);

	if (AppError.NONE != err)
	{
	    m_message = AppError.NO_SWITCH_IN_TRANSACTION.message();
	    return AppError.NO_SWITCH_IN_TRANSACTION;
	}

	return AppError.NONE;
    }


    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar

	m_event.getHook().sendMessage(m_message).queue();
    }


}
