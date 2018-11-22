package net.pl3x.bukkit.claims.hook;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;

public class DiscordSRVHook {
    public void sendToDiscord(String message) {
        DiscordUtil.sendMessage(DiscordSRV.getPlugin().getMainTextChannel(), DiscordUtil.strip(message));
    }
}
