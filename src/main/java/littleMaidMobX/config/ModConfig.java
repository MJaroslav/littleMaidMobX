package littleMaidMobX.config;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import littleMaidMobX.LittleMaidMobX;
import net.minecraftforge.common.config.Configuration;

public class ModConfig {
    public static Configuration instance;

    public static void init(FMLPreInitializationEvent event) {
        instance = new Configuration(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(new ModConfig());
        sync();
    }

    public static int spawnWeight;
    public static int spawnLimit;
    public static int minGroupSize;
    public static int maxGroupSize;
    public static boolean canDespawn;
    public static boolean checkOwnerName;
    public static boolean antiDoppelganger;
    public static boolean enableSpawnEgg;
    public static boolean voiceDistortion;
    public static String defaultTexture;
    public static boolean printDeathMessages;
    public static boolean dominant;
    public static boolean aggressive;
    public static String ignoreItemList;
    public static boolean alphaBlend;
    public static boolean debugMessages;

    private static void sync() {
        spawnWeight = instance.getInt("spawn_weight", "general", 5, 0, Integer.MAX_VALUE, "Relative spawn weight. The lower the less common. 10=pigs. 0=off");
        spawnLimit = instance.getInt("spawn_limit", "general", 20, 0, Integer.MAX_VALUE, "Maximum spawn count in the World.");
        minGroupSize = instance.getInt("min_group_size", "general", 1, 1, Integer.MAX_VALUE, "Minimum spawn group count.");
        maxGroupSize = instance.getInt("max_group_size", "general", 1, 3, Integer.MAX_VALUE, "Maximum spawn group count.");
        canDespawn = instance.getBoolean("can_despawn", "general", false, "It will despawn, if it lets things go.");
        checkOwnerName = instance.getBoolean("check_owner_name", "general", false, "At local, make sure the name of the owner.");
        antiDoppelganger = instance.getBoolean("antidoppelganger", "general", true, "Not to survive the doppelganger.");
        enableSpawnEgg = instance.getBoolean("enable_spawn_egg", "general", true, "Enable LMM SpawnEgg recipe.");
        voiceDistortion = instance.getBoolean("voice_distortion", "general", true, "LittleMaid Voice distortion.");
        defaultTexture = instance.getString("default_texture", "general", "", "Default selected Texture package. Null is Random.").trim();
        printDeathMessages = instance.getBoolean("print_death_messages", "general", true, "Print death massages.");
        dominant = instance.getBoolean("dominant", "general", false, "Spawn Anywhere.");
        aggressive = instance.getBoolean("aggressive", "general", true, "true: Will be hostile, false: Is a pacifist.");
        ignoreItemList = instance.getString("ignore_item_list", "general", "arsmagica2", "aaa, bbb, ccc: Items little maid to ignore.").trim();
        alphaBlend = instance.getBoolean("alpha_blend", "general", true, "true: AlphaBlend(request power), false: AlphaTest(more fast).");
        debugMessages = instance.getBoolean("debug_messages", "general", false, "Print debug messages.");
        if (instance.hasChanged())
            instance.save();
    }

    @SubscribeEvent
    public void onConfigUpdate(ConfigChangedEvent event) {
        if (event.modID.equals(LittleMaidMobX.DOMAIN))
            sync();
    }
}
