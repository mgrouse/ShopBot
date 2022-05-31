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
		ImportCommandHandler importHandler = new ImportCommandHandler(m_dBase);
		importHandler.go(event);
		break;
	    }
	    case "remove":
	    {
		RemoveCommandHandler removeHandler = new RemoveCommandHandler(m_dBase);
		removeHandler.go(event);
		break;
	    }
	    case "character":
	    {
		CharacterCommandHandler characterHandler = new CharacterCommandHandler(m_dBase);
		characterHandler.go(event);
		break;
	    }
	    case "abort":
	    {
		AbortCommandHandler abortHandler = new AbortCommandHandler(m_dBase);
		abortHandler.go(event);
		break;
	    }
	    case "buy":
	    {
		// if we are on the correct channel
		if (event.getChannel().getName().contentEquals(Secret.SHOP_CHANNEL))
		{
		    BuyCommandHandler buyHandler = new BuyCommandHandler(m_dBase);
		    buyHandler.go(event);
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
		    GoldCommandHandler goldHandler = new GoldCommandHandler(m_dBase);
		    goldHandler.go(event);
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
