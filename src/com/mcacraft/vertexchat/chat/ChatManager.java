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
    private VertexChat plugin;
    
    private HashMap<String,String> allChannels = new HashMap<>(); //{ChannelName, Nick}
    private HashMap<String,String> allChannelsReverse = new HashMap<>(); //{Nick, ChannelName}
    private HashMap<String,ChatChannel> focusedChannelMap = new HashMap<>(); //{PlayerName, ChatChannelObj}
    private HashMap<String,String> focusedChannelStrMap = new HashMap<>(); //{PlayerName, ChatChannelName}
    private HashMap<String,ChatChannel> channelConverter = new HashMap<>(); //{ChannelName, ChatChannelObj}
    private HashMap<ChatChannel,String> channelConverterRev = new HashMap<>(); //{ChatChannelObj, ChatChannelName}
    private HashMap<String,ArrayList<String>> playerLstnChannels = new HashMap<>(); //{PlayerName, List of all the channels they are listening to}
    
    private HashMap<String,Boolean> chatSilencer = new HashMap<>();
    
    public ChatManager(){}
    
    public ChatManager(VertexChat instance)
    {
        this.plugin = instance;
    }
    
    public void createChannel(String name)
    {
        this.allChannels.put(name, String.valueOf(name.toUpperCase().charAt(0)));
        this.channelConverter.put(name, new ChatChannel());
        this.allChannelsReverse.put(String.valueOf(name.toUpperCase().charAt(0)), name);
        this.channelConverterRev.put(new ChatChannel(), name);
        Bukkit.getLogger().log(Level.INFO, allChannels.toString());
        Bukkit.getLogger().log(Level.INFO, channelConverter.toString());
        Bukkit.getLogger().log(Level.INFO, allChannelsReverse.toString());
        Bukkit.getLogger().log(Level.INFO, channelConverterRev.toString());
        
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
        }
    }
    
    public void deleteChannel(String name)
    {
        this.allChannels.remove(name);
        List<String> channels = plugin.getConfig().getStringList("channels");
        channels.remove(name);
        plugin.getConfig().set("channels", channels);
        plugin.saveConfig();
    }
    
    public boolean channelExists(String channel)
    {
        for(String s : this.allChannels.keySet())
        {
            if(s.equalsIgnoreCase(channel))
            {
                return true;
            }
        }
        return false;
    }
    
    public String getNick(String channel)
    {
        return this.allChannels.get(channel);
    }
    
    public ArrayList<String> getAvaliableChannels()
    {
        ArrayList<String> channels = new ArrayList<>();
        for(String s : this.allChannels.keySet())
        {
            channels.add(s);
        }
        return channels;
    }
    
    public ArrayList<String> getAvaliableChannelNicks()
    {
        ArrayList<String> nicks = new ArrayList<>();
        for(String s : this.allChannels.values())
        {
            nicks.add(s);
        }
        return nicks;
    }
    
    public ChatChannel getFocusedChannel(String pname)
    {
        try
        {
            return this.focusedChannelMap.get(pname);
        }catch(Exception e)
        {
            return null;
        }
    }
    
    public String getFocusedChannelName(String pname)
    {
        return this.focusedChannelStrMap.get(pname);
    }
    
    public void setFocusedChannel(String pname, String channel) //Used Player as param to ensure the player will be online
    {
        this.focusedChannelMap.put(pname, this.getChannel(channel));
        this.getChannel(channel).addPlayer(pname);
        this.focusedChannelStrMap.put(pname, channel);
        
    }
    
    public HashMap<String,ChatChannel> getFocusedChannelMap()
    {
        return this.focusedChannelMap;
    }
    
    //Returns ChatChannelObj
    public ChatChannel getChannel(String name)
    {
        return this.channelConverter.get(name);
    }
    
    public String getChannelName(ChatChannel channel)
    {
        return this.channelConverterRev.get(channel);
    }
    
    public String getChannelFromNick(String nick)
    {
        return this.allChannelsReverse.get(nick);
    }
    
    public String getChannelColor(String channel)
    {
        VConfig chConfig = new VConfig(plugin.getDataFolder()+File.separator+"/channels", channel+".yml", plugin);
        
        return ChatColor.translateAlternateColorCodes('&', chConfig.getConfig().getString("color"));
    }
    
    public void leaveFocusedChannel(String pname)
    {
        this.playerLstnChannels.get(pname).remove(this.getFocusedChannelName(pname));
        this.getFocusedChannel(pname).removePlayer(pname);
        this.setFocusedChannel(pname, this.playerLstnChannels.get(pname).get(0));
    }
    
    public HashMap<String,ArrayList<String>> getListeningChannelsMap()
    {
        return this.playerLstnChannels;
    }
    
    public void addChannelToPlayer(String pname, String channel)
    {
        if(!this.playerLstnChannels.containsKey(pname))
        {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(channel);
            this.playerLstnChannels.put(pname, temp);
        }else
        {
            ArrayList<String> temp = this.playerLstnChannels.get(pname);
            if(temp.contains(channel))
            {
                return;
            }
            temp.add(channel);
            this.playerLstnChannels.put(pname, temp);
        }      
    }
    
    public void setSilentChat(String pname, Boolean activator)
    {
        this.chatSilencer.put(pname, activator);
    }
    
    public Boolean isChatSilenced(String pname)
    {
        if(this.chatSilencer.get(pname))
        {
            return true;
        }else
        {
            return false;
        }
    }
    
    public Boolean isMuted(String pname)
    {
        VConfig mutesConfig = new VConfig(plugin.getDataFolder().getAbsolutePath(), "mutes.yml", plugin);
        List<String> mutes = mutesConfig.getConfig().getStringList("mutes");
        if(mutes.contains(pname))
        {
            return true;
        }
        return false;
    }
}
