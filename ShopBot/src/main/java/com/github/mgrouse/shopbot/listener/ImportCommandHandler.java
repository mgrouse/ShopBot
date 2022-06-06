package com.github.mgrouse.shopbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.PlayerCharacter;
import com.github.mgrouse.shopbot.net.NetTools;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class ImportCommandHandler extends CommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(ImportCommandHandler.class);

    private SlashCommandInteractionEvent m_event = null;

    // protected DataBaseTools m_dBase;

    // protected String m_message = "";

    // protected Player m_player = null;

    // protected PlayerCharacter m_pc = null;

    private PlayerCharacter m_webPC = null;


    public ImportCommandHandler(DataBaseTools dBase)
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
	// get the argument. 'id' = DNDB_NUM
	String dndb_Num = m_event.getOption("id").getAsString();

	// get User's Discord Name (Not the Koni nick Name)
	String pName = m_event.getUser().getName();

	performImport(pName, dndb_Num);
    }

    // package function for testing
    AppError performImport(String pName, String dndb_Num)
    {
	AppError err = validate(pName, dndb_Num);

	if (AppError.NONE != err)
	{
	    m_message = err.message();
	    return err;
	}

	// get PLayer from Database (if they are not there insert them)
	m_player = m_dBase.findOrCreatePlayer(pName);

	// Get PC with Player's name and PC's DNDB_NUM
	m_pc = m_dBase.getPC(pName, dndb_Num);

	// If PC IS in database,
	if (null != m_pc)
	{
	    // Update dBaseCharacter
	    m_pc.setAvatarURL(m_webPC.getAvatarURL());
	    m_pc.setName(m_webPC.getName());

	    m_dBase.updateCharacter(m_pc);

	    // message user that character updated
	    m_message = m_pc.getName() + " was upated.";
	}
	else
	{ // PC is NOT in DBase

	    // put PC in Database
	    m_pc = m_dBase.createCharacter(m_webPC);

	    // Link the PC to the Player
	    m_dBase.associatePlayerAndPC(m_player, m_pc);

	    // Update Player
	    m_dBase.updatePlayer(m_player);

	    // Update m_pc
	    m_dBase.updateCharacter(m_pc);

	    // message user that character was imported
	    m_message = m_pc.getName() + " was imported.";
	}
	return AppError.NONE;
    }

    private AppError validate(String pName, String dndb_Num)
    {
	// get the PC from the net if any
	m_webPC = NetTools.getDndbPlayerCharacter(dndb_Num);

	if (null == m_webPC)
	{
	    // message user that character not found
	    return AppError.DNDB_404;
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
