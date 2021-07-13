package littleMaidMobX;

import java.io.File;

import littleMaidMobX.aimodes.IFF;
import littleMaidMobX.entity.EntityLittleMaid;
import littleMaidMobX.gui.GuiCommonHandler;
import littleMaidMobX.io.Config;
import littleMaidMobX.item.ItemSpawnEgg;
import littleMaidMobX.network.Message;
import littleMaidMobX.network.NetConstants;
import littleMaidMobX.network.Network;
import littleMaidMobX.registry.ModelManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(	modid = LittleMaidMobX.DOMAIN,
		name  = LittleMaidMobX.DOMAIN, version = "@VERSION@")
public class LittleMaidMobX {
	
	public static final String DOMAIN = "lmmx";

	public static String[] cfg_comment = {
		"spawnWeight = Relative spawn weight. The lower the less common. 10=pigs. 0=off",
		"spawnLimit = Maximum spawn count in the World.",
		"minGroupSize = Minimum spawn group count.",
		"maxGroupSize = Maximum spawn group count.",
		"canDespawn = It will despawn, if it lets things go. ",
		"checkOwnerName = At local, make sure the name of the owner. ",
		"antiDoppelganger = Not to survive the doppelganger. ",
		"enableSpawnEgg = Enable LMM SpawnEgg Recipe. ",
		"VoiceDistortion = LittleMaid Voice distortion.",
		"defaultTexture = Default selected Texture Packege. Null is Random",
		"DebugMessage = Print Debug Massages.",
		"DeathMessage = Print Death Massages.",
		"Dominant = Spawn Anywhere.",
		"Aggressive = true: Will be hostile, false: Is a pacifist",
		"IgnoreItemList = aaa, bbb, ccc: Items little maid to ignore",
//		"AchievementID = used Achievement index.(0 = Disable)",
//		"UniqueEntityId = UniqueEntityId(0 is AutoAssigned. max 255)"
	};
	
//	@MLProp(info="Relative spawn weight. The lower the less common. 10=pigs. 0=off")
	public static int cfg_spawnWeight = 5;
//	@MLProp(info="Maximum spawn count in the World.")
	public static int cfg_spawnLimit = 20;
//	@MLProp(info="Minimum spawn group count.")
	public static int cfg_minGroupSize = 1;
//	@MLProp(info="Maximum spawn group count.")
	public static int cfg_maxGroupSize = 3;
//	@MLProp(info="It will despawn, if it lets things go. ")
	public static boolean cfg_canDespawn = false;
//	@MLProp(info="At local, make sure the name of the owner. ")
	public static boolean cfg_checkOwnerName = false;
//	@MLProp(info="Not to survive the doppelganger. ")
	public static boolean cfg_antiDoppelganger = true;
//	@MLProp(info="Enable LMM SpawnEgg Recipe. ")
	public static boolean cfg_enableSpawnEgg = true;
	
	
//	@MLProp(info="LittleMaid Voice distortion.")
	public static boolean cfg_VoiceDistortion = true;
	
//	@MLProp(info="Default selected Texture Packege. Null is Random")
	public static String cfg_defaultTexture = "";
//	@MLProp(info="Print Death Massages.")
	public static boolean cfg_DeathMessage = true;
//	@MLProp(info="Spawn Anywhere.")
	public static boolean cfg_Dominant = false;
//	@MLProp(info="true: AlphaBlend(request power), false: AlphaTest(more fast)")
//	public static boolean AlphaBlend = true;
//	@MLProp(info="true: Will be hostile, false: Is a pacifist")
	public static boolean cfg_Aggressive = true;
	public static String cfg_IgnoreItemList = "arsmagica2";

	public static Achievement ac_Contract;
	public static boolean cfg_isModelAlphaBlend = true;

	public static boolean isDebugMessage = true;
	public static boolean isDebugModels = false;
	public static boolean isModelAlphaBlend = true;
	
