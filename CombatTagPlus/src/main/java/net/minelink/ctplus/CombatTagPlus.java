package net.minelink.ctplus;

import net.minelink.ctplus.api.NpcNameGeneratorFactory;
import net.minelink.ctplus.api.NpcPlayerHelper;
import net.minelink.ctplus.compat.NpcPlayerHelperImpl;
import net.minelink.ctplus.event.LogoutEvent;
import net.minelink.ctplus.listener.*;
import net.minelink.ctplus.task.SafeLogoutTask;
import net.minelink.ctplus.task.TagUpdateTask;
import net.minelink.ctplus.util.BarUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public final class CombatTagPlus extends JavaPlugin {

    private final PlayerCache playerCache = new PlayerCache();

    private Settings settings;

    private TagManager tagManager;

    private NpcPlayerHelper npcPlayerHelper;

    private NpcManager npcManager;

    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    public Settings getSettings() {
        return settings;
    }

    public TagManager getTagManager() {
        return tagManager;
    }

    public NpcPlayerHelper getNpcPlayerHelper() {
        return npcPlayerHelper;
    }

    public NpcManager getNpcManager() {
        return npcManager;
    }

    @Override
    public void onEnable() {
        // Load settings
        saveDefaultConfig();

        settings = new Settings(this);
        if (settings.isOutdated()) {
            settings.update();
            getLogger().info("Configuration file has been updated.");
        }

        // Initialize plugin state
        npcPlayerHelper = new NpcPlayerHelperImpl();
        tagManager = new TagManager(this);
        if (npcPlayerHelper != null) {
            npcManager = new NpcManager(this);
        }

        NpcNameGeneratorFactory.setNameGenerator(new NpcNameGeneratorImpl(this));

        BarUtils.init();

        // Build player cache from currently online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            getPlayerCache().addPlayer(player);
        }

        // Register event listeners
        Bukkit.getPluginManager().registerEvents(new ForceFieldListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InstakillListener(this), this);

        if (getNpcManager() != null) {
            Bukkit.getPluginManager().registerEvents(new NpcListener(this), this);
        }

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new TagListener(this), this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlayerHeads")) {
            Bukkit.getPluginManager().registerEvents(new PlayerHeadsListener(this), this);
        }

        // Periodic task for purging unused data
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                getTagManager().purgeExpired();
                TagUpdateTask.purgeFinished();
                SafeLogoutTask.purgeFinished();
            }
        }, 3600, 3600);
    }

    @Override
    public void onDisable() {
        TagUpdateTask.cancelTasks(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equals("ctplusreload")) {
            reloadConfig();
            getSettings().load();
            if (sender instanceof Player) {
                sender.sendMessage(GREEN + getName() + " config reloaded.");
            }

            getLogger().info("Config reloaded by " + sender.getName());
        } else if (cmd.getName().equals("combattagplus")) {
            if (!(sender instanceof Player)) return false;

            UUID uniqueId = ((Player) sender).getUniqueId();
            Tag tag = getTagManager().getTag(uniqueId);
            if (tag == null || tag.isExpired() || !getTagManager().isTagged(uniqueId)) {
                sender.sendMessage(getSettings().getCommandUntagMessage());
                return true;
            }

            String duration = settings.formatDuration(tag.getTagDuration());
            sender.sendMessage(getSettings().getCommandTagMessage().replace("{time}", duration));
        } else if (cmd.getName().equals("ctpluslogout")) {
            if (!(sender instanceof Player)) return false;

            // Do nothing if player is already logging out
            Player player = (Player) sender;
            if (SafeLogoutTask.hasTask(player)) return false;

            // Attempt to start a new logout task
            LogoutEvent event = new LogoutEvent(player);
            Bukkit.getPluginManager().callEvent(event);

            // Do nothing if event was cancelled
            if (event.isCancelled()) return false;

            SafeLogoutTask.run(this, player);
        } else if (cmd.getName().equals("ctplusuntag")) {

            if (args.length < 1) {
                sender.sendMessage(RED + "Please specify a player to untag");
                return true;
            }

            @SuppressWarnings("deprecation")
            Player player = getServer().getPlayer(args[0]);
            if (player == null || getNpcPlayerHelper().isNpc(player)) {
                sender.sendMessage(RED + args[0] + " is not currently online!");
                return true;
            }
            UUID uniqueId = player.getUniqueId();
            if (getTagManager().untag(uniqueId)) {
                sender.sendMessage(GREEN + "Successfully untagged " + player.getName() + ".");
            } else {
                sender.sendMessage(GREEN + player.getName() + " is already untagged.");
            }
        }

        return true;
    }

}
