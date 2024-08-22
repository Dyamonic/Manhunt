package eu.macsworks.fiverr.manhunt.commands;

import eu.macsworks.fiverr.manhunt.Manhunt;
import eu.macsworks.premium.macslibs.objects.MacsCommand;

public class SetManhuntLobbyCommand extends MacsCommand {
	public SetManhuntLobbyCommand() {
		super("setmanhuntlobby");

		setAcceptsNoArgs(true);
		setUsage("Sets the lobby location");
		setRequiredArgs("");
		setRequiredPerm("manhunt.admin");

		setDefaultBehavior((player, args) -> {
			Manhunt.getInstance().getMacsPluginLoader().setLobbyLocation(player.getLocation().clone());
			player.sendMessage(Manhunt.getInstance().getMacsPluginLoader().getLang("lobby-set"));
		});
	}
}
