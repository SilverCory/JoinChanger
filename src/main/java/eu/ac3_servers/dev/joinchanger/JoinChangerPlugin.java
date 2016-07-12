package eu.ac3_servers.dev.joinchanger;

import java.io.File;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinChangerPlugin extends JavaPlugin implements Listener {
	
	private HashMap<String, String> joinMessages = new HashMap<>();
	private HashMap<String, String> leaveMessages = new HashMap<>();
	
	private boolean sendIfNone = false;
	
	@Override
	public void onEnable() {
		
		File config = new File(getDataFolder(), "config.yml");
		if(!config.exists()) saveDefaultConfig();
		
		sendIfNone = getConfig().getBoolean("send_if_none", false);
		
		ConfigurationSection section = getConfig().getConfigurationSection("groups");
		for(String key : section.getKeys(false))
			joinMessages.put(key.toUpperCase(), colour(section.getString(key)));
			
		section = getConfig().getConfigurationSection("leaveGroups");
		for(String key : section.getKeys(false))
			leaveMessages.put(key.toUpperCase(), colour(section.getString(key)));
		
		getServer().getPluginManager().registerEvents(this, this);
		
	}
	
	private static String colour(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		
		for(String group : joinMessages.keySet())
			if(e.getPlayer().hasPermission("joinchanger.group." + group)) {
				String joinMessage = joinMessages.get(group);
				joinMessage = joinMessage.replace("%name%", e.getPlayer().getName());
				joinMessage = joinMessage.replace("%displayname%", e.getPlayer().getDisplayName());
				if(joinMessage.equals("")) joinMessage = null;
				e.setJoinMessage(joinMessage);
				return;
			}
		
		if(!sendIfNone)
			e.setJoinMessage(null);
		
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		
		for(String group : leaveMessages.keySet())
			if(e.getPlayer().hasPermission("joinchanger.group." + group)) {
				String leaveMessage = leaveMessages.get(group);
				leaveMessage = leaveMessage.replace("%name%", e.getPlayer().getName());
				leaveMessage = leaveMessage.replace("%displayname%", e.getPlayer().getDisplayName());
				if(leaveMessage.equals("")) leaveMessage = null;
				e.setLeaveMessage(leaveMessage);
				return;
			}
		
		if(!sendIfNone)
			e.setLeaveMessage(null);
		
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		
		for(String group : leaveMessages.keySet())
			if(e.getPlayer().hasPermission("joinchanger.group." + group)) {
				String leaveMessage = leaveMessages.get(group);
				leaveMessage = leaveMessage.replace("%name%", e.getPlayer().getName());
				leaveMessage = leaveMessage.replace("%displayname%", e.getPlayer().getDisplayName());
				if(leaveMessage.equals("")) leaveMessage = null;
				e.setQuitMessage(leaveMessage);
				return;
			}
		
		if(!sendIfNone)
			e.setQuitMessage(null);
		
	}

}
