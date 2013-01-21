package com.mcacraft.vertexchat;

import com.mcacraft.easypromote.EasyPromoteAPI;
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
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Kenny
 */
public class VertexChat extends JavaPlugin
{
    private ChatManager chatManager = new ChatManager(this);
    private VertexChatAPI api = new VertexChatAPI(this);
    private ChannelCommand channelCommand = new ChannelCommand(this);
    private ChatChannel chatChannel = new ChatChannel(this);
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
        setupEvents();
        setupFiles();
        //createDefaultChannel();
        setupChannels();
        VertexChatAPI.reloadChannels();
        this.updateChannels = false;
    }
    
    @Override
    public void onDisable()
    {
        VertexChatAPI.saveFocusedChannels();
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
