package net.dev.bungeefriends.commands;

import java.util.UUID;

import net.dev.bungeefriends.sql.FriendManager;
import net.dev.bungeefriends.sql.SettingsManager;
import net.dev.bungeefriends.utils.FileUtils;
import net.dev.bungeefriends.utils.FriendMessageUtils;
import net.dev.bungeefriends.utils.Utils;
import net.dev.bungeefriends.uuidfetching.UUIDFetcher;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class FriendCommand extends Command {

	public FriendCommand(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Configuration cfg = FileUtils.getConfig();
		
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			boolean showHelp = false;
			
			if(args.length >= 1) {
				if(args[0].equalsIgnoreCase("help")) {
					showHelp = true;
				} else if(args[0].equalsIgnoreCase("list")) {
					if(FriendManager.getFriends(p.getUniqueId()).size() >= 1) {
						for (UUID uuid : FriendManager.getFriends(p.getUniqueId())) {
							String name = UUIDFetcher.getName(uuid);
							String status = (BungeeCord.getInstance().getPlayer(uuid) != null) ? "§aOnline" : "§cOffline";
							
							p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + "§e" + name + " §7(§r" + status + "§7)"));
						}
					} else {
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NoFriends")));
					}
				} else if(args[0].equalsIgnoreCase("requests")) {
					if(FriendManager.getRequests(p.getUniqueId()).size() >= 1) {
						for (UUID uuid : FriendManager.getRequests(p.getUniqueId())) {
							String name = UUIDFetcher.getName(uuid);
							
							p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + "§e" + name + ""));
						}
					} else {
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NoRequestOpen")));
					}
				} else if(args[0].equalsIgnoreCase("acceptall")) {
					if(FriendManager.getRequests(p.getUniqueId()).size() >= 1) {
						for(UUID requested : FriendManager.getRequests(p.getUniqueId())) {
							BungeeCord.getInstance().getPluginManager().dispatchCommand(p, "friend accept " + UUIDFetcher.getName(requested));
						}
					} else {
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NoRequestOpen")));
					}
				} else if(args[0].equalsIgnoreCase("denyall")) {
					if(FriendManager.getRequests(p.getUniqueId()).size() >= 1) {
						for(UUID requested : FriendManager.getRequests(p.getUniqueId())) {
							BungeeCord.getInstance().getPluginManager().dispatchCommand(p, "friend deny " + UUIDFetcher.getName(requested));
						}
					} else {
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NoRequestOpen")));
					}
				} else if(args[0].equalsIgnoreCase("clear")) {
					if(FriendManager.getFriends(p.getUniqueId()).size() >= 1) {
						for(UUID uuid : FriendManager.getFriends(p.getUniqueId())) {
							BungeeCord.getInstance().getPluginManager().dispatchCommand(p, "friend remove " + UUIDFetcher.getName(uuid));
						}
					} else {
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NoFriends")));
					}
				} else if(args[0].equalsIgnoreCase("togglerequests")) {
					if(SettingsManager.isGettingRequests(p.getUniqueId())) {
						SettingsManager.setGetRequests(p.getUniqueId(), false);
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.DeactivatedRequests")));
					} else {
						SettingsManager.setGetRequests(p.getUniqueId(), true);
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.ActivatedRequests")));
					}
				} else if(args[0].equalsIgnoreCase("togglenotify")) {
					if(SettingsManager.isGettingNotified(p.getUniqueId())) {
						SettingsManager.setGetNotified(p.getUniqueId(), false);
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.DeactivatedNotify")));
					} else {
						SettingsManager.setGetNotified(p.getUniqueId(), true);
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.ActivatedNotify")));
					}
				} else if(args[0].equalsIgnoreCase("togglemessages")) {
					if(SettingsManager.isUsingFriendChat(p.getUniqueId())) {
						SettingsManager.setUseFriendChat(p.getUniqueId(), false);
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.DeactivatedFriendChat")));
					} else {
						SettingsManager.setUseFriendChat(p.getUniqueId(), true);
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.ActivatedFriendChat")));
					}
				} else if(args[0].equalsIgnoreCase("togglejump")) {
					if(SettingsManager.isAllowingServerJumping(p.getUniqueId())) {
						SettingsManager.setServerJuming(p.getUniqueId(), false);
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.DeactivatedServerJumping")));
					} else {
						SettingsManager.setServerJuming(p.getUniqueId(), true);
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.ActivatedServerJumping")));
					}
				} else {
					if(args.length >= 2) {
						ProxiedPlayer t = BungeeCord.getInstance().getPlayer(args[1]);
						
						if(args[0].equalsIgnoreCase("add")) {
							if(t != null) {
								if(t != p) {
									if(!(FriendManager.isFriend(p.getUniqueId(), t.getUniqueId()))) {
										if(!(FriendManager.isRequestOpen(p.getUniqueId(), t.getUniqueId()))) {
											if(SettingsManager.isGettingRequests(t.getUniqueId())) {
												if((p.hasPermission("friend.100") && (FriendManager.getFriends(p.getUniqueId()).size() < 100) || (FriendManager.getFriends(p.getUniqueId()).size() < 50) || p.hasPermission("friend.unlimited"))) {
													FriendManager.addRequest(p.getUniqueId(), t.getUniqueId());
													
													p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.MessageSent").replace("%player%", args[1])));
													
													TextComponent message = new TextComponent(FriendMessageUtils.prefix + "     ");
													
													TextComponent accept = new TextComponent("§8[§aACCEPT");
													accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + p.getName()));
													message.addExtra(accept);
													
													message.addExtra(new TextComponent(" §7| "));
													
													TextComponent deny = new TextComponent("§cDENY§8]");
													deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + p.getName()));
													message.addExtra(deny);
													
													t.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.PlayerInvite").replace("%player%", p.getDisplayName())));
													t.sendMessage(message);
												} else {
													p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.TooManyFriends")));
												}
											} else {
												p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NotAllowingRequests").replace("%player%", t.getDisplayName())));
											}
										} else {
											p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.AlreadySentARequest")));
										}
									} else {
										p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.AlreadyFriend").replace("%player%", t.getDisplayName())));
									}
								} else {
									p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.CantInteractSelf")));
								}
							} else {
								p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.PlayerNotFound").replace("%player%", args[1])));
							}
						} else if(args[0].equalsIgnoreCase("remove")) {
							UUID uuid = UUIDFetcher.getUUID(args[1]);
							
							if(FriendManager.isFriend(p.getUniqueId(), uuid)) {
								FriendManager.removeFriend(p.getUniqueId(), uuid);
								FriendManager.removeFriend(uuid, p.getUniqueId());
								
								p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.Removed").replace("%player%", args[1])));
								
								if(t != null) {
									t.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NotFriendAnymore").replace("%player%", p.getName())));
								}
							} else {
								p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NotYourFriend").replace("%player%", args[1])));
							}
						} else if(args[0].equalsIgnoreCase("accept")) {
							UUID uuid;
							
							if(t != null) {
								uuid = t.getUniqueId();
							} else {
								uuid = UUIDFetcher.getUUID(args[1]);
							}
							
							if(FriendManager.isRequestOpen(uuid, p.getUniqueId())) {
								if((p.hasPermission("friend.100") && (FriendManager.getFriends(p.getUniqueId()).size() < 100) || (FriendManager.getFriends(p.getUniqueId()).size() < 50) || p.hasPermission("friend.unlimited"))) {
									FriendManager.removeRequest(uuid, p.getUniqueId());
									FriendManager.addFriend(p.getUniqueId(), uuid);
									FriendManager.addFriend(uuid, p.getUniqueId());
									
									p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.Accepted")));
									p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NowYourFriend").replace("%player%", args[1])));
									
									if(t != null)
										t.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NowYourFriend").replace("%player%", p.getDisplayName())));
								} else {
									p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.TooManyFriends")));
								}
							} else {
								p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NoRequestSent").replace("%player%",  args[1])));
							}
						} else if(args[0].equalsIgnoreCase("deny")) {
							UUID uuid;
							
							if(t != null) {
								uuid = t.getUniqueId();
							} else {
								uuid = UUIDFetcher.getUUID(args[1]);
							}
							
							if(FriendManager.isRequestOpen(uuid, p.getUniqueId())) {
								FriendManager.removeRequest(uuid, p.getUniqueId());
								
								p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.Denied")));
								
								if(t != null)
									t.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.RequestDenied").replace("%player%", p.getDisplayName())));
							} else {
								p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NoRequestSent").replace("%player%", args[1])));
							}
						} else if(args[0].equalsIgnoreCase("jump")) {
							UUID uuid = UUIDFetcher.getUUID(args[1]);
							
							if(t != null) {
								if(FriendManager.isFriend(p.getUniqueId(), uuid)) {
									if(SettingsManager.isAllowingServerJumping(uuid)) {
										p.connect(t.getServer().getInfo());
									
										p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.Jumped").replace("%player%", t.getDisplayName())));
									} else {
										p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NotAllowingServerJumping").replace("%player%", t.getDisplayName())));
									}
								} else {
									p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.NotYourFriend").replace("%player%", t.getDisplayName())));
								}
							} else {
								p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString("Messages.Friend.PlayerNotFound").replace("%player%", args[1])));
							}
						} else {
							showHelp = true;
						}
					} else {
						showHelp = true;
					}
				}
			} else {
				showHelp = true;
			}
			
			if(showHelp) {
				for (int i = 1; i <= Integer.MAX_VALUE; i++) {
					String path = "Messages.Friend.Help.Line" + i;
					
					if(cfg.contains(path))
						p.sendMessage(Utils.getAsBaseComponent(FriendMessageUtils.prefix + cfg.getString(path)));
					else
						break;
				}
			}
		} else {
			Utils.sendConsole(FriendMessageUtils.prefix + FriendMessageUtils.notPlayer);
		}
	}

}
