package com.jiajiaxd;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.Mirai;
import net.mamoe.mirai.console.data.AutoSavePluginDataHolder;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.BotOnlineEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class AutoFire extends JavaPlugin implements AutoSavePluginDataHolder {
    public static final AutoFire INSTANCE = new AutoFire();

    private AutoFire() {
        super(new JvmPluginDescriptionBuilder("com.jiajiaxd.mirai.autofire", "0.1.0")
                .name("AutoFire")
                .author("jiajiaxd")
                .build());
    }

    @Override
    public void onEnable() {
        reloadPluginConfig(Config.INSTANCE);
        getLogger().info("Plugin loaded!");
        Runnable runnable = new Runnable() {
            public void run() {
                System.out.println("定时进行续火检查操作...");
                Fire();
            }
        };
        ScheduledExecutorService autoFire = Executors.newSingleThreadScheduledExecutor();
        //立即执行，并且每5秒执行一次
        autoFire.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.HOURS);
    }

    Listener<BotOnlineEvent> listener=GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, event->{
        getLogger().info(String.format("QQ号%s(昵称%s)已经上线", event.getBot().getId(), Mirai.getInstance().queryProfile(event.getBot(),event.getBot().getId()).getNickname()));
        getLogger().info(String.format("自动为%s和%s续火",Config.INSTANCE.qq1.get(),Config.INSTANCE.qq2.get()));
        Fire();
    });

    public void Fire(){
        if (Bot.getInstances().size() <=1){
            getLogger().warning("登录账号过少，目前无法进行续火检查。");
            return;
        }
        long qq1=Long.parseLong(Config.INSTANCE.qq1.get());
        long qq2=Long.parseLong(Config.INSTANCE.qq2.get());
        int sent=0;
        String lastFiredDate=Config.INSTANCE.lastFiredDate.get();
        getLogger().info("正在检查续火情况，上次续火："+lastFiredDate);
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String today = formatter.format(date);
        if (!today.equals(lastFiredDate)){
            getLogger().info("检测到今日没有续火，开始续火！");
            List<Bot> bots=Bot.getInstances();
            for (Bot bot:bots) {
                if (bot.getId() == qq1) {
                    bot.getBot().getFriend(qq2).sendMessage("[自动续火]" + today);
                    sent += 1;
                    getLogger().info(String.format("已经使用%S为%S发送了自动续火消息！", qq1, qq2));
                } else if (bot.getId() == qq2) {
                    bot.getBot().getFriend(qq1).sendMessage("[自动续火]" + today);
                    sent += 1;
                    getLogger().info(String.format("已经使用%S为%S发送了自动续火消息！", qq2, qq1));
                }
            }
            if (sent==2){
                getLogger().info("续火成功！");
                Config.INSTANCE.lastFiredDate.set(today);

            }else{
                getLogger().warning("续火失败！请检查是否已经登录所有账号，或者配置文件是否正确。");
            }
        }else{
            getLogger().info("今日已经续火，无需进行续火操作。");
        }
    }


}