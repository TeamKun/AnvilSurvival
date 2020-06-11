package net.teamfruit.anvilsurvival;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.Random;

public final class AnvilSurvival extends JavaPlugin {
    public World world;
    public Location pos1;
    public Location pos2;
    public BoundingBox area;
    Random rand = new Random();
    BukkitRunnable runnable;
    public float interval = 1f;
    public int count = 20;

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private BukkitRunnable createTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < count; i++) {
                    double x = area.getMinX() + rand.nextInt(Math.max(1, (int) area.getWidthX()));
                    double y = area.getMinY() + rand.nextInt(Math.max(1, (int) area.getHeight()));
                    double z = area.getMinZ() + rand.nextInt(Math.max(1, (int) area.getWidthZ()));
                    Location loc = new Location(world, x, y, z);
                    FallingBlock block = world.spawnFallingBlock(loc, Material.ANVIL, (byte) 2);
                    //loc.getBlock().setType(Material.ANVIL);
                }
            }
        };
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("引数が足らんぞ");
            return false;
        }
        if ("interval".equals(args[0]) || "+interval".equals(args[0]) || "-interval".equals(args[0])) {
            if (args.length <= 1) {
                sender.sendMessage("引数が足らんぞ");
                return true;
            }
            if (args[0].startsWith("+"))
                interval += NumberUtils.toFloat(args[1]);
            else if (args[0].startsWith("-"))
                interval -= NumberUtils.toFloat(args[1]);
            else
                interval = NumberUtils.toFloat(args[1]);
            sender.sendMessage("インターバル(秒)をセット: " + interval);
            if (runnable != null) {
                runnable.cancel();
                runnable = createTask();
                runnable.runTaskTimer(this, 0, (int) (interval * 20f));
            }
            return true;
        }
        if ("count".equals(args[0]) || "+count".equals(args[0]) || "-count".equals(args[0])) {
            if (args.length <= 1) {
                sender.sendMessage("引数が足らんぞ");
                return true;
            }
            if (args[0].startsWith("+"))
                count += NumberUtils.toInt(args[1]);
            else if (args[0].startsWith("-"))
                count -= NumberUtils.toInt(args[1]);
            else
                count = NumberUtils.toInt(args[1]);
            sender.sendMessage("一度に降らす量をセット: " + count);
            return true;
        }
        if ("start".equals(args[0])) {
            if (world == null || pos1 == null || pos2 == null) {
                sender.sendMessage("範囲が未設定");
                return true;
            }
            area = new BoundingBox(pos1.getX(), pos1.getY(), pos1.getZ(), pos1.getX(), pos1.getY(), pos1.getZ());
            area.union(pos2.getX(), pos2.getY(), pos2.getZ());
            if (runnable != null) {
                sender.sendMessage("すでに実行中");
                return true;
            }
            runnable = createTask();
            runnable.runTaskTimer(this, 0, (int) (interval * 20f));
            sender.sendMessage("バトル開始");
            return true;
        }
        if ("stop".equals(args[0])) {
            if (runnable == null) {
                sender.sendMessage("実行されていない");
                return true;
            }
            runnable.cancel();
            runnable = null;
            sender.sendMessage("バトル終了");
            return true;
        }
        if (("pos1".equals(args[0]) || "pos2".equals(args[0])) && !(sender instanceof Player)) {
            sender.sendMessage("プレイヤーで実行するんやで");
            return true;
        }
        if ("pos1".equals(args[0])) {
            world = ((Player) sender).getWorld();
            pos1 = ((Player) sender).getLocation();
            sender.sendMessage("始点を" + String.format("(%d, %d, %d)", pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ()) + "にセット");
            return true;
        }
        if ("pos2".equals(args[0])) {
            world = ((Player) sender).getWorld();
            pos2 = ((Player) sender).getLocation();
            sender.sendMessage("終点を" + String.format("(%d, %d, %d)", pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ()) + "にセット");
            return true;
        }
        return false;
    }
}