	public static void Debug(String pText, Object... pData) {
		if (isDebugMessage) {
			// TODO: use Logger class instead with proper names and a way to enable/disable (one logger for sound, one for AI, one for models, etc)
//			if (pText.contains("Sound")) { 
//				return;
//			}			
//			if (pText.contains("daytime")) { 
//				return;
//			}
//			System.out.println(String.format("MMMLib-" + pText, pData));
		}
	}
	public static void DebugModel(String string) {
		System.out.println("LMM Models: " + string);
	}
	public static void Debug(boolean isRemote, String pText, Object... pData) {
		if (isDebugMessage) {
//			System.out.println(String.format("["+(isRemote? "Client":"Server")+"]MMMLib-" + pText, pData));
		}
	}
	
	@SidedProxy(
			clientSide = "littleMaidMobX.ProxyClient",
			serverSide = "littleMaidMobX.ProxyServer")
	public static ProxyCommon proxy;

	@Instance(DOMAIN)
	public static LittleMaidMobX instance;
	
	public static ItemSpawnEgg spawnEgg;

	public static boolean isServer = false;

	public static boolean isForge = true;

	public String getName() {
		return "littleMaidMobX";
	}

	public String getPriorities() {
		
		return "required-after:mod_MMM_MMMLib";
	}

	public String getVersion() {
		return "1.7.2-x";
	}

	
	@EventHandler
	public void PreInit(FMLPreInitializationEvent evt)
	{
		{

			File configFile = evt.getSuggestedConfigurationFile();
			Configuration lconf = new Configuration(configFile);
			lconf.load();
			isDebugMessage		= true;//lconf.get("MMMLib", "isDebugMessage", false).getBoolean(false);
			isModelAlphaBlend	= lconf.get("MMMLib", "isModelAlphaBlend", true).getBoolean(true);
			cfg_isModelAlphaBlend = isModelAlphaBlend;
			
			lconf.save();
			
			proxy.loadTextures();
//			ModelManager.instance.loadTextures();
			if (Helper.isClient) {
//				MMM_TextureManager.loadTextures();
				Debug("Localmode: InitTextureList.");
				ModelManager.instance.initTextureList(true);
			} else {
				ModelManager.instance.loadTextureServer();
			}
		}
		Config.init();
		
		
//		MMM_Helper.checkRevision("6");
		Config.checkConfig(this.getClass());
		
		cfg_defaultTexture = cfg_defaultTexture.trim();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiCommonHandler());
		

		EntityRegistry.registerModEntity(EntityLittleMaid.class, "LittleMaidX", 0, instance, 80, 3, true);

		
		
		spawnEgg = new ItemSpawnEgg();
		spawnEgg.setUnlocalizedName(DOMAIN + ":spawn_lmmx_egg");
		spawnEgg.setTextureName(DOMAIN + ":spawn_lmmx_egg");
		GameRegistry.registerItem(spawnEgg, "spawn_lmmx_egg");
		if (cfg_enableSpawnEgg) {
			
			GameRegistry.addRecipe(new ItemStack(spawnEgg, 1), new Object[] {
				"scs",
				"sbs",
				" e ",
				Character.valueOf('s'), Items.sugar,
				Character.valueOf('c'), new ItemStack(Items.dye, 1, 3),
				Character.valueOf('b'), Items.slime_ball,
				Character.valueOf('e'), Items.egg,
			});
		}
		
		ac_Contract = new Achievement("achievement.contract", "contract", 0, 0, Items.cake, null).initIndependentStat().registerStat();
		Achievement[] achievements = new Achievement[] { ac_Contract };
		AchievementPage.registerAchievementPage(new AchievementPage("LittleMaidX", achievements));

		if (Helper.isClient) {
			
			
			
			
			proxy.init();
		}
		
		
		
