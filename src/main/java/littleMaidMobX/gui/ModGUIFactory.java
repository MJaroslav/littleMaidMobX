package littleMaidMobX.gui;

import java.util.Set;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import littleMaidMobX.LittleMaidMobX;
import littleMaidMobX.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

public class ModGUIFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {
  
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ModGuiConfig.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }
    
    public static class ModGuiConfig extends GuiConfig {
        public ModGuiConfig(GuiScreen parentScreen) {
            super(parentScreen, new ConfigElement<>(ModConfig.instance.getCategory("general")).getChildElements(), LittleMaidMobX.DOMAIN, false, false, LittleMaidMobX.NAME);
        }
    }
}
