package com.mcacraft.vertexchat.commands;

import com.mcacraft.vertexchat.VertexChat;
import com.mcacraft.vertexchat.VertexChatAPI;
import com.mcacraft.vertexchat.chat.ChatManager;
import com.mcacraft.vertexchat.util.MSG;
import com.mcacraft.vertexchat.util.VConfig;
import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Kenny
 */
public class ChannelCommand implements CommandExecutor
{
    private VertexChat plugin;
    
    private ChatColor gold = ChatColor.GOLD;
    private ChatColor blue = ChatColor.BLUE;
        
    public ChannelCommand(VertexChat instance)
    {
        this.plugin = instance;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
    {
        //Add in help dialog with all the commands. Also check un-used arg lengths!
        if(lbl.equalsIgnoreCase("ch"))
        {
            if(args.length == 0)
            {
                sender.sendMessage(ChatColor.GREEN+"==========VertexChat==========");
                sender.sendMessage(ChatColor.BLUE+"Type "+ChatColor.YELLOW+"/ch help"+ChatColor.BLUE+" for a list of commands");
                return true;
            }
            if(args.length > 2)
            {
                sender.sendMessage(ChatColor.RED+"Error: Too many arguments");
                return true;
            }
            
            if(args[0].equalsIgnoreCase("create"))
            {
                if(args.length == 2)
                {
                    if(!sender.hasPermission("vertexchat.create"))
                    {
                        MSG.noPermMessage((Player) sender, "/ch create");
                        return true;
                    }
                    ChatManager.createChannel(args[1]);
                    sender.sendMessage(ChatColor.GREEN+"Success!");
                }else
                {
                    sender.sendMessage(ChatColor.RED+"Usage: /ch create <channel>");
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("delete"))
            {
                //Need to makre sure to kick all players from channel when the channel is deleted
                if(args.length == 1)
                {
                    try
                    {
                        ChatManager.deleteChannel(args[1]);
                    }catch(Exception e)
                    {
                        sender.sendMessage(ChatColor.RED+"Could not delete "+args[0]);
                    }
                }else
                {
                    sender.sendMessage(ChatColor.RED+"Usage: /ch create <channel>");
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("reload"))
            {
                if(args.length == 1)
                {
                    VertexChatAPI.reloadConfiguration();
                    sender.sendMessage(ChatColor.GREEN+"Reload done");
                }else
                {
                    sender.sendMessage(ChatColor.RED+"Usage: /ch reload");
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("leave"))
            {
                if(args.length != 1)
                {
                    sender.sendMessage(ChatColor.RED+"Usage: /ch leave");
                    return true;
                }
                if(ChatManager.getListeningChannelsMap().get(sender.getName()).size() < 2)
                {
                    sender.sendMessage(ChatColor.RED+"Error: You must be focused on one channel. To leave the chat completely type "+ChatColor.YELLOW+"/silence");
                    return true;
                }
                String channelOld = ChatManager.getFocusedChannelName(sender.getName());
                ChatManager.leaveFocusedChannel(sender.getName());
                sender.sendMessage(ChatColor.RED+"You have left "+ChatManager.getChannelColor(channelOld)+channelOld);
                String channelNew = ChatManager.getFocusedChannelName(sender.getName());
                sender.sendMessage(ChatColor.BLUE+"Force joined "+ChatManager.getChannelColor(channelNew)+channelNew);
            }else if(args[0].equalsIgnoreCase("list"))
            {
                if(args.length != 1)
                {
                    sender.sendMessage(ChatColor.RED+"Usage: /ch leave");
                    return true;
                }
                
                String output = "";
                boolean first = true;
                for(String s : ChatManager.getAvaliableChannels())
                {
                    if(!first)
                    {
                        output += ChatColor.WHITE+", ";
                    }else
                    {
                        first = false;
                    }
                    VConfig temp = new VConfig(plugin.getDataFolder()+File.separator+"channels", s+".yml", plugin);
                    output += temp.getConfig().getString("color");
                    output+= s;
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', output));
            }else if(args[0].equalsIgnoreCase("help"))
            {
                if(args.length != 1)
                {
                    sender.sendMessage(ChatColor.RED+"Usage: /ch leave");
                    return true;
                }
                
                if(!sender.hasPermission("vertexchat.help"))
                {
                    MSG.noPermMessage((Player) sender, "/ch help");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN+"==========VertexChat Help==========");
                sender.sendMessage(gold+"/ch"+blue+" Chat base command");
                sender.sendMessage(gold+"/ch reload"+blue+" Reloads the config file");
                sender.sendMessage(gold+"/ch create"+blue+" Creates a chat channel");
                sender.sendMessage(gold+"/ch delete"+blue+" Deletes a channel");
                sender.sendMessage(gold+"/ch leave"+blue+" Leaves a channel");
                sender.sendMessage(gold+"/ch <nick>"+blue+" Joins a channel");
                sender.sendMessage(gold+"/silence"+blue+" Silences the chat");
                sender.sendMessage(gold+"/mute"+blue+" Mutes a player");
                sender.sendMessage(gold+"/unmute"+blue+" Unmutes a player");
                return true;
            }else
            {
                for(String s : ChatManager.getAvaliableChannelNicks())
                {
                    if(s.equalsIgnoreCase(args[0]))
                    {
                        if(ChatManager.getChannelFromNick(s).equalsIgnoreCase(ChatManager.getFocusedChannelName(sender.getName())))
                        {
                            sender.sendMessage(ChatColor.RED+"Error: You are already focused on "+ChatManager.getChannelColor(ChatManager.getFocusedChannelName(sender.getName())) +ChatManager.getFocusedChannelName(sender.getName()));
                            return true;
                        }
                        if(sender instanceof Player)
                        {
                            ChatManager.addChannelToPlayer(sender.getName(), ChatManager.getChannelFromNick(s));
                            ChatManager.setFocusedChannel(sender.getName(), ChatManager.getChannelFromNick(s));
                            sender.sendMessage(ChatColor.GREEN+"Focused on "+ChatManager.getChannelColor(ChatManager.getFocusedChannelName(sender.getName())) +ChatManager.getFocusedChannelName(sender.getName()));
                            return true;
                        }
                    }
                }
                sender.sendMessage(ChatColor.RED+"Unknown argument. Type "+ChatColor.YELLOW+"/ch help"+ChatColor.RED+" for a list of commands.");
            }
        }
        
        return false;
    }

}