		Network.init(DOMAIN);

		
		proxy.loadSounds();
		
//		Debug("GUID-sneak: %s", LMM_EntityLittleMaid.maidUUIDSneak.toString());

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt)
	{
		
		// "aaa, bbb,ccc  " -> "aaa" "bbb" "ccc"
		ignoreItemList = cfg_IgnoreItemList.trim().split("\\s*,\\s*");
		
		MinecraftForge.EVENT_BUS.register(new EventHook());
		
		
		ModelManager.instance.setDefaultTexture(EntityLittleMaid.class, ModelManager.instance.getTextureBox("default_Orign"));
		
		// Dominant
		BiomeGenBase[] biomeList = null;
		if(cfg_spawnWeight > 0) {
			if (cfg_Dominant)
			{
				biomeList = BiomeGenBase.getBiomeGenArray();
			}
			else
			{
				
				biomeList = new BiomeGenBase[]{
						BiomeGenBase.desert,
						BiomeGenBase.plains,
						BiomeGenBase.savanna,
						BiomeGenBase.mushroomIsland,
						BiomeGenBase.forest,
						BiomeGenBase.birchForest,
						BiomeGenBase.swampland,
						BiomeGenBase.taiga,
				};
			}
			for(BiomeGenBase biome : biomeList)
			{
				if(biome!=null)
				{
					EntityRegistry.addSpawn(EntityLittleMaid.class,
							cfg_spawnWeight, cfg_minGroupSize, cfg_maxGroupSize, EnumCreatureType.creature, biome);
				}
			}
		}
		
		
		

		
		
		IFF.loadIFFs();
	}
	

	
	private static String ignoreItemList[] = new String[]{};

	public static boolean isMaidIgnoreItem(ItemStack item)
	{
		return item!=null && item.getItem()!=null && isMaidIgnoreItem(item.getItem());
	}
	public static boolean isMaidIgnoreItem(Item item)
	{
		if(item!=null)
		{
			String name = Item.itemRegistry.getNameForObject(item);
			for(String ignoreItemName : ignoreItemList)
			{
				if(name.indexOf(ignoreItemName) != -1)
				{
					return true;
				}
			}
		}
		return false;
	}
	// TODO: Merge this with LMM_Net, no need for 2 channels
	public static void serverCustomPayload(EntityPlayer playerEntity, Message var2)
	{
		byte lmode = var2.data[0];
		int leid = 0;
		Entity lentity = null;
		if ((lmode & 0x80) != 0) {
			leid = Helper.getInt(var2.data, 1);
			lentity = Helper.getEntity(var2.data, 1, playerEntity.worldObj);
			if (lentity == null) {
//				System.out.println("lentity == null");
				return;
			}
		}
		Debug("MMM|Upd Srv Call[%2x:%d].", lmode, leid);
//		byte[] ldata;
		
		switch (lmode) {
		case NetConstants.Server_SetTexturePackIndex:
//			System.out.println("SetTexturePackIndex");
			ModelManager.instance.reciveFromClientSetTexturePackIndex(lentity, var2.data);
			break;
		case NetConstants.Server_GetTextureIndex:
			ModelManager.instance.reciveFromClientGetTexturePackIndex(playerEntity, var2.data);
//			System.out.println("GetTextureIndex");
			break;
		case NetConstants.Server_GetTexturePackName:
//			System.out.println("GetTexturePackName");
			ModelManager.instance.reciveFromClientGetTexturePackName(playerEntity, var2.data);
			break;
		}
	}

	public static void sendToClient(EntityPlayer player, byte[] ldata)
	{
//		System.out.println("SendToClient");
		Network.sendPacketToPlayer(1, player, ldata);
	}

	public static String getModelName(String t, String t1) {
		int i = t != null && !t.isEmpty() ? t.lastIndexOf(t1) : -1;
		int i1 = i > -1 ? i + t1.length() : -1;
		if (i1 > -1)
			return t.length() > i1 ? t.substring(i1) : null;
		return t;
	}
}
