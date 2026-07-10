package com.nikolaipatrick.disablepluginscommand;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Collections;

public class commandListener implements Listener {

    /**
     * Blocks the command itself from running (e.g. "/version", "/ver BedrockSkinRestorer").
     * Matching is done on the command label only, so arguments no longer let the command
     * through (previously "/version foo" was NOT blocked because the code compared the
     * whole message against "/version" exactly). Probably..
     */
    @EventHandler
    public void playerRunCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String label = firstToken(e.getMessage());
        String configKey = configKeyFor(label);

        if (configKey != null && isDenied(p, configKey)) {
            e.setCancelled(true);
            sendDenyMessage(p);
        }
        //TODO: disable /bungee and /waterfall commands
    }

    /**
     * Blocks tab completion for the same commands ENTIRELY. This is what actually fixes the
     * "/version <tab>" leak: Bukkit's built-in /version and /plugins commands supply
     * their own tab completer (like a full list of every loaded plugin) directly through the
     * server's async tab-complete pipeline, which PlayerCommandPreprocessEvent never sees
     * because no command is being run yet, it's just a suggestion request. Paper exposes
     * that pipeline via AsyncTabCompleteEvent, so we hook it here and wipe the suggestions
     * before they reach the client.
     */
    @EventHandler
    public void onAsyncTabComplete(AsyncTabCompleteEvent e) {
        CommandSender sender = e.getSender();
        if (!(sender instanceof Player)) {
            return;
        }
        Player p = (Player) sender;
        String label = firstToken(e.getBuffer());
        String configKey = configKeyFor(label);

        if (configKey != null && isDenied(p, configKey)) {
            e.setCompletions(Collections.emptyList());
            e.setCancelled(true);
        }
    }

    /**
     * Maps a command label (without the leading slash, e.g. "ver" or "bukkit:plugins")
     * to the config key that governs or control it, or null if this plugin doesn't touch that command.
     */
    private String configKeyFor(String label) {
        if (isOneOf(label, "plugins", "pl")) {
            return "disable-plugins-cmd";
        }
        if (isOneOf(label, "version", "ver")) {
            return "disable-version-cmd";
        }
        if (isOneOf(label, "about")) {
            return "disable-about-cmd";
        }
        return null;
    }

    private boolean isOneOf(String label, String... names) {
        if (label == null) {
            return false;
        }
        for (String name : names) {
            if (label.equalsIgnoreCase(name)
                    || label.equalsIgnoreCase("bukkit:" + name)
                    || label.equalsIgnoreCase("minecraft:" + name)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDenied(Player p, String configKey) {
        DisablePluginsCommand plugin = DisablePluginsCommand.getPlugin(DisablePluginsCommand.class);
        if (!plugin.getConfig().getBoolean(configKey)) {
            return false;
        }
        return !p.hasPermission("disablePluginsCmd.bypass") && !p.isOp();
    }

    private void sendDenyMessage(Player p) {
        DisablePluginsCommand plugin = DisablePluginsCommand.getPlugin(DisablePluginsCommand.class);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("deny-message")));
    }

    /**
     * I forgot this one, But it returns slash null ig.
     */
    private String firstToken(String buffer) {
        if (buffer == null) {
            return null;
        }
        String trimmed = buffer.trim();
        if (trimmed.isEmpty() || trimmed.charAt(0) != '/') {
            return null;
        }
        String withoutSlash = trimmed.substring(1);
        int spaceIndex = withoutSlash.indexOf(' ');
        return spaceIndex == -1 ? withoutSlash : withoutSlash.substring(0, spaceIndex);
    }
}
