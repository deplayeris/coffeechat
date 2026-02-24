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
import mod.deplayer.coffeechat.coffeeirc.client.Client;

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
        CoffeeChat.LOGGER.info("--------------------------Deplayer CoffeeChat--------------------------");
        CoffeeChat.LOGGER.info("CoffeeChat Version:" + CoffeeChat.VERSION);
        CoffeeChat.LOGGER.info("Susscssfully loaded!");
        CoffeeChat.LOGGER.info("--------------------------Deplayer CoffeeChat--------------------------");

        Client client = new Client(4, "127.0.0.1", 10025, Minecraft.getInstance().player.getName().getString(), Minecraft.getInstance().player.getName().getString(), "CoffeeChat");
        client.Connect();
        client.disconnect();
    }
}
