package net.dev.bungeefriends.commands;

import net.dev.bungeefriends.party.PartyManager;
import net.dev.bungeefriends.utils.FileUtils;
import net.dev.bungeefriends.utils.PartyMessageUtils;
import net.dev.bungeefriends.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class PartyChatCommand extends Command {

	public PartyChatCommand(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Configuration cfg = FileUtils.getConfig();
		
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			
			if(args.length >= 1) {
				if(PartyManager.isInParty(p)) {
					String msg = "";
					
					for (int i = 0; i < args.length; i++) {
						msg += args[i] + " ";
					}
					
					msg = msg.trim();
					
					for (ProxiedPlayer member : PartyManager.getPlayerParty(p).getMembers()) {
						member.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.PartyChat").replace("%player%", p.getDisplayName()).replace("%message%", msg)));
					}
				} else {
					p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.NotInParty")));
				}
			} else {
				p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.Help.PartyChat")));
			}
		} else {
			Utils.sendConsole(PartyMessageUtils.prefix + PartyMessageUtils.notPlayer);
		}
	}

}
