package eu.ac3_servers.dev.joinchanger;

import java.io.File;
import java.util.Map;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class JoinChangerPlugin extends JavaPlugin implements Listener {
	
	private final Map<String, String> joinMessages = Maps.newHashMap();
	private final Map<String, String> leaveMessages = Maps.newHashMap();
	
	private boolean sendIfNone;
	
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

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		for(String group : joinMessages.keySet())
			if(e.getPlayer().hasPermission("joinchanger.group." + group)) {
				String joinMessage = replacePlaceholders(joinMessages.get(group), e.getPlayer());
				if(joinMessage != null && joinMessage.isEmpty()) joinMessage = null;
				e.setJoinMessage(joinMessage);
				return;
			}
		
		if(!sendIfNone)
			e.setJoinMessage(null);
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		final String message = quit(e.getPlayer());

		e.setLeaveMessage(message == null && !sendIfNone ? null : message);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		final String message = quit(e.getPlayer());

		e.setQuitMessage(message == null && !sendIfNone ? null : message);
	}

	private String replacePlaceholders(final String text, final Player player) {
		return text == null || player == null ? null : text.
				replaceAll("(?i)%name%", player.getName()).
				replaceAll("(?i)%displayname%", player.getDisplayName());
	}

	private String quit(final Player player) {
		for(String group : leaveMessages.keySet())
			if (player.hasPermission("joinchanger.group." + group)) {
				String leaveMessage = replacePlaceholders(leaveMessages.get(group), player);
				return leaveMessage != null && leaveMessage.isEmpty() ? null : leaveMessage;
			}
		return null;
	}

	private static String colour(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
}
