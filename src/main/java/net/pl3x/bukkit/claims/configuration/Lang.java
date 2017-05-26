package net.pl3x.bukkit.claims.configuration;

import net.pl3x.bukkit.claims.Pl3xClaims;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Lang {
    public static String COMMAND_NO_PERMISSION;
    public static String PLAYER_COMMAND;
    public static String VERSION;
    public static String RELOAD;

    public static String WORLD_DISABLED;

    public static String TOOLMODE_BASIC;
    public static String TOOLMODE_CHILD;
    public static String TOOLMODE_ADMIN;
    public static String REMAINING_CLAIM_BLOCKS;

    public static String MUST_HOLD_CLAIM_TOOL;
    public static String MUST_HOLD_CLAIM_TOOL_RADIUS;
    public static String MIN_RADIUS;
    public static String NOT_YOUR_CLAIM;
    public static String YOU_HAVE_NO_CLAIMS;

    public static String ABANDON_CLAIM_MISSING;
    public static String ABANDON_TOP_LEVEL_CLAIM;
    public static String ABANDON_SUCCESS;

    public static String INSPECT_NO_CLAIM;
    public static String INSPECT_TOO_FAR;
    public static String INSPECT_NEARBY_CLAIMS;
    public static String INSPECT_BLOCK_CLAIMED;
    public static String INSPECT_CLAIM_DIMENSIONS;
    public static String INSPECT_OWNER_INACTIVITY;

    public static String NO_CLAIM_CREATE_PERMISSION;

    public static String RESIZE_FAILED_OVERLAP;
    public static String RESIZE_FAILED_NEED_MORE_BLOCKS;
    public static String RESIZE_FAILED_TOO_NARROW;
    public static String RESIZE_FAILED_TOO_SMALL;
    public static String RESIZE_START;
    public static String RESIZE_SUCCESS;

    public static String CREATE_FAILED_CLAIM_LIMIT;
    public static String CREATE_FAILED_OVERLAP;
    public static String CREATE_FAILED_OVERLAP_OTHER_PLAYER;
    public static String CREATE_FAILED_TOO_NARROW;
    public static String CREATE_FAILED_TOO_SMALL;
    public static String CREATE_FAILED_NEED_MORE_BLOCKS;
    public static String CREATE_START;
    public static String CREATE_SUCCESS;
    public static String CREATE_FAILED_CHILD;
    public static String CREATE_FAILED_CHILD_OVERLAP;
    public static String CREATE_FAILED_CHILD_OVERLAP_PARENT;
    public static String CREATE_START_CHILD;
    public static String CREATE_SUCCESS_CHILD;

    private Lang() {
    }

    public static void reload(Pl3xClaims plugin) {
        String langFile = Config.LANGUAGE_FILE;
        File configFile = new File(plugin.getDataFolder(), langFile);
        plugin.saveResource(Config.LANGUAGE_FILE, false);
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        COMMAND_NO_PERMISSION = config.getString("command-no-permission", "&4You do not have permission for that command");
        PLAYER_COMMAND = config.getString("player-command", "&4Player only command");
        VERSION = config.getString("version", "&d{plugin} v{version}");
        RELOAD = config.getString("reload", "&d{plugin} v{version} reloaded");

        WORLD_DISABLED = config.getString("world-disabled", "&4Claims are disabled in this world");

        TOOLMODE_BASIC = config.getString("toolmode-basic", "&dTool returned to basic claims mode");
        TOOLMODE_CHILD = config.getString("toolmode-child", "&dTool changed to child claims mode");
        TOOLMODE_ADMIN = config.getString("toolmode-admin", "&dTool changed to admin claims mode");
        REMAINING_CLAIM_BLOCKS = config.getString("remaining-claim-blocks", "&dYou may claim up to {amount} more blocks");

        MUST_HOLD_CLAIM_TOOL = config.getString("must-hold-claim-tool", "&4You must be holding the claim tool to do that");
        MUST_HOLD_CLAIM_TOOL_RADIUS = config.getString("must-hold-claim-tool-radius", "&4You must be holding a golden shovel when specifying a radius");
        MIN_RADIUS = config.getString("min-radius", "&4Minimum radius is {radius}");
        NOT_YOUR_CLAIM = config.getString("not-your-claim", "&4This isn't your claim");
        YOU_HAVE_NO_CLAIMS = config.getString("you-have-no-claims", "&4You don't have any land claims");

        ABANDON_CLAIM_MISSING = config.getString("abandon-claim-missing", "&4Stand in the claim you want to delete, or consider /AbandonAllClaims");
        ABANDON_TOP_LEVEL_CLAIM = config.getString("abandon-top-level-claim", "&4To delete a child claim, stand inside it. Otherwise, use /AbandonClaim force to delete this claim and all it's children");
        ABANDON_SUCCESS = config.getString("abandon-success", "&dClaim abandoned. You now have {remaining} available claim blocks");

        INSPECT_NO_CLAIM = config.getString("inspect-no-claim", "&4There are no claims here");
        INSPECT_TOO_FAR = config.getString("inspect-too-far", "&4That is too far away");
        INSPECT_NEARBY_CLAIMS = config.getString("inspect-nearby-claims", "&dFound {amount} land claims");
        INSPECT_BLOCK_CLAIMED = config.getString("inspect-block-claimed", "&dThat block has been claimed by {owner}");
        INSPECT_CLAIM_DIMENSIONS = config.getString("inspect-claim-dimensions", "&d {widthX}x{widthZ}={area}");
        INSPECT_OWNER_INACTIVITY = config.getString("inspect-owner-inactivity", "&d Last login: {amount} days ago");

        NO_CLAIM_CREATE_PERMISSION = config.getString("no-claim-create-permission", "&You don't have permission to claim land");

        RESIZE_FAILED_OVERLAP = config.getString("resize-failed-overlap", "&4You can't resize the claim here because it will overlap another claim");
        RESIZE_FAILED_NEED_MORE_BLOCKS = config.getString("resize-failed-need-more-blocks", "&4You don't have enough blocks for this size. You need {amount} more");
        RESIZE_FAILED_TOO_NARROW = config.getString("resize-failed-too-narrow", "&4This new size would be too s`ll. Claims must be at least {minimum} blocks wide");
        RESIZE_FAILED_TOO_SMALL = config.getString("resize-failed-too-small", "&4This claim would be too small. Any claim must use at least {minimum} total claim blocks");
        RESIZE_START = config.getString("resize-start", "&dResizing claim. Use your tool again at the new location for this corner");
        RESIZE_SUCCESS = config.getString("resize-success", "&dClaim resized. {amount} available claim blocks remaining");

        CREATE_FAILED_CLAIM_LIMIT = config.getString("create-failed-claim-limit", "&4You've reached your limit on land claims. Use /AbandonClaim to remove one before creating another");
        CREATE_FAILED_OVERLAP = config.getString("create-failed-overlap", "&4You can't create a claim here because it would overlap another claim");
        CREATE_FAILED_OVERLAP_OTHER_PLAYER = config.getString("create-failed-overlap-other-player", "&4You can't create a claim here because it would overlap {owner}'s claim");
        CREATE_FAILED_TOO_NARROW = config.getString("create-failed-too-narrow", "&4This claim would be too small. Claims must be at least {minimum} blocks wide");
        CREATE_FAILED_TOO_SMALL = config.getString("create-failed-too-small", "&4This claim would be too small. Any claim must use at least {minimum} total claim blocks");
        CREATE_FAILED_NEED_MORE_BLOCKS = config.getString("create-failed-need-more-blocks", "&4You don't have enough blocks to claim that entire area. You need {required} more blocks");
        CREATE_START = config.getString("create-start", "&dClaim corner set! Use the tool again at the opposite corner to claim a rectangle of land. To cancel, put your tool away");
        CREATE_SUCCESS = config.getString("create-success", "&dClaim created! {amount} available claim blocks remaining. Use /trust to share it with friends");
        CREATE_FAILED_CHILD = config.getString("create-failed-child", "&4You can't create a child here because it would overlap another child");
        CREATE_FAILED_CHILD_OVERLAP = config.getString("create-failed-child-overlap", "&4Your selected area overlaps another child");
        CREATE_FAILED_CHILD_OVERLAP_PARENT = config.getString("", "&4Your selected area overlaps the parent claim's border");
        CREATE_START_CHILD = config.getString("create-start-child", "&dChild corner set! Use your tool at the location for the opposite corner of this new child");
        CREATE_SUCCESS_CHILD = config.getString("create-success-child", "&dChild claim created! Use /trust to share it with friends");
    }

    public static void send(CommandSender recipient, String message) {
        if (message == null) {
            return; // do not send blank messages
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (ChatColor.stripColor(message).isEmpty()) {
            return; // do not send blank messages
        }

        for (String part : message.split("\n")) {
            recipient.sendMessage(part);
        }
    }
}
