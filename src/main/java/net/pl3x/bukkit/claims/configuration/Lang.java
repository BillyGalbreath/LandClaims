package net.pl3x.bukkit.claims.configuration;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Lang {
    public static String COMMAND_NO_PERMISSION;
    public static String COMMAND_MISSING_WORLD;
    public static String COMMAND_WORLD_NOT_FOUND;
    public static String COMMAND_MISSING_PLAYER;
    public static String COMMAND_PLAYER_NOT_FOUND;
    public static String COMMAND_MISSING_AMOUNT;
    public static String COMMAND_NOT_A_NUMBER;
    public static String COMMAND_MISSING_FLAG;
    public static String COMMAND_FLAG_NOT_FOUND;
    public static String CONSOLE_COMMAND;
    public static String PLAYER_COMMAND;
    public static String VERSION;
    public static String RELOAD;

    public static String WORLD_DISABLED;

    public static String EVERYONE;

    public static String CLAIM_BLOCK_COUNTS;
    public static String CLEAN_LOCATION;

    public static String TOOLMODE_BASIC;
    public static String TOOLMODE_CHILD;
    public static String TOOLMODE_ADMIN;
    public static String REMAINING_CLAIM_BLOCKS;

    public static String MIN_RADIUS;
    public static String NOT_YOUR_CLAIM;
    public static String YOU_HAVE_NO_CLAIMS;

    public static String BUILD_DENY;
    public static String PVP_DENY;
    public static String CONTAINER_DENY;
    public static String ACCESS_DENY;

    public static String ABANDON_CLAIM_MISSING;
    public static String ABANDON_TOP_LEVEL_CLAIM;
    public static String ABANDON_SUCCESS;

    public static String ADJUST_ACCRUED_BLOCKS_SUCCESS;
    public static String ADJUST_BONUS_BLOCKS_SUCCESS;

    public static String AVOID_GRIEF_CLAIM_LAND;

    public static String CLAIMBOOK_DISABLED;
    public static String CLAIMBOOK_GIVEN;
    public static String CLAIMBOOK_AUTHOR;
    public static String CLAIMBOOK_TITLE;
    public static List<String> CLAIMBOOK_PAGES;

    public static String CLAIMSLIST_HEADER;
    public static String CLAIMSLIST_CLAIM;
    public static String CLAIMSLIST_CLAIM_ADMIN;
    public static String CLAIMSLIST_FOOTER;

    public static String INSPECT_NO_CLAIM;
    public static String INSPECT_TOO_FAR;
    public static String INSPECT_NEARBY_CLAIMS;
    public static String INSPECT_BLOCK_CLAIMED;
    public static String INSPECT_CLAIM_DIMENSIONS;
    public static String INSPECT_OWNER_INACTIVITY;

    public static String NO_CLAIM_CREATE_PERMISSION;

    public static String RESIZE_FAILED_OVERLAP;
    public static String RESIZE_FAILED_CHILD_OVERLAP;
    public static String RESIZE_FAILED_CHILD_OVERLAP_PARENT;
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

    public static String DELETE_NO_CLAIM;
    public static String DELETE_NO_PERMISSION;
    public static String DELETE_HAS_CHILDREN;
    public static String DELETE_SUCCESS;

    public static String DELETE_ALL_CLAIMS_SUCCESS;

    public static String DELETE_ALL_CLAIMS_IN_WORLD_NO_CLAIMS;
    public static String DELETE_ALL_CLAIMS_IN_WORLD_SUCCESS;

    public static String DELETE_ALL_ADMIN_CLAIMS_IN_WORLD_NO_CLAIMS;
    public static String DELETE_ALL_ADMIN_CLAIMS_IN_WORLD_SUCCESS;

    public static String DELETE_ALL_USER_CLAIMS_IN_WORLD_NO_CLAIMS;
    public static String DELETE_ALL_USER_CLAIMS_IN_WORLD_SUCCESS;

    public static String EXTEND_NO_CLAIM;
    public static String EXTEND_TO_SKY;
    public static String EXTEND_DOWNWARD;

    public static String IGNORE_CLAIMS;
    public static String RESPECT_CLAIMS;

    public static String SETFLAG_NO_CLAIM;
    public static String SETFLAG_NO_PERMISSION;
    public static String SETFLAG_REMOVED;
    public static String SETFLAG_SUCCESS;

    public static String TRANSFER_NO_CLAIM;
    public static String TRANSFER_CHILD;
    public static String TRANSFER_NO_PERMISSION;
    public static String TRANSFER_SUCCESS;

    public static String TRUST_ERROR_NO_PERMISSION;
    public static String TRUST_ERROR_NO_CLAIM;
    public static String TRUST_PUBLIC;
    public static String TRUST_BUILD;
    public static String TRUST_CONTAINER;
    public static String TRUST_ACCESS;
    public static String TRUST_PERMISSION;
    public static String TRUST_CURRENT_CLAIM;
    public static String TRUST_ALL_CLAIMS;
    public static String TRUST_SUCCESS;
    public static String UNTRUST_SUCCESS;

    public static String TRUSTLIST_NO_CLAIM;
    public static String TRUSTLIST_NO_PERMISSION;
    public static String TRUSTLIST_HEADER;
    public static String TRUSTLIST_MANAGERS;
    public static String TRUSTLIST_BUILDERS;
    public static String TRUSTLIST_CONTAINERS;
    public static String TRUSTLIST_ACCESSORS;

    public static String TRAPPED_CAN_BUILD;
    public static String TRAPPED_WONT_WORK_HERE;
    public static String TRAPPED_MOVED_ABORTED;
    public static String TRAPPED_RESCUE_PENDING;

    private Lang() {
    }

    public static void reload(JavaPlugin plugin) {
        String langFile = Config.LANGUAGE_FILE;
        File configFile = new File(plugin.getDataFolder(), langFile);
        plugin.saveResource(Config.LANGUAGE_FILE, false);
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        COMMAND_NO_PERMISSION = config.getString("command-no-permission", "&4You do not have permission for that command");
        COMMAND_MISSING_WORLD = config.getString("command-missing-world", "&4Missing world in command");
        COMMAND_WORLD_NOT_FOUND = config.getString("command-world-not-found", "&4World not found");
        COMMAND_MISSING_PLAYER = config.getString("command-missing-player-name", "&4Missing player name in command");
        COMMAND_PLAYER_NOT_FOUND = config.getString("player-not-found", "&4Player not found");
        COMMAND_MISSING_AMOUNT = config.getString("command-missing-amount", "&4Missing amount in command");
        COMMAND_NOT_A_NUMBER = config.getString("command-not-a-number", "&4Invalid number specified");
        COMMAND_MISSING_FLAG = config.getString("command-missing-flag", "&4Missing flag in command");
        COMMAND_FLAG_NOT_FOUND = config.getString("command-flag-not-found", "&4Flag not found");
        CONSOLE_COMMAND = config.getString("console-command", "&4Console only command");
        PLAYER_COMMAND = config.getString("player-command", "&4Player only command");
        VERSION = config.getString("version", "&d{plugin} v{version}");
        RELOAD = config.getString("reload", "&d{plugin} v{version} reloaded");

        WORLD_DISABLED = config.getString("world-disabled", "&4Claims are disabled in this world");

        EVERYONE = config.getString("everyone", "everyone");

        CLAIM_BLOCK_COUNTS = config.getString("claim-blocks-count", "&d{accrued} from play + {bonus} bonus = {total} total");
        CLEAN_LOCATION = config.getString("clean-location", "{world} {x}, {z}");

        TOOLMODE_BASIC = config.getString("toolmode-basic", "&dTool returned to basic claims mode");
        TOOLMODE_CHILD = config.getString("toolmode-child", "&dTool changed to child claims mode");
        TOOLMODE_ADMIN = config.getString("toolmode-admin", "&dTool changed to admin claims mode");
        REMAINING_CLAIM_BLOCKS = config.getString("remaining-claim-blocks", "&dYou may claim up to {amount} more blocks");

        MIN_RADIUS = config.getString("min-radius", "&4Minimum radius is {radius}");
        NOT_YOUR_CLAIM = config.getString("not-your-claim", "&4This isn't your claim");
        YOU_HAVE_NO_CLAIMS = config.getString("you-have-no-claims", "&4You don't have any land claims");

        BUILD_DENY = config.getString("build-deny", "&4You can't build here");
        PVP_DENY = config.getString("pvp-deny", "&4PvP is disabled here");
        CONTAINER_DENY = config.getString("container-deny", "&4You don't have permission to use that");
        ACCESS_DENY = config.getString("container-deny", "&4You don't have permission to use that");

        ABANDON_CLAIM_MISSING = config.getString("abandon-claim-missing", "&4Stand in the claim you want to delete, or consider /AbandonAllClaims");
        ABANDON_TOP_LEVEL_CLAIM = config.getString("abandon-top-level-claim", "&4To delete a child claim, stand inside it. Otherwise, use /AbandonClaim force to delete this claim and all it's children");
        ABANDON_SUCCESS = config.getString("abandon-success", "&dClaim abandoned. You now have {remaining} available claim blocks");

        ADJUST_ACCRUED_BLOCKS_SUCCESS = config.getString("adjust-claim-blocks-success", "&dAdjusted {target}'s accrued claim blocks by {amount}");
        ADJUST_BONUS_BLOCKS_SUCCESS = config.getString("adjust-bonus-blocks-success", "&dAdjusted {target}'s bonus claim blocks by {amount}");

        AVOID_GRIEF_CLAIM_LAND = config.getString("avoid-grief-claim-land", "&dPrevent grief! If you claim your land, you will be grief-proof");

        CLAIMBOOK_DISABLED = config.getString("claimbook-disabled", "&4Claimbooks are disabled");
        CLAIMBOOK_GIVEN = config.getString("claimbook-given", "&gGiven claimbook to {target}");
        CLAIMBOOK_AUTHOR = config.getString("claimbook-author", "BillyGalbreath");
        CLAIMBOOK_TITLE = config.getString("claimbook-title", "How to Clam Land");
        CLAIMBOOK_PAGES = config.getStringList("claimbook-pages");
        if (CLAIMBOOK_PAGES == null || CLAIMBOOK_PAGES.isEmpty()) {
            CLAIMBOOK_PAGES = new ArrayList<>();
            CLAIMBOOK_PAGES.add("Claim land to protect your stuff!\n\nOur claim tools are {claimtool} and {inspecttool}.");
            CLAIMBOOK_PAGES.add("Useful Commands:\n\n/trust /untrust /trustlist\n/claimslist\n/abandonclaim\n\n/claim /extendclaim\n\n/childclaims\n/accesstrust\n/containertrust\n/permissiontrust");
        }

        CLAIMSLIST_HEADER = config.getString("claimslist-header", "&d{owner} Claims:");
        CLAIMSLIST_CLAIM = config.getString("claimslist-claim", "&d{clean-location} (-{area} blocks)");
        CLAIMSLIST_CLAIM_ADMIN = config.getString("claimslist-claim-admin", "&d{clean-location} ({area} blocks)");
        CLAIMSLIST_FOOTER = config.getString("claimslist-footer", " = &d{remaining} blocks left");

        INSPECT_NO_CLAIM = config.getString("inspect-no-claim", "&4There are no claims here");
        INSPECT_TOO_FAR = config.getString("inspect-too-far", "&4That is too far away");
        INSPECT_NEARBY_CLAIMS = config.getString("inspect-nearby-claims", "&dFound {amount} land claims");
        INSPECT_BLOCK_CLAIMED = config.getString("inspect-block-claimed", "&dThat block has been claimed by {owner}");
        INSPECT_CLAIM_DIMENSIONS = config.getString("inspect-claim-dimensions", "&d {widthX}x{widthZ}={area}");
        INSPECT_OWNER_INACTIVITY = config.getString("inspect-owner-inactivity", "&d Last login: {amount} days ago");

        NO_CLAIM_CREATE_PERMISSION = config.getString("no-claim-create-permission", "&You don't have permission to claim land");

        RESIZE_FAILED_OVERLAP = config.getString("resize-failed-overlap", "&4You can't resize the claim here because it will overlap another claim");
        RESIZE_FAILED_CHILD_OVERLAP = config.getString("resize-failed-child-overlap", "&4Your selected area overlaps another child");
        RESIZE_FAILED_CHILD_OVERLAP_PARENT = config.getString("resize-failed-child-overlap-parent", "&4Your selected area overlaps the parent claim's border");
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
        CREATE_FAILED_CHILD_OVERLAP_PARENT = config.getString("create-failed-child-overlap-parent", "&4Your selected area overlaps the parent claim's border");
        CREATE_START_CHILD = config.getString("create-start-child", "&dChild corner set! Use your tool at the location for the opposite corner of this new child");
        CREATE_SUCCESS_CHILD = config.getString("create-success-child", "&dChild claim created! Use /trust to share it with friends");

        DELETE_NO_CLAIM = config.getString("delete-no-claim", "&4Stand inside the claim you want to delete");
        DELETE_NO_PERMISSION = config.getString("delete-no-permission", "&4You can't delete admin claims");
        DELETE_HAS_CHILDREN = config.getString("delete-has-children", "&4This claim has children. Use /DelteClaim force to override");
        DELETE_SUCCESS = config.getString("delete-success", "&d");

        DELETE_ALL_CLAIMS_SUCCESS = config.getString("delete-all-claims-success", "&dDeleted all {target}'s claims");

        DELETE_ALL_CLAIMS_IN_WORLD_NO_CLAIMS = config.getString("delete-all-claims-in-world-no-claims", "&4No claims to delete in {world}");
        DELETE_ALL_CLAIMS_IN_WORLD_SUCCESS = config.getString("delete-all-claims-in-world-success", "&dDeleted all claims in {world}");

        DELETE_ALL_ADMIN_CLAIMS_IN_WORLD_NO_CLAIMS = config.getString("delete-all-admin-claims-in-world-no-claims", "&4No admin claims to delete in {world}");
        DELETE_ALL_ADMIN_CLAIMS_IN_WORLD_SUCCESS = config.getString("delete-all-admin-claims-in-world-success", "&dDeleted all admin claims in {world}");

        DELETE_ALL_USER_CLAIMS_IN_WORLD_NO_CLAIMS = config.getString("delete-all-user-claims-in-world-no-claims", "&4No non-admin claims to delete in {world}");
        DELETE_ALL_USER_CLAIMS_IN_WORLD_SUCCESS = config.getString("delete-all-user-claims-in-world-success", "&dDeleted all non-admin claims in {world}");

        EXTEND_NO_CLAIM = config.getString("extend-no-claim", "&4Stand inside the land claim you want to resize");
        EXTEND_TO_SKY = config.getString("extend-to-sky", "&4Land claims always extend to max build height");
        EXTEND_DOWNWARD = config.getString("extend-downward", "&4Land claims always extend to bedrock");

        IGNORE_CLAIMS = config.getString("ignore-claims", "&dNow ignoring claims");
        RESPECT_CLAIMS = config.getString("respect-claims", "&dNow respecting claims");

        SETFLAG_NO_CLAIM = config.getString("setflag-no-claim", "&4Stand inside the claim you want to set a flag for");
        SETFLAG_NO_PERMISSION = config.getString("setflag-no-permission", "&4You do not have permission to edit this claim's flags");
        SETFLAG_REMOVED = config.getString("setflag-removed", "&dFlag {flag} removed");
        SETFLAG_SUCCESS = config.getString("setflag-allow", "&dFlag {flag} set to {value}");

        TRANSFER_NO_CLAIM = config.getString("transfer-no-claim", "&4There's no claim here. Stand in the claim you want to transfer");
        TRANSFER_CHILD = config.getString("transfer-child", "&4Only top level claims can be transferred. Stand outside the child claim and try again");
        TRANSFER_NO_PERMISSION = config.getString("transfer-no-permission", "&4That requires the administrative claims permission");
        TRANSFER_SUCCESS = config.getString("transfer-success", "&dClaim transferred");

        TRUST_ERROR_NO_PERMISSION = config.getString("trust-error-no-permission", "&4You can't grant a permission you don't have yourself");
        TRUST_ERROR_NO_CLAIM = config.getString("trust-error-no-claim", "&4Stand inside the claim where you want to grant permission");
        TRUST_PUBLIC = config.getString("trust-public", "public");
        TRUST_BUILD = config.getString("trust-build", "build");
        TRUST_CONTAINER = config.getString("trust-container", "access containers and animals");
        TRUST_ACCESS = config.getString("trust-access", "use buttons and levers");
        TRUST_PERMISSION = config.getString("trust-permission", "manage permissions");
        TRUST_CURRENT_CLAIM = config.getString("trust-current-claim", "in this claim");
        TRUST_ALL_CLAIMS = config.getString("trust-all-claims", "in all your claims");
        TRUST_SUCCESS = config.getString("trust-success", "&dGranted {target} permission to {desc} {location}");
        UNTRUST_SUCCESS = config.getString("trust-success", "&dRevoked {target}'s trust {location}");

        TRUSTLIST_NO_CLAIM = config.getString("trustlist-no-claim", "&4Stand inside the claim you're curious about");
        TRUSTLIST_NO_PERMISSION = config.getString("trustlist-no-permission", "&4You don't have access to manage permissions here");
        TRUSTLIST_HEADER = config.getString("trustlist-header", "&dExplicit permissions here:");
        TRUSTLIST_MANAGERS = config.getString("trustlist-managers", "manage");
        TRUSTLIST_BUILDERS = config.getString("trustlist-builders", "build");
        TRUSTLIST_CONTAINERS = config.getString("trustlist-containers", "containers");
        TRUSTLIST_ACCESSORS = config.getString("trustlist-accessors", "access");

        TRAPPED_CAN_BUILD = config.getString("trapped-can-build", "&4You can build here. Save yourself");
        TRAPPED_WONT_WORK_HERE = config.getString("trapped-wont-work-here", "&4Sorry, unable to find a safe location to teleport you to. Contact an admin");
        TRAPPED_MOVED_ABORTED = config.getString("trapped-aborted", "&4You moved! Rescue cancelled");
        TRAPPED_RESCUE_PENDING = config.getString("trapped-rescue-pending", "&dIf you stay put for 10 seconds, you'll be teleported out. Please wait");
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
