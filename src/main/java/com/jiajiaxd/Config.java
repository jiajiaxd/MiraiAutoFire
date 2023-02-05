package com.jiajiaxd;

import net.mamoe.mirai.console.data.ListValue;
import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.data.java.JavaAutoSavePluginConfig;

public class Config extends JavaAutoSavePluginConfig {
    public Config(String saveName) {
        super(saveName);
    }

    public static final Config INSTANCE = new Config("fireQQ");

    public final Value<String> qq1 = value("qq1","123");
    public final Value<String> qq2 = value("qq2","456");
    public final Value<String> lastFiredDate = value("lastFiredDate","never");
}
