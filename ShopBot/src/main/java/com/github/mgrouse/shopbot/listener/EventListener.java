package com.github.mgrouse.shopbot.listener;

import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class EventListener extends ListenerAdapter
{

    private static final String APP_NAME = "!ShopBot";

    private CommandHandler comHandler;

    public EventListener()
    {
	comHandler = new CommandHandler();
    }


//    @Override
//    public void onMessageReceived(MessageReceivedEvent event)
//    {
//
//	String[] args = event.getMessage().getContentRaw().split(" ");
//
//	if (args[0].contentEquals(APP_NAME))
//	{
//	    if (args.length >= 3)
//	    {
//
//		switch (args[1])
//		{
//		    case "-import":
//		    {
//			comHandler.handleImport(event, args);
//			break;
//		    }
//		    default:
//		    {
//			// print error no such command
//			event.getChannel()
//				.sendMessage("Error: ShopBot was called with an unsupported command: " + args[1])
//				.queue();
//		    } // default
//		}// switch
//	    } // if long enough args
//	    else
//	    {
//		event.getChannel().sendMessage("Error: ShopBot was called with too few args").queue();
//	    }
//	} // if APP_NAME
//
//    }

}
