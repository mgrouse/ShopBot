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

	switch (event.getName())
	{
	    case "import":
	    {
		ImportCommandHandler icHandler = new ImportCommandHandler(m_dBase);
		icHandler.go(event);
		break;
	    }
	    case "character":
	    {
		CharacterCommandHandler ccHandler = new CharacterCommandHandler(m_dBase);
		ccHandler.go(event);
		break;
	    }
	    case "buy":
	    {
		// if we are on the correct channel
		if (event.getChannel().getName().contentEquals(Secret.SHOP_CHANNEL))
		{
		    BuyCommandHandler bcHandler = new BuyCommandHandler(m_dBase);
		    bcHandler.go(event);
		}
		else
		{
		    event.getHook()
			    .sendMessage("You must be on the #shop-purchases channel to interract with the ShopBot.")
			    .queue();
		}
		break;
	    }
	    case "gold":
	    {
		// if we are on the correct channel
		if (event.getChannel().getName().contentEquals(Secret.SHOP_CHANNEL))
		{
		    GoldCommandHandler gcHandler = new GoldCommandHandler(m_dBase);
		    gcHandler.go(event);
		}
		else
		{
		    event.getHook()
			    .sendMessage("You must be on the #shop-purchases channel to interract with the ShopBot.")
			    .queue();
		}
		break;
	    }
	    default:
	    {
		m_logger.info("Unknown Slash Command. CommandHandler.java");
		event.getHook().sendMessage("ShopBot does not recognize that slash command.").queue();
	    }
	}// switch
    }


}
