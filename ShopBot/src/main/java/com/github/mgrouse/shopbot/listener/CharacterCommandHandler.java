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

    private String m_message = "";

    public CharacterCommandHandler(DataBaseTools dBase)
    {
	m_dBase = dBase;
    }

    public void doChar(SlashCommandInteractionEvent event)
    {
	m_event = event;

	// get User's Discord Name (Not the Koni nick Name)
	User user = m_event.getUser();

	String playerName = user.getName();

	// get pc name from event args
	String pcName = m_event.getOption("name").getAsString();

	performChar(playerName, pcName);

	displayResults();
    }

    // package level function for testing
    void performChar(String playerName, String pcName)
    {
	m_dBase = DataBaseTools.getInstance();

	// find Player in DB
	m_player = m_dBase.readPlayer(playerName);

	// if they are not there abort ("you have no pcs")
	if (null == m_player)
	{
	    m_message = "You have no PC's in the ShopBot system.";
	}
	else
	{
	    // find pc in DB
	    PlayerCharacter pc = m_dBase.getPCByPlayerNameAndPCName(playerName, pcName);

	    // if pc not in DB ("did not find pc")
	    if (null == pc)
	    {
		m_message = "The PC: " + pcName + " was not found";
	    }
	    else
	    {
		// set players current char to pc.dnb
		m_player.setCurrCharDNDB_Id(pc.getDNDB_Num());

		// update player in B
		m_dBase.updatePlayer(m_player);

		m_message = pcName + " is ready to shop.";
	    }
	}
    }

    private void displayResults()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar

	m_event.getHook().sendMessage(m_message).queue();
    }


}
