package com.github.mgrouse.shopbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.Secret;
import com.github.mgrouse.shopbot.database.DataBaseTools;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class CommandDispatcher extends ListenerAdapter
{
    private static Logger m_logger = LoggerFactory.getLogger(CommandDispatcher.class);

    DataBaseTools m_dBase;

    public CommandDispatcher(DataBaseTools tools)
    {
	m_dBase = tools;
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
	Boolean isEphemeral = false;

	// We only have 3 seconds until Discord sends "App not responding"
	event.deferReply(isEphemeral).queue();

	// if we are on the correct channel
	if (event.getChannel().getName().contentEquals(Secret.SHOP_CHANNEL))
	{

	    switch (event.getName())
	    {
		case "import":
		{
		    ImportCommandHandler icHandler = new ImportCommandHandler(m_dBase);
		    icHandler.doImport(event);
		}
		case "character":
		{
		    CharacterCommandHandler ccHandler = new CharacterCommandHandler(m_dBase);
		    ccHandler.doChar(event);
		}
		case "buy":
		{
		    CharacterCommandHandler ccHandler = new CharacterCommandHandler(m_dBase);
		    ccHandler.doChar(event);
		}
		default:
		{
		    m_logger.info("Unknown Slash Command. CommandHandler.java");
		}
	    }// switch
	} // if channel
	else
	{
	    event.getHook().sendMessage("You must be on the '#shop_purchases' channel to interract with the ShopBot.")
		    .queue();
	}
    }


}
