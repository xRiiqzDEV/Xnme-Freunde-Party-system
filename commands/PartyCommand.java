package net.dev.bungeefriends.commands;

import net.dev.bungeefriends.party.Party;
import net.dev.bungeefriends.party.PartyManager;
import net.dev.bungeefriends.party.PartyRank;
import net.dev.bungeefriends.sql.SettingsManager;
import net.dev.bungeefriends.utils.FileUtils;
import net.dev.bungeefriends.utils.PartyMessageUtils;
import net.dev.bungeefriends.utils.Utils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class PartyCommand extends Command {

	public PartyCommand(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Configuration cfg = FileUtils.getConfig();
		
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			boolean showHelp = false;
			
			if(args.length >= 1) {
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("help")) {
						showHelp = true;
					} else if(args[0].equalsIgnoreCase("list")) {
						if(PartyManager.isInParty(p)) {
							Party party = PartyManager.getPlayerParty(p);
							
							p.sendMessage(Utils.getAsBaseComponent(""));
							
							for (ProxiedPlayer member : party.getMembers()) {
								p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + "§e" + member.getDisplayName() + " §7(§r" + party.members.get(member).name() + "§7)"));
							}
							
							p.sendMessage(Utils.getAsBaseComponent(""));
						} else {
							p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.NotInParty")));
						}
					} else if(args[0].equalsIgnoreCase("leave")) {
						if(PartyManager.isInParty(p)) {
							PartyManager.getPlayerParty(p).leaveParty(p);
						} else {
							p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.NotInParty")));
						}
					} else if(args[0].equalsIgnoreCase("toggleinvites")) {
						if(SettingsManager.isGettigPartyInvites(p.getUniqueId())) {
							SettingsManager.setGetPartyInvites(p.getUniqueId(), false);
							p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.DeactivatedInvites")));
						} else {
							SettingsManager.setGetPartyInvites(p.getUniqueId(), true);
							p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.ActivatedInvites")));
						}
					} else {
						showHelp = true;
					}
				} else {
					ProxiedPlayer t = BungeeCord.getInstance().getPlayer(args[1]);
					
					if(args[0].equalsIgnoreCase("accept")) {
						if(t != null) {
							if(PartyManager.isRequestOpen(t, p)) {
								PartyManager.requests.remove(t);
								
								PartyManager.getPlayerParty(t).addMember(p);
								
								p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.Accepted")));

								for (ProxiedPlayer member : PartyManager.getPlayerParty(p).getMembers()) {
									member.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.Joined").replace("%player%", p.getDisplayName())));
								}
							} else {
								p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.NoRequestSent").replace("%player%", t.getDisplayName())));
							}
						} else {
							p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.PlayerNotFound").replace("%player%", args[1])));
						}
					} else if(args[0].equalsIgnoreCase("deny")) {
						if(t != null) {
							if(PartyManager.isRequestOpen(t, p)) {
								PartyManager.requests.remove(t);
								
								p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.Denied")));
								t.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.RequestDenied").replace("%player%", p.getDisplayName())));
							} else {
								p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.NoRequestSent").replace("%player%", t.getDisplayName())));
							}
						} else {
							p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.PlayerNotFound").replace("%player%", args[1])));
						}
					} else {
						if(args[0].equalsIgnoreCase("kick")) {
							if(PartyManager.isInParty(p)) {
								if(t != null) {
									if(PartyManager.getPlayerParty(p).isMember(t)) {
										if((PartyManager.getPlayerParty(p).members.get(p) == PartyRank.MOD) || (PartyManager.getPlayerParty(p).members.get(p) == PartyRank.LEADER)) {
											PartyManager.getPlayerParty(p).leaveParty(t);
											
											p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.Kick").replace("%player%", t.getDisplayName())));
											t.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.Kicked")));
										} else {
											p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.NotLeader")));
										}
									} else {
										p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.PlayerNotInParty").replace("%player%", t.getDisplayName())));
									}
								} else {
									p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.PlayerNotFound").replace("%player%", args[1])));
								}
							} else {
								p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.NotInParty")));
							}
						} else if(args[0].equalsIgnoreCase("promote")) {
							if(PartyManager.isInParty(p)) {
								if(t != null) {
									if(PartyManager.getPlayerParty(p).isMember(t)) {
										if(PartyManager.getPlayerParty(p).members.get(p) == PartyRank.LEADER) {
											PartyManager.getPlayerParty(p).promote(t);
											
											p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.Promoted").replace("%player%", t.getDisplayName()).replace("%newRank%", PartyManager.getPlayerParty(p).members.get(t).name())));
										} else {
											p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.NotLeader")));
										}
									} else {
										p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.PlayerNotInParty").replace("%player%", t.getDisplayName())));
									}
								} else {
									p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.PlayerNotFound").replace("%player%", args[1])));
								}
							} else {
								p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.NotInParty")));
							}
						} else if(args[0].equalsIgnoreCase("demote")) {
							if(PartyManager.isInParty(p)) {
								if(t != null) {
									if(PartyManager.getPlayerParty(p).isMember(t)) {
										if(PartyManager.getPlayerParty(p).members.get(p) == PartyRank.LEADER) {
											PartyManager.getPlayerParty(p).demote(t);
											
											p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.Demoted").replace("%player%", t.getDisplayName()).replace("%newRank%", PartyManager.getPlayerParty(p).members.get(t).name())));
										} else {
											p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.NotLeader")));
										}
									} else {
										p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.PlayerNotInParty").replace("%player%", t.getDisplayName())));
									}
								} else {
									p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.PlayerNotFound").replace("%player%", args[1])));
								}
							} else {
								p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.NotInParty")));
							}
						} else if(!(PartyManager.isInParty(p)) && args[0].equalsIgnoreCase("invite")) {
							if(t != null) {
								if(t != p) {
									if(!(PartyManager.isInParty(t))) {
										if(!(PartyManager.requests.containsKey(p))) {
											if(SettingsManager.isGettigPartyInvites(t.getUniqueId())) {
												PartyManager.requests.put(p, t);
												
												PartyManager.parties.add(new Party(p));
												
												p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.Invited").replace("%player%", args[1])));
												
												TextComponent message = new TextComponent(PartyMessageUtils.prefix + "     ");
												
												TextComponent accept = new TextComponent("§8[§aACCEPT");
												accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + p.getName()));
												message.addExtra(accept);
												
												message.addExtra(new TextComponent(" §7| "));
												
												TextComponent deny = new TextComponent("§cDENY§8]");
												deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + p.getName()));
												message.addExtra(deny);
												
												t.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.PlayerInvite").replace("%player%", p.getDisplayName())));
												t.sendMessage(message);
											} else {
												p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.NotAllowingInvites")));
											}
										} else {
											p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.AlreadySentARequest")));
										}
									} else {
										p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.AlreadyInParty").replace("%player%", t.getDisplayName())));
									}
								} else {
									p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.CantInteractSelf")));
								}
							} else {
								p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.PlayerNotFound").replace("%player%", args[1])));
							}
						} else if(PartyManager.isInParty(p) && args[0].equalsIgnoreCase("invite")) {
							if(t != null) {
								if(t != p) {
									if((PartyManager.getPlayerParty(p).members.get(p) == PartyRank.MOD) || (PartyManager.getPlayerParty(p).members.get(p) == PartyRank.LEADER)) {
										if(!(PartyManager.isInParty(t))) {
											if(!(PartyManager.requests.containsKey(p))) {
												PartyManager.requests.put(p, t);
												
												p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.Invited").replace("%player%", args[1])));
												
												TextComponent accept = new TextComponent(PartyMessageUtils.prefix + "§aACCEPT");
												accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + p.getName()));
												
												TextComponent deny = new TextComponent(PartyMessageUtils.prefix + "§cDENY");
												deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + p.getName()));
												
												t.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.PlayerInvite").replace("%player%", p.getDisplayName())));
												t.sendMessage(accept);
												t.sendMessage(deny);
											} else {
												p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.AlreadySentARequest")));
											}
										} else {
											p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.AlreadyInParty").replace("%player%", t.getDisplayName())));
										}
									} else {
										p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.NotLeader")));
									}
								} else {
									p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.CantInteractSelf")));
								}
							} else {
								p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString("Messages.Party.PlayerNotFound").replace("%player%", args[1])));
							}
						} else {
							showHelp = true;
						}
					}
				}
			} else {
				showHelp = true;
			}
			
			if(showHelp) {
				for (int i = 1; i <= Integer.MAX_VALUE; i++) {
					String path = "Messages.Party.Help.Line" + i;
					
					if(cfg.contains(path))
						p.sendMessage(Utils.getAsBaseComponent(PartyMessageUtils.prefix + cfg.getString(path)));
					else
						break;
				}
			}
		} else {
			Utils.sendConsole(PartyMessageUtils.prefix + PartyMessageUtils.notPlayer);
		}
	}

}
