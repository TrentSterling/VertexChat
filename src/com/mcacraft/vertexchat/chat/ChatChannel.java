package com.mcacraft.vertexchat.chat;

import com.mcacraft.vertexchat.VertexChat;
import com.mcacraft.vertexchat.util.VConfig;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.ChatColor;

/**
 *
 * @author Kenny
 */
public class ChatChannel
{
    public ChatChannel(){}
    
    private String name;
    private String color;
    private String nick;
    private String format;
    private String defaultFormat;
    private String password;
    private boolean verbose;
    private VertexChat plugin;
    private VConfig config;
    
    public ChatChannel(VertexChat instance, String channelName)
    {
        this.plugin = instance;
        this.name = channelName;
        this.config = new VConfig(plugin.getDataFolder()+File.separator+"channels", channelName+".yml", plugin);
        this.color = ChatColor.translateAlternateColorCodes('&', config.getConfig().getString("color"));
        this.nick = config.getConfig().getString("nick");
        this.format = config.getConfig().getString("format");
        this.defaultFormat = plugin.getConfig().getString("format.default");
        this.password = config.getConfig().getString("password");
        this.verbose = config.getConfig().getBoolean("verbose");
    }
    
    private ArrayList<String> chatters = new ArrayList<>();
    
    public ArrayList<String> getPlayers()
    {
        return this.chatters;
    }
    
    public void addPlayer(String name)
    {
        if(chatters.contains(name))
        {
            return;
        }
        this.chatters.add(name);
    }
    
    public void removePlayer(String name)
    {
        if(chatters.contains(name))
        {
            chatters.remove(name);
        }
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setNick(String nick)
    {
        this.nick = nick;
    }
    
    public String getNick()
    {
        return this.nick;
    }
    
    public void setColor(String color)
    {
        this.color = color;
    }
    
    public String getColor()
    {
        return this.color;
    }
    
    public boolean isVerbose()
    {
        if(this.verbose)
        {
            return true;
        }
        return false;
    }
    
    public String getFormat()
    {
        if(this.format.equalsIgnoreCase("%default%"))
        {
            return this.defaultFormat;
        }
        return this.format;
    }
    
    public void setFormat(String format)
    {
        this.format = format;
    }
}
