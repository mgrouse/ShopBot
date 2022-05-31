package com.github.mgrouse.shopbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class CharacterCommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(ImportCommandHandler.class);

    private DataBaseTools m_dBase;

    private SlashCommandInteractionEvent m_event = null;

    private Player m_player = null;

    private PlayerCharacter m_pc = null;

    private String m_message = "";

    enum CharError
    {
	NONE, NO_PC, NO_USER, IN_TRANSACTION;
    }

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
    CharError performChar(String playerName, String pcName)
    {
	CharError err = validate(playerName, pcName);

	if (CharError.NONE != err)
	{
	    return err;
	}

	// set players current char to pc.dnb
	m_player.setCurrCharDNDB_Id(m_pc.getDNDB_Num());

	// update player in B
	m_dBase.updatePlayer(m_player);

	m_message = pcName + " is ready to shop.";

	return CharError.NONE;
    }

    private CharError validate(String playerName, String pcName)
    {
	// find Player in DB
	m_player = m_dBase.readPlayer(playerName);

	// if they are not there abort ("you have no pcs")
	if (null == m_player)
	{
	    m_message = "You have no PC's in the ShopBot system.";
	    return CharError.NO_USER;
	}

	// find pc in DB
	m_pc = m_dBase.getPCByPlayerNameAndPCName(playerName, pcName);

	// if pc not in DB ("did not find pc")
	if (null == m_pc)
	{
	    m_message = "The PC: " + pcName + " was not found";
	    return CharError.NO_PC;
	}

	// if there is an open transaction
	if (m_player.hasBill())
	{
	    m_message = "The PC: " + pcName + " owes " + m_player.getBill().toString()
		    + "in gold. You cannot switch PCs in the middle of a transaction";
	    return CharError.IN_TRANSACTION;
	}

	return CharError.NONE;
    }


    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar

	m_event.getHook().sendMessage(m_message).queue();
    }


}
