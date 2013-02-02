package com.mcacraft.vertexchat;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.mcacraft.vertexchat.chat.ChatManager;
import com.mcacraft.vertexchat.util.VConfig;
import de.bananaco.bpermissions.api.CalculableType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Kenny
 */
public class VertexChatAPI 
{
    private static VertexChat plugin;
    private static VConfig users;
    private static VConfig groups;
        
    public VertexChatAPI(){}
    
    public VertexChatAPI(VertexChat instance)
    {
        VertexChatAPI.plugin = instance;
    }
    
    public static String parseFormat(String message, Player p, String channel)
    {        
        String output = ChatManager.getChannel(channel).getFormat();
        
        String color = ChatManager.getChannel(channel).getColor();
        String world = p.getWorld().getName();
        String focusedChannel = ChatManager.getFocusedChannelName(p.getName());
        String cNick = ChatManager.getChannel(channel).getNick();
        
        String temp = output.replaceAll("%player%", p.getDisplayName()).replaceAll("%prefix%", getPrefix(p.getName(), world)).replaceAll("%suffix%", getSuffix(p.getName(), world)).replaceAll("%color%", color).replaceAll("%channel%", focusedChannel).replaceAll("%faction%", VertexChatAPI.parseFaction(p)).replaceAll("%nick%", cNick);
        
        if(p.hasPermission("vertexchat.color"))
        {
            return ChatColor.translateAlternateColorCodes('&', temp+" "+message);
        }else
        {
            return ChatColor.translateAlternateColorCodes('&', temp)+" "+message;
        }
    }
    
    public static String logParseFormat(String message, Player p, String channel)
    {
        String output = ChatManager.getChannel(channel).getFormat();
        
        //Some variahbles so the parsing line isnt so long
        String color = ChatManager.getChannel(channel).getColor();
        String world = p.getWorld().getName();
        String focusedChannel = ChatManager.getFocusedChannelName(p.getName());
        String cNick = ChatManager.getChannel(channel).getNick();
        
        //There has to be a better way to do this..
        String temp = output.replaceAll("%player%", p.getDisplayName()).replaceAll("%prefix%", getPrefix(p.getName(), world)).replaceAll("%suffix%", getSuffix(p.getName(), world)).replaceAll("%color%", color).replaceAll("%channel%", focusedChannel).replaceAll("%faction%", VertexChatAPI.parseFaction(p)).replaceAll("%nick%", cNick);
        String replaceAll = temp+" "+message;
        
        return VertexChatAPI.removeColorCodes(replaceAll);
        
    }
    
    public static String removeColorCodes(String input)
    {
        return input.replaceAll("&[a-fk-r0-9]", "");
    }
    
    public static String getGroup(String player, String world)
    {
        String[] groupsList = de.bananaco.bpermissions.api.ApiLayer.getGroups(null, CalculableType.USER, player);
        return groupsList[0];
    }
    
    public static String getPrefix(String player, String world)
    {
        return de.bananaco.bpermissions.api.ApiLayer.getValue(world, CalculableType.GROUP, getGroup(player, world), "prefix");
    }
    
    public static String getSuffix(String player, String world)
    {
        return de.bananaco.bpermissions.api.ApiLayer.getValue(world, CalculableType.GROUP, getGroup(player, world), "suffix");
    }
    
    private static String parseFaction(Player p)
    {
        if(Bukkit.getPluginManager().getPlugin("Factions") == null)
        {
            return "";
        }else
        {
            FPlayer fplayer = FPlayers.i.get(p);
            if(fplayer.hasFaction())
            {
                return plugin.getConfig().getString("format.parse-faction").replaceAll("%ftag%", fplayer.getChatTag());
            }else
            {
                return "";
            }
        }
    }
    
    public static void reloadConfiguration()
    {
        plugin.reloadConfig();
        for(String s : ChatManager.getAvaliableChannels())
        {
            VConfig temp = new VConfig(plugin.getDataFolder()+File.separator+"channels", s+".yml", plugin);
            temp.reloadConfig();
        }
        VConfig mutes = new VConfig(plugin.getDataFolder().getAbsolutePath(), "mutes.yml", plugin);
        mutes.getConfig().set("muted", ChatManager.getMuted());
        mutes.saveConfig();
        ChatManager.reloadChannels();
    }
    
    public static void logToFile(String message)
    {
        File logFile = new File(plugin.getDataFolder(), "log.txt");
        FileWriter fw;
        try {
            fw = new FileWriter(logFile, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(message);
            pw.flush();
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, ex.getLocalizedMessage());
        }
    }
    
    public static void saveFocusedChannels()
    {
        VConfig data = new VConfig(plugin.getDataFolder()+File.separator+"data", "focused-channels.yml", plugin);
        for(String s : ChatManager.getListeningChannelsMap().keySet())
        {
            data.getConfig().set(s, ChatManager.getListeningChannelsMap().get(s));
            data.saveConfig();
        }
    }
    
    
    //Only to be used for when the server reloads
    public static void reloadChannels()
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            if(!ChatManager.hasChannel(p.getName()))
            {
                VConfig data = new VConfig(plugin.getDataFolder()+File.separator+"data", "focused-channels.yml", plugin);
                for(String s : data.getConfig().getStringList(p.getName()))
                {
                    ChatManager.getChannel(s).addPlayer(p.getName());
                    ChatManager.addChannelToPlayer(p.getName(), s);
                }
                ChatManager.setFocusedChannel(p.getName(), ChatManager.getDefaultChannel());
                ChatManager.setSilentChat(p.getName(), Boolean.FALSE);
            }
        }
    }
    
}