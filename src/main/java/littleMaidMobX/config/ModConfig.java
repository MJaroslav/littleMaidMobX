package littleMaidMobX.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.lang.annotation.ElementType;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import littleMaidMobX.LittleMaidMobX;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import littleMaidMobX.util.ItemStackUtils;

public class ModConfig {
    public static Configuration instance;

    public static void init(FMLPreInitializationEvent event) {
        instance = new Configuration(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(new ModConfig());
        sync();
    }

    private static void sync() {
        // spawnLimit = instance.getInt("spawn_limit", "general", 20, 0,
        // Integer.MAX_VALUE, "Maximum spawn count in the World.");
        // minGroupSize = instance.getInt("min_group_size", "general", 1, 1,
        // Integer.MAX_VALUE, "Minimum spawn group count.");
        // maxGroupSize = instance.getInt("max_group_size", "general", 1, 3,
        // Integer.MAX_VALUE, "Maximum spawn group count.");
        // canDespawn = instance.getBoolean("can_despawn", "general", false, "It will
        // despawn, if it lets things go.");
        // checkOwnerName = instance.getBoolean("check_owner_name", "general", false,
        // "At local, make sure the name of the owner.");
        // antiDoppelganger = instance.getBoolean("antidoppelganger", "general", true,
        // "Not to survive the doppelganger.");
        // enableSpawnEgg = instance.getBoolean("enable_spawn_egg", "general", true,
        // "Enable LMM SpawnEgg recipe.");
        // voiceDistortion = instance.getBoolean("voice_distortion", "general", true,
        // "LittleMaid Voice distortion.");
        // defaultTexture = instance.getString("default_texture", "general", "",
        // "Default selected Texture package. Null is Random.").trim();
        // printDeathMessages = instance.getBoolean("print_death_messages", "general",
        // true, "Print death massages.");
        // dominant = instance.getBoolean("dominant", "general", false, "Spawn
        // Anywhere.");
        // aggressive = instance.getBoolean("aggressive", "general", true, "true: Will
        // be hostile, false: Is a pacifist.");
        // ignoreItemList = instance.getString("ignore_item_list", "general",
        // "arsmagica2", "aaa, bbb, ccc: Items little maid to ignore.").trim();
        // alphaBlend = instance.getBoolean("alpha_blend", "general", true, "true:
        // AlphaBlend(request power), false: AlphaTest(more fast).");
        // debugMessages = instance.getBoolean("debug_messages", "general", false,
        // "Print debug messages.");
        parseCategory(General.class, null);
        syncExtra();
        if (instance.hasChanged())
            instance.save();
    }

    private static void syncExtra() {
        LittleMaidMobX.ignoredItemList = ItemStackUtils.parseStringArray(General.Behavior.ignoredItemList);
    }

    private static void parseCategory(Class<?> categoryClass, String parentName) {
        String name = StringUtils.isNullOrEmpty(parentName) ? formatName(categoryClass.getSimpleName())
                : String.format("%s.%s", parentName, formatName(categoryClass.getSimpleName()));
        CategoryInfo data = categoryClass.getDeclaredAnnotation(CategoryInfo.class);
        if (data != null) {
            if (!StringUtils.isNullOrEmpty(data.name()))
                name = StringUtils.isNullOrEmpty(parentName) ? data.name()
                        : String.format("%s.%s", parentName, data.name());
            if (!StringUtils.isNullOrEmpty(data.comment()))
                instance.setCategoryComment(name, data.comment());
            if (!StringUtils.isNullOrEmpty(data.langKey()))
                instance.setCategoryLanguageKey(name, data.langKey());
            if (data.requiresMcRestart())
                instance.setCategoryRequiresMcRestart(name, true);
            if (data.requiresWorldRestart())
                instance.setCategoryRequiresWorldRestart(name, true);
        }
        int mods;
        for (Field field : categoryClass.getDeclaredFields()) {
            mods = field.getModifiers();
            if (Modifier.isStatic(mods) && Modifier.isPublic(mods))
                parseField(field, name);
        }
        for (Class<?> clazz : categoryClass.getDeclaredClasses()) {
            mods = clazz.getModifiers();
            if (Modifier.isStatic(mods) && Modifier.isPublic(mods))
                parseCategory(clazz, name);
        }
    }

    private static String formatName(String name) {
        if (Character.isUpperCase(name.charAt(0)))
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        return name.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    private static Property.Type parseType(Class<?> type) {
        if (type.equals(int.class) || type.equals(int[].class))
            return Property.Type.INTEGER;
        if (type.equals(boolean.class) || type.equals(boolean[].class))
            return Property.Type.BOOLEAN;
        if (type.equals(double.class) || type.equals(double[].class))
            return Property.Type.DOUBLE;
        return Property.Type.STRING;
    }

    private static void parseField(Field field, String categoryName) {
        String name = formatName(field.getName());
        ConfigCategory category = instance.getCategory(categoryName);
        PropertyInfo data = field.getDeclaredAnnotation(PropertyInfo.class);
        if (data != null) {
            Property.Type type = parseType(field.getType());
            if (type == Property.Type.STRING && data.isModIdString())
                type = Property.Type.MOD_ID;
            if (type == Property.Type.STRING && data.isColorString())
                type = Property.Type.COLOR;
            boolean isArray = field.getType().isArray();
            Property property;
            switch (type) {
                case INTEGER: {
                    if (isArray)
                        property = instance.get(categoryName, name, data.defaultIntArray(), data.comment());
                    else
                        property = instance.get(categoryName, name, data.defaultInt(), data.comment());
                    boolean hasRange = false;
                    if (data.maxInt() < Integer.MAX_VALUE) {
                        property.setMaxValue(data.maxInt());
                        hasRange = true;
                    }
                    if (data.minInt() > Integer.MIN_VALUE) {
                        property.setMinValue(data.minInt());
                        hasRange = true;
                    }
                    if (!StringUtils.isNullOrEmpty(property.comment))
                        if (hasRange)
                            property.comment += " [range: " + property.getMinValue() + " ~ " + property.getMaxValue()
                                    + ", default: " + property.getDefault() + "]";
                        else
                            property.comment += " [default: " + property.getDefault() + "]";
                }
                    break;
                case BOOLEAN: {
                    if (isArray)
                        property = instance.get(categoryName, name, data.defaultBooleanArray(), data.comment());
                    else
                        property = instance.get(categoryName, name, data.defaultBoolean(), data.comment());
                    if (!StringUtils.isNullOrEmpty(property.comment))
                        property.comment += " [default: " + property.getDefault() + "]";
                }
                    break;
                case DOUBLE: {
                    if (isArray)
                        property = instance.get(categoryName, name, data.defaultDoubleArray(), data.comment());
                    else
                        property = instance.get(categoryName, name, data.defaultDouble(), data.comment());
                    boolean hasRange = false;
                    if (data.maxDouble() < Double.MAX_VALUE) {
                        property.setMaxValue(data.maxInt());
                        hasRange = true;
                    }
                    if (data.minDouble() > Double.MIN_VALUE) {
                        property.setMinValue(data.minInt());
                        hasRange = true;
                    }
                    if (!StringUtils.isNullOrEmpty(property.comment))
                        if (hasRange)
                            property.comment += " [range: " + property.getMinValue() + " ~ " + property.getMaxValue()
                                    + ", default: " + property.getDefault() + "]";
                        else
                            property.comment += " [default: " + property.getDefault() + "]";
                }
                    break;
                default: {
                    if (isArray)
                        property = instance.get(categoryName, name, data.defaultStringArray(), data.comment(), type);
                    else
                        property = instance.get(categoryName, name, data.defaulString(), data.comment(), type);
                    if (!StringUtils.isNullOrEmpty(property.comment))
                        property.comment += " [default: " + property.getDefault() + "]";
                }
                    break;
            }
            if (data.requiresMcRestart())
                property.setRequiresMcRestart(true);
            if (data.requiresWorldRestart())
                property.setRequiresWorldRestart(true);
            if (isArray) {
                if (data.maxArraySize() < Integer.MAX_VALUE)
                    property.setMaxListLength(data.maxArraySize());
                if (data.fixArraySize())
                    property.setIsListLengthFixed(true);
            }
            if (!StringUtils.isNullOrEmpty(data.langKey()))
                property.setLanguageKey(data.langKey());

            try {
                switch (type) {
                    case INTEGER: {
                        if (isArray)
                            field.set(null, property.getIntList());
                        else
                            field.set(null, property.getInt());
                    }
                        break;
                    case BOOLEAN: {
                        if (isArray)
                            field.set(null, property.getBooleanList());
                        else
                            field.set(null, property.getBoolean());
                    }
                        break;
                    case DOUBLE: {
                        if (isArray)
                            field.set(null, property.getDoubleList());
                        else
                            field.set(null, property.getDouble());
                    }
                        break;
                    default: {
                        if (isArray)
                            field.set(null, property.getStringList());
                        else
                            field.set(null, property.getString());
                    }
                        break;
                }
                category.put(name, property);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onConfigUpdate(ConfigChangedEvent event) {
        if (event.modID.equals(LittleMaidMobX.DOMAIN))
            sync();
    }

    @CategoryInfo(comment = "Categorized mod settings.", langKey = "lmmx.config.general")
    public static class General {
        @PropertyInfo(defaultBoolean = true, comment = "Send chat message on maid death.")
        public static boolean deathMessages;
        @PropertyInfo(comment = "Automatically kill maid entity doppelgangers")
        public static boolean antiDoppelganger;

        @CategoryInfo(comment = "Look and feel settings.")
        public static class Cosmetic {
            @PropertyInfo(defaultBoolean = true, comment = "Enable random maid voice distortion.")
            public static boolean voiceDistortion;
            @PropertyInfo(defaultBoolean = true, comment = "Enable alpha blending for maid models (may reduce fps).")
            public static boolean alphaBlend;
            @PropertyInfo(comment = "Default maid texture pack. Set to empty for random.")
            public static String defaultTexture;
        }

        @CategoryInfo(comment = "Maid behavior settings.", langKey = "lmmx.config.general.behavior")
        public static class Behavior {
            @PropertyInfo(defaultBoolean = true, comment = "Check maid owner when interacts with her.")
            public static boolean checkOwner;
            @PropertyInfo(defaultBoolean = true, comment = "Can maid attack unknown entities.")
            public static boolean aggressive;
            @PropertyInfo(comment = "List of ignored item ids (modid:item or modid:item:meta).")
            public static String[] ignoredItemList;
            @PropertyInfo(defaultStringArray = {
                    "arsmagica2" }, isModIdString = true, comment = "List of ignored mod ids.")
            public static String[] ignoredModIds;
        }

        @CategoryInfo(comment = "Debug settings.", langKey = "lmmx.config.general.debug")
        public static class Debug {
            @PropertyInfo(comment = "Enable all debug features.")
            public static boolean enable;
            @PropertyInfo(comment = "Print debug messages in logs.")
            public static boolean messages;
        }

        @CategoryInfo(comment = "Maid naturally spawning settings.", langKey = "lmmx.config.general.spawn")
        public static class Spawn {
            @PropertyInfo(requiresMcRestart = true, defaultInt = 5, minInt = 0, comment = "Maid spawn weight. The lower the less common. 10 is pig weight. Set 0 for disable maid spawn.")
            public static int weight;
            @PropertyInfo(defaultInt = 20, minInt = 0, comment = "Max total maid count in world. Set 0 for unlimited.")
            public static int limit;
            @PropertyInfo(defaultInt = 1, minInt = 1, comment = "Minimum amount of maids in one spawn group.")
            public static int minGroupSize;
            @PropertyInfo(defaultInt = 3, minInt = 1, comment = "Maximum amount of maids in one spawn group.")
            public static int maxGroupSize;
            @PropertyInfo(comment = "Enable despawn maids without contract.")
            public static boolean despawn;
            @PropertyInfo(defaultBoolean = true, comment = "Enable recipe for maid spawn egg.")
            public static boolean spawnEggRecipe;
            @PropertyInfo(comment = "Allow spawn anywhere")
            public static boolean dominant;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    private static @interface PropertyInfo {
        String name() default "";

        int defaultInt() default 0;

        String defaultString() default "";

        boolean defaultBoolean() default false;

        double defaultDouble() default 0d;

        int[] defaultIntArray() default {};

        String[] defaultStringArray() default {};

        boolean[] defaultBooleanArray() default {};

        double[] defaultDoubleArray() default {};

        int minInt() default Integer.MIN_VALUE;

        int maxInt() default Integer.MAX_VALUE;

        double minDouble() default Double.MIN_VALUE;

        double maxDouble() default Double.MAX_VALUE;

        int maxArraySize() default Integer.MAX_VALUE;

        boolean fixArraySize() default false;

        String comment() default "";

        String langKey() default "";

        boolean requiresMcRestart() default false;

        boolean requiresWorldRestart() default false;

        boolean isModIdString() default false;

        boolean isColorString() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    private static @interface CategoryInfo {
        String name() default "";

        String comment() default "";

        String langKey() default "";

        boolean requiresMcRestart() default false;

        boolean requiresWorldRestart() default false;
    }
}
