package com.github.mgrouse.shopbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class CommandHandler extends ListenerAdapter
{
    private static Logger m_logger = LoggerFactory.getLogger(CommandHandler.class);

    public CommandHandler()
    {

    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
	switch (event.getName())
	{
	    case "import":
	    {
		ImportCommandHandler icHandler = new ImportCommandHandler();
		icHandler.doImport(event);
	    }
	    case "char":
	    {
		CharacterCommandHandler ccHandler = new CharacterCommandHandler();
		ccHandler.doChar(event);
	    }
	    default:
	    {
		m_logger.info("Unknown Slash Command. CommandHandler.java");
	    }
	}
    }


}
