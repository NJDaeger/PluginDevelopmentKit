package com.njdaeger.pdk.command.flags;

import com.njdaeger.pdk.command.CommandExecutor;
import com.njdaeger.pdk.command.TabExecutor;
import com.njdaeger.pdk.utils.Text;
import org.bukkit.Material;

public abstract class Flag<T> implements CommandExecutor, TabExecutor {

    public Flag(Class<T> type, String flag, boolean followingArgument) {
    }

}
