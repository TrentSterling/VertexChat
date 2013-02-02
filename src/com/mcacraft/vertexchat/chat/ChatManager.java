package com.mcacraft.vertexchat.chat;

import com.mcacraft.vertexchat.VertexChat;
import com.mcacraft.vertexchat.util.VConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 *
 * @author Kenny
 */
public class ChatManager 
{
    private static VertexChat plugin;
    
    private static HashMap<String,String> allChannels = new HashMap<>(); //{ChannelName, Nick}
    private static HashMap<String,String> allChannelsReverse = new HashMap<>(); //{Nick, ChannelName}
    private static HashMap<String,ChatChannel> focusedChannelMap = new HashMap<>(); //{PlayerName, ChatChannelObj}
    private static HashMap<String,String> focusedChannelStrMap = new HashMap<>(); //{PlayerName, ChatChannelName}
    private static HashMap<String,ChatChannel> channelConverter = new HashMap<>(); //{ChannelName, ChatChannelObj}
    private static HashMap<ChatChannel,String> channelConverterRev = new HashMap<>(); //{ChatChannelObj, ChatChannelName}
    private static HashMap<String,ArrayList<String>> playerLstnChannels = new HashMap<>(); //{PlayerName, List of all the channels they are listening to}
    
    private static HashMap<String,Boolean> chatSilencer = new HashMap<>();
    private static List<String> isMuted;
    
    public ChatManager(){}
    
    public ChatManager(VertexChat instance)
    {
        ChatManager.plugin = instance;
    }
    
    public static void createChannel(String name)
    {
        File f = new File(plugin.getDataFolder()+File.separator+"channels"+File.separator+name+".yml");
        if(!f.exists())
        {
            List<String> temp;
            if(plugin.getConfig().contains("channels"))
            {
                temp = plugin.getConfig().getStringList("channels");
                temp.add(name);
                plugin.getConfig().set("channels", temp);
                plugin.saveConfig();
            }else
            {
                temp = new ArrayList<>();
                temp.add(name);
                plugin.getConfig().set("channels", temp);
                plugin.saveConfig();
            }
            VConfig config = new VConfig(plugin.getDataFolder()+File.separator+"channels", name+".yml", plugin);
            config.getConfig().set("name", name);
            config.getConfig().set("nick", name.toUpperCase().charAt(0));
            config.getConfig().set("format", "%default%");
            config.getConfig().set("password", "");
            config.getConfig().set("color", "&f");
            config.getConfig().set("verbose", Boolean.TRUE);
            config.saveConfig();
            ChatChannel channel = new ChatChannel(plugin, name);
        }
        if(plugin == null)
        {
            System.out.println("Null plugin");
        }
        ChatChannel channel = new ChatChannel(plugin, name);
        
        ChatManager.allChannels.put(name, String.valueOf(name.toUpperCase().charAt(0)));
        ChatManager.channelConverter.put(name, channel);
        ChatManager.allChannelsReverse.put(String.valueOf(name.toUpperCase().charAt(0)), name);
        ChatManager.channelConverterRev.put(channel, name);

        Bukkit.getLogger().log(Level.INFO, allChannels.toString());
        Bukkit.getLogger().log(Level.INFO, channelConverter.toString());
        Bukkit.getLogger().log(Level.INFO, allChannelsReverse.toString());
        Bukkit.getLogger().log(Level.INFO, channelConverterRev.toString());
    }
    
    public static void deleteChannel(String name)
    {
        ChatManager.allChannels.remove(name);
        List<String> channels = plugin.getConfig().getStringList("channels");
        channels.remove(name);
        plugin.getConfig().set("channels", channels);
        plugin.saveConfig();
    }
    
    public static boolean channelExists(String channel)
    {
        for(String s : ChatManager.allChannels.keySet())
        {
            if(s.equalsIgnoreCase(channel))
            {
                return true;
            }
        }
        return false;
    }
    
    public static String getNick(String channel)
    {
        return ChatManager.allChannels.get(channel);
    }
    
    public static ArrayList<String> getAvaliableChannels()
    {
        ArrayList<String> channels = new ArrayList<>();
        for(String s : ChatManager.allChannels.keySet())
        {
            channels.add(s);
        }
        return channels;
    }
    
    public static ArrayList<String> getAvaliableChannelNicks()
    {
        ArrayList<String> nicks = new ArrayList<>();
        for(String s : ChatManager.allChannels.values())
        {
            nicks.add(s);
        }
        return nicks;
    }
    
