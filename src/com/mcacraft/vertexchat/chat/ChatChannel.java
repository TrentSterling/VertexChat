package com.mcacraft.vertexchat.chat;

import com.mcacraft.vertexchat.VertexChat;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Kenny
 */
public class ChatChannel implements Serializable
{
    private VertexChat plugin;
    public ChatChannel(VertexChat instance)
    {
        this.plugin = instance;
    }
    public ChatChannel(){}
    
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
}
