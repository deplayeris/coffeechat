package mod.deplayer.coffeechat;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import mod.deplayer.coffeechat.coffeeirc.client.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import java.io.File;


// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CoffeeChat.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CoffeeChat.MODID, value = Dist.CLIENT)
public class CoffeeChatClient {
    private String CICDistribution = "CoffeeChat";
    public CoffeeChatClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // 初始化 CoffeeIRC 日志配置
        initializeCoffeeIRCLogging();
        
        CoffeeChat.LOGGER.info("--------------------------Deplayer CoffeeChat--------------------------");
        CoffeeChat.LOGGER.info("CoffeeChat Version:" + CoffeeChat.VERSION);
        CoffeeChat.LOGGER.info("Successfully loaded!");
        CoffeeChat.LOGGER.info("--------------------------Deplayer CoffeeChat--------------------------");

        Client client = new Client(4, "127.0.0.1", 10025,"xcv", "dp515", "CoffeeChat");
        client.Connect();
        client.disconnect();
    }
    
    private static void initializeCoffeeIRCLogging() {
        try {
            // 创建日志目录
            File logDir = new File("./ciclogs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            // 使用类路径资源加载配置
            Configurator.initialize("CoffeeIRC", CoffeeChatClient.class.getClassLoader(), "log4j2-coffeeirc.xml");
            LogManager.getLogger("mod.deplayer.coffeechat.coffeeirc.client").info("CoffeeIRC 客户端日志系统初始化完成");
            LogManager.getLogger("mod.deplayer.coffeechat.coffeeirc.server").info("CoffeeIRC 服务器日志系统初始化完成");
            
        } catch (Exception e) {
            CoffeeChat.LOGGER.warn("CoffeeIRC 日志配置初始化失败: " + e.getMessage());
            // 降级方案：直接配置日志
            setupFallbackLogging();
        }
    }
    
    private static void setupFallbackLogging() {
        try {
            // 直接编程方式配置日志
            org.apache.logging.log4j.core.LoggerContext context = 
                (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
            org.apache.logging.log4j.core.config.Configuration config = context.getConfiguration();
            
            // 创建 Appender
            org.apache.logging.log4j.core.appender.RollingFileAppender appender = 
                org.apache.logging.log4j.core.appender.RollingFileAppender.newBuilder()
                    .setName("CoffeeIRCRollingFile")
                    .withFileName("./ciclogs/coffeeirc-fallback.log")
                    .withFilePattern("./ciclogs/coffeeirc-fallback-%d{yyyy-MM-dd}-%i.log.gz")
                    .setLayout(org.apache.logging.log4j.core.layout.PatternLayout.newBuilder()
                        .withPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [CoffeeIRC] %msg%n")
                        .build())
                    .build();
            appender.start();
            
            config.addAppender(appender);
            
            // 为 CoffeeIRC 包配置 Logger
            org.apache.logging.log4j.core.config.LoggerConfig loggerConfig = 
                org.apache.logging.log4j.core.config.LoggerConfig.createLogger(
                    false, // additive
                    org.apache.logging.log4j.Level.INFO,
                    "mod.deplayer.coffeechat.coffeeirc",
                    "true",
                    new org.apache.logging.log4j.core.config.AppenderRef[]{
                        org.apache.logging.log4j.core.config.AppenderRef.createAppenderRef("CoffeeIRCRollingFile", null, null)
                    },
                    null, config, null);
            
            config.addLogger("mod.deplayer.coffeechat.coffeeirc", loggerConfig);
            context.updateLoggers();
            
            LogManager.getLogger("mod.deplayer.coffeechat.coffeeirc").info("CoffeeIRC 降级日志配置完成");
            
        } catch (Exception e) {
            CoffeeChat.LOGGER.error("CoffeeIRC 降级日志配置也失败了: " + e.getMessage());
        }
    }
}