    public static ChatChannel getFocusedChannel(String pname)
    {
        try
        {
            return ChatManager.focusedChannelMap.get(pname);
        }catch(Exception e)
        {
            return null;
        }
    }
    
    public static String getFocusedChannelName(String pname)
    {
        return ChatManager.focusedChannelStrMap.get(pname);
    }
    
    public static void setFocusedChannel(String pname, String channel) //Used Player as param to ensure the player will be online
    {
        ChatManager.focusedChannelMap.put(pname, ChatManager.getChannel(channel));
        ChatManager.getChannel(channel).addPlayer(pname);
        ChatManager.focusedChannelStrMap.put(pname, channel);
        
    }
    
    public static HashMap<String,ChatChannel> getFocusedChannelMap()
    {
        return ChatManager.focusedChannelMap;
    }
    
    //Returns ChatChannelObj
    public static ChatChannel getChannel(String name)
    {
        return ChatManager.channelConverter.get(name);
    }
    
    public static String getChannelName(ChatChannel channel)
    {
        return ChatManager.channelConverterRev.get(channel);
    }
    
    public static String getChannelFromNick(String nick)
    {
        return ChatManager.allChannelsReverse.get(nick);
    }
    
    public static String getChannelColor(String channel)
    {
        VConfig chConfig = new VConfig(plugin.getDataFolder()+File.separator+"/channels", channel+".yml", plugin);
        
        return ChatColor.translateAlternateColorCodes('&', chConfig.getConfig().getString("color"));
    }
    
    public static void leaveFocusedChannel(String pname)
    {
        ChatManager.playerLstnChannels.get(pname).remove(ChatManager.getFocusedChannelName(pname));
        ChatManager.getFocusedChannel(pname).removePlayer(pname);
        ChatManager.setFocusedChannel(pname, ChatManager.playerLstnChannels.get(pname).get(0));
    }
    
    public static HashMap<String,ArrayList<String>> getListeningChannelsMap()
    {
        return ChatManager.playerLstnChannels;
    }
    
    public static void addChannelToPlayer(String pname, String channel)
    {
        if(!ChatManager.playerLstnChannels.containsKey(pname))
        {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(channel);
            ChatManager.playerLstnChannels.put(pname, temp);
        }else
        {
            ArrayList<String> temp = ChatManager.playerLstnChannels.get(pname);
            if(temp.contains(channel))
            {
                return;
            }
            temp.add(channel);
            ChatManager.playerLstnChannels.put(pname, temp);
        }
    }
    
    public static void setListeningMap(HashMap<String,ArrayList<String>> map)
    {
        ChatManager.playerLstnChannels = map;
    }
    
    public static void setSilentChat(String pname, Boolean activator)
    {
        ChatManager.chatSilencer.put(pname, activator);
    }
    
    public static Boolean isChatSilenced(String pname)
    {
        if(!ChatManager.chatSilencer.get(pname))
        {
            return false;
        }else
        {
            return true;
        }
    }
    
    public static Boolean isMuted(String pname)
    {
        if(ChatManager.isMuted.contains(pname))
        {
            return true;
        }
        return false;
    }
    
    public static void mute(String pname)
    {
        ChatManager.isMuted.add(pname);
    }
    
    public static void unmute(String pname)
    {
        ChatManager.isMuted.remove(pname);
    }
    
    public static List<String> getMuted()
    {
        return ChatManager.isMuted;
    }
    
    public static void setMutedList(List<String> mutedPlayers)
    {
        ChatManager.isMuted = mutedPlayers;
    }
    
    public static Boolean hasChannel(String pname)
    {
        if(!ChatManager.focusedChannelStrMap.containsKey(pname))
        {
            return false;
        }else
        {
            return true;
        }
    }
    
    public static String getDefaultChannel()
    {
        return plugin.getConfig().getString("default-channel");
    }
    
    public static void reloadChannels()
    {
        for(String s : plugin.getConfig().getStringList("channels"))
        {
            ChatManager.createChannel(s);
        }
    }
    
    public static void setupMuted()
    {
        VConfig muted = new VConfig(plugin.getDataFolder().getAbsolutePath(), "muted.yml", plugin);
        if(muted.getConfig().contains("muted"))
        {
            ChatManager.isMuted = muted.getConfig().getStringList("muted");
            return;
        }
        ChatManager.isMuted = new ArrayList<>();
    }
}
