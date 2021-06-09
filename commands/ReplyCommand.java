package net.dev.bungeefriends.commands;

import net.dev.bungeefriends.sql.FriendManager;
import net.dev.bungeefriends.utils.FileUtils;
import net.dev.bungeefriends.utils.FriendMessageUtils;
import net.dev.bungeefriends.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class ReplyCommand extends Command {

	public ReplyCommand(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Configuration cfg = FileUtils.getConfig();
		
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			
			if(args.length >= 1) {
				if(FriendMessageUtils.chats.containsKey(p)) {
					ProxiedPlayer t = FriendMessageUtils.chats.get(p);
					
					if(t != null) {
						if(FriendManager.isFriend(p.getUniqueId(), t.getUniqueId())) {
							String msg = "";
							
							for (int i = 0; i < args.length; i++) {
								msg += args[i] + " ";
							}
							
							msg = msg.trim();
							
							if(FriendMessageUtils.chats.containsKey(p))
								FriendMessageUtils.chats.remove(p);
							
							if(FriendMessageUtils.chats.containsKey(t))
								FriendMessageUtils.chats.remove(t);
							
							FriendMessageUtils.chats.put(p, t);
							FriendMessageUtils.chats.put(t, p);
					
							p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.MSG.Self").replace("%player%", p.getDisplayName()).replace("%target%", t.getDisplayName()).replace("%message%", msg)));
							t.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.MSG.Other").replace("%player%", p.getDisplayName()).replace("%target%", t.getDisplayName()).replace("%message%", msg)));
						} else {
							p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NotYourFriend").replace("%player%", t.getDisplayName())));
						}
					} else {
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.PlayerNotFound").replace("%player%", args[0])));
					}
				} else {
					p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NoMessageReceived")));
				}
			} else {
				p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.Help.Reply")));
			}
		} else {
			Utils.sendConsole(FriendMessageUtils.prefix + FriendMessageUtils.notPlayer);
		}
	}

}
