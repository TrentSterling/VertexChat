package com.mcacraft.vertexchat;

import com.mcacraft.vertexchat.chat.ChatChannel;
import com.mcacraft.vertexchat.chat.ChatManager;
import com.mcacraft.vertexchat.commands.ChannelCommand;
import com.mcacraft.vertexchat.commands.Mute;
import com.mcacraft.vertexchat.commands.ReloadOverride;
import com.mcacraft.vertexchat.commands.Silence;
import com.mcacraft.vertexchat.commands.Unmute;
import com.mcacraft.vertexchat.listeners.ChatListener;
import com.mcacraft.vertexchat.listeners.PlayerJoin;
import com.mcacraft.vertexchat.listeners.PlayerQuit;
import com.mcacraft.vertexchat.util.VConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Kenny
 */
public class VertexChat extends JavaPlugin
{
    private VertexChatAPI api = new VertexChatAPI(this);
    private ChannelCommand channelCommand = new ChannelCommand(this);
    private ChatChannel chatChannel = new ChatChannel();
    private PlayerJoin playerJoin = new PlayerJoin(this);
    private ChatListener chatListener = new ChatListener(this);
    private Silence silence = new Silence(this);
    private Mute mute = new Mute(this);
    private Unmute unmute = new Unmute(this);
    private PlayerQuit pq = new PlayerQuit();
    private ReloadOverride ro = new ReloadOverride(this);
    
    public boolean updateChannels = false;
    
    @Override
    public void onEnable()
    {
        ChatManager chatManager = new ChatManager(this);
        setupEvents();
        setupFiles();
        //createDefaultChannel();
        setupChannels();
        VertexChatAPI.reloadChannels();
        ChatManager.setupMuted();
        this.loadListeningChannels();
        this.updateChannels = false;
    }
    
    @Override
    public void onDisable()
    {
        VertexChatAPI.saveFocusedChannels();
        this.saveListeningChannels();
        this.saveMuted();
    }
    
    private void setupEvents()
    {
        PluginManager pm = Bukkit.getPluginManager();
        this.getCommand("ch").setExecutor(this.channelCommand);
        this.getCommand("silence").setExecutor(this.silence);
        this.getCommand("mute").setExecutor(this.mute);
        this.getCommand("unmute").setExecutor(this.unmute);
        pm.registerEvents(this.playerJoin, this);
        pm.registerEvents(this.chatListener, this);
        pm.registerEvents(this.pq, this);
        pm.registerEvents(this.ro, this);
    }
    
    private void setupFiles()
    {
        File f = new File(this.getDataFolder()+File.separator+"config.yml");
        if(!f.exists())
        {
            this.saveDefaultConfig();
        }
    }
    
    private void setupChannels()
    {
        //If channels are currently setup
        if(this.getConfig().contains("channels"))
        {
            //Create all setup channels
            for(String s : this.getConfig().getStringList("channels"))
            {
                ChatManager.createChannel(s);
            }
        }else
        {
            ChatManager.createChannel(this.getConfig().getString("default-channel"));
        }
    }
    
    private void updateChannels()
    {
        if(this.updateChannels)
        {
            VertexChatAPI.reloadChannels();
        }
    }
    
    private void loadListeningChannels()
    {
        HashMap<String, ArrayList<String>> map = new HashMap<>();
        VConfig data = new VConfig(this.getDataFolder()+File.separator+"data", "focused-channels.yml", this);
        for(String s : data.getConfig().getKeys(false))
        {
            ArrayList<String> temp = new ArrayList<>();
            
            for(String str : data.getConfig().getStringList(s))
            {
                temp.add(str);
            }
            map.put(s, temp);
        }
        ChatManager.setListeningMap(map);
    }
    
    private void saveMuted()
    {
        VConfig muted = new VConfig(this.getDataFolder().getAbsolutePath(), "muted.yml", this);
        muted.getConfig().set("muted", ChatManager.getMuted());
        muted.saveConfig();
    }
    
    private void saveListeningChannels()
    {
        VConfig data = new VConfig(this.getDataFolder()+File.separator+"data", "focused-channels.yml", this);
        for(String s : ChatManager.getListeningChannelsMap().keySet())
        {
            List<String> temp = ChatManager.getListeningChannelsMap().get(s);
            data.getConfig().set(s, temp);
        }
    }
    
//    private void createDefaultChannel()
//    {
//        getChatManager().createChannel(this.getConfig().getString("default-channel"));
//    }
    
//    public ChatManager getChatManager()
//    {
//        return this.chatManager;
//    }
//    
//    public VertexChatAPI getAPI()
//    {
//        return this.api;
//    }
}
