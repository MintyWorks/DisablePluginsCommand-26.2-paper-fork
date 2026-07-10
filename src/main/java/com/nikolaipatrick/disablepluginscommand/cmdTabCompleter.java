package com.nikolaipatrick.disablepluginscommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class cmdTabCompleter implements org.bukkit.command.TabCompleter {

    private static final List<String> VERBS = Collections.unmodifiableList(Arrays.asList("help", "version", "reload"));

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Only offer suggestions for the first argument; previously this returned the
        // full verb list regardless of how many arguments uh were already typed.
        if (args.length == 1) {
            return VERBS;
        }
        return Collections.emptyList();
    }
}
