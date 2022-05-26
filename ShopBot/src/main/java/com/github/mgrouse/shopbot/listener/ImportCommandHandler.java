package com.github.mgrouse.shopbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;
import com.github.mgrouse.shopbot.net.NetTools;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


enum ImportError
{
    NONE, NO_PC_404;
}


public class ImportCommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(ImportCommandHandler.class);

    DataBaseTools m_dBase;

    private SlashCommandInteractionEvent m_event = null;

    private String m_message = "";

    private PlayerCharacter m_pc = null;

    public ImportCommandHandler(DataBaseTools dBase)
    {
	m_dBase = dBase;
    }

    public void doImport(SlashCommandInteractionEvent event)
    {
	m_event = event;

	// get the argument. 'id' = DNDB_NUM
	String dndb_Num = m_event.getOption("id").getAsString();

	// test to see that the string is composed of numbers

	// get User's Discord Name (Not the Koni nick Name)
	String pName = m_event.getUser().getName();

	performImport(pName, dndb_Num);

	outPutImportResults();
    }

    // package function for testing
    ImportError performImport(String pName, String dndb_Num)
    {
	PlayerCharacter dBasePC, webPC;

	// get the PC from the net if any
	webPC = NetTools.getDndbPlayerCharacter(dndb_Num);

	if (null == webPC)
	{
	    // message user that character not found
	    m_message = "PC with Id = " + dndb_Num + " was not found at DND Beyond.";
	    return ImportError.NO_PC_404;
	}

	// Get PC with Player's name and PC's DNDB_NUM
	dBasePC = m_dBase.getPC(pName, dndb_Num);

	// If PC is in database,
	if (null != dBasePC)
	{
	    // Update dBaseCharacter
	    dBasePC.setAvatarURL(webPC.getAvatarURL());
	    dBasePC.setName(webPC.getName());

	    m_dBase.updateCharacter(dBasePC);

	    // message user that character updated
	    m_message = dBasePC.getName() + " was upated.";
	}
	else
	{ // PC is not in DBase

	    // get PC from Database (if they are not there insert them)
	    Player player = m_dBase.findOrCreatePlayer(pName);

	    // Link the PC to the Player
	    m_dBase.associatePlayerAndPC(player, webPC);

	    // Update Player
	    m_dBase.updatePlayer(player);

	    // put PC in Database
	    dBasePC = m_dBase.createCharacter(webPC);

	    // message user that character was imported
	    m_message = dBasePC.getName() + " was imported.";
	}

	m_pc = dBasePC;
	return ImportError.NONE;
    }

    private void outPutImportResults()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar

	m_event.getHook().sendMessage(m_message).queue();
    }
}
