package net.pl3x.bukkit.claims.hook;

import net.pl3x.bukkit.discord4bukkit.D4BPlugin;
import org.bukkit.ChatColor;

public class DiscordHook {
    public void sendToDiscord(String message) {
        D4BPlugin.getInstance().getBot().sendMessageToDiscord(
                ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message)));
    }
}
