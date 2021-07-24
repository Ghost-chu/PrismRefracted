package me.botsko.prism.parameters;

import com.google.common.base.Joiner;
import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionTypeImpl;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.api.actions.MatchRule;
import me.botsko.prism.api.actions.PrismProcessType;
import me.botsko.prism.utils.LevenshteinDistance;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ActionParameter extends SimplePrismParameterHandler {

    public ActionParameter() {
        super("Action", Pattern.compile("[~|!]?[\\w,-]+"), "a", "行为");
    }

    @Override
    public void process(QueryParameters query, String alias, String input, CommandSender sender) {
        // Check match type
        MatchRule match = MatchRule.INCLUDE;
        if (input.startsWith("!")) {
            match = MatchRule.EXCLUDE;
        }

        final String[] actions = input.split(",");
        if (actions.length > 0) {
            for (final String action : actions) {
                // Find all actions that match the action provided - whether the
                // full name or
                // short name.
                final ArrayList<ActionTypeImpl> actionTypes = Prism.getActionRegistry()
                        .getActionsByShortName(action.replace("!", ""));
                if (!actionTypes.isEmpty()) {
                    List<String> noPermission = new ArrayList<>();
                    for (final ActionTypeImpl actionType : actionTypes) {

                        // Ensure the action allows this process type
                        if ((query.getProcessType().equals(PrismProcessType.ROLLBACK) && !actionType.canRollback())
                                || (query.getProcessType().equals(PrismProcessType.RESTORE)
                                && !actionType.canRestore())) {
                            // @todo this is important information but is too
                            // spammy with a:place, because vehicle-place
                            // doesn't support a rollback etc
                            // respond( sender,
                            // Prism.messenger.playerError("Ignoring action '"+actionType.getName()+"'
                            // because it doesn't support rollbacks.")
                            // );
                            continue;
                        }

                        if (sender != null && !sender.hasPermission(getPermission() + "." + actionType.getName())) {
                            noPermission.add(actionType.getName());
                            continue;
                        }

                        query.addActionType(actionType.getName(), match);
                    }

                    if (!noPermission.isEmpty()) {
                        String message = "忽略了行为 '" + action + "' , 由于您没有";
                        if (noPermission.size() != 1) {
                            message += "任何 " + Joiner.on(',').join(noPermission) + " 其中之一";
                        } else if (noPermission.get(0).equals(action)) {
                            message += "它";
                        } else {
                            message += noPermission.get(0);
                        }
                        message += "的权限.";
                        Prism.messenger.sendMessage(sender, Prism.messenger.playerError(message));
                    }

                } else {
                    if (sender != null) {
                        Prism.messenger.sendMessage(sender,
                                Prism.messenger.playerError("忽略了行为 '"
                                        + action.replace("!", "")
                                        + "' , 因为我们无法识别它. 可能您是想要使用 '"
                                        + LevenshteinDistance.getClosestAction(action)
                                        + "'? 请输入 '/prism params' 来获取帮助."));
                    }
                }
            }
            // If none were valid, we end here.
            if (query.getActionTypes().size() == 0) {
                throw new IllegalArgumentException("无法识别行为参数值. 请使用 /pr ? 来获取帮助.");
            }
        }
    }

    @Override
    protected List<String> tabComplete(String alias, String partialParameter, CommandSender sender) {
        List<String> res = super.tabComplete(alias, partialParameter, sender);
        if (res == null) {
            res = new ArrayList<>();
        }
        final String[] actionTypes = Prism.getActionRegistry().listAll();
        for (String ac : actionTypes) {
            if (ac.startsWith(partialParameter)) {
                res.add(ac);
            }
        }

        return res;
    }
}