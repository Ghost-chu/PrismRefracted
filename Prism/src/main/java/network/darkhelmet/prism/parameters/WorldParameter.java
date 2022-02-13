package network.darkhelmet.prism.parameters;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actionlibs.QueryParameters;
import network.darkhelmet.prism.api.actions.PrismProcessType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WorldParameter extends SimplePrismParameterHandler {

    /**
     * Constructor.
     */
    public WorldParameter() {
        super("World", Pattern.compile("[^\\s]+"), "w", "世界");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(QueryParameters query, String alias, String input, CommandSender sender) {
        String worldName = input;
        if (worldName.equalsIgnoreCase("current") || worldName.equalsIgnoreCase("现处")) {
            if (sender instanceof Player) {
                worldName = ((Player) sender).getWorld().getName();
            } else {
                Prism.messenger.sendMessage(sender, Prism.messenger
                        .playerError("由于您不是玩家, 无法使用 '现处(current)'. 已使用默认值 world."));
                worldName = Bukkit.getServer().getWorlds().get(0).getName();
            }
        }
        query.setWorld(worldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void defaultTo(QueryParameters query, CommandSender sender) {
        if (query.getProcessType().equals(PrismProcessType.DELETE)) {
            return;
        }
        if (sender instanceof Player && !query.allowsNoRadius()) {
            query.setWorld(((Player) sender).getWorld().getName());
        }
    }

    @Override
    protected List<String> tabComplete(String alias, String partialParameter, CommandSender sender) {
        List<String> result = new ArrayList<>();
        for (World w : Bukkit.getWorlds()) {
            if (w.getName().startsWith(partialParameter)) {
                result.add(w.getName());
            }
        }
        return result;
    }

}