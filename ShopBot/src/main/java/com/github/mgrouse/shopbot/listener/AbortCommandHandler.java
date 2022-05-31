package com.github.mgrouse.shopbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class AbortCommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(ImportCommandHandler.class);

    DataBaseTools m_dBase;

    private SlashCommandInteractionEvent m_event = null;

    private String m_message = "";

    private Player m_player = null;

    private PlayerCharacter m_pc = null;


    enum AbortError
    {
	NONE, NO_PLAYER, NO_ACT_PC, NO_TRANSACTION;
    }

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

    AbortError performAbort(String playerName)
    {
	AbortError err = validate(playerName);

	if (AbortError.NONE != err)
	{
	    return err;
	}

	// if there is a Transaction
	if (m_player.hasBill())
	{
	    m_player.clearTransaction();
	    m_message = m_pc.getName() + "'s transaction has been aborted.";
	}
	else
	{
	    m_message = m_pc.getName() + " has no transaction to abort.";
	}

	return AbortError.NONE;
    }

    private AbortError validate(String playerName)
    {
	// look to see if there is a Player
	m_player = m_dBase.readPlayer(playerName);

	if (null == m_player)
	{
	    m_message = "You do not have any PC's in the ShopBot system.";
	    return AbortError.NO_PLAYER;
	}

	// look to see if there is a PC
	m_pc = m_dBase.getActivePcByPlayerName(playerName);

	if (null == m_pc)
	{
	    m_message = "You have no active PC in the ShopBot system.";
	    return AbortError.NO_ACT_PC;
	}

	return AbortError.NONE;
    }

    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar

	m_event.getHook().sendMessage(m_message).queue();
    }
}
