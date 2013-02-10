/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcacraft.vertexchat.commands;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author Kenny
 */
public class MeOverride implements Listener
{
    
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        String[] args = event.getMessage().split(" ");
        //String[] meAlias = {"me", "action", "describe", }
    }
    
}
