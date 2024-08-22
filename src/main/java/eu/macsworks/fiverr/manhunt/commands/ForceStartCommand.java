package eu.macsworks.fiverr.manhunt.commands;

import eu.macsworks.fiverr.manhunt.Manhunt;
import eu.macsworks.premium.macslibs.objects.MacsCommand;

public class ForceStartCommand extends MacsCommand {

	public ForceStartCommand() {
		super("forcestart");

		setAcceptsNoArgs(true);
		setUsage("Force starts the game");
		setRequiredArgs("");
		setRequiredPerm("manhunt.admin");

		setDefaultBehavior((player, args) -> {
			Manhunt.getInstance().getGameManager().startGame();
			player.sendMessage(Manhunt.getInstance().getMacsPluginLoader().getLang("game-started"));
		});
	}

}
