package littleMaidMobX.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;

public class ItemStackUtils {
    public static List<ItemStack> parseStringArray(String[] array) {
        ArrayList<ItemStack> result = new ArrayList<>();
        String[] data;
        ItemStack temp;
        for (String element : array) {
            data = element.split(":");
            if (data.length > 1) {
                temp = GameRegistry.findItemStack(data[0], data[1], 1);
                if (data.length > 2)
                    temp.setItemDamage(Integer.valueOf(data[2]));
                result.add(temp);
            }
        }
        return result;
    }

    public static boolean equalsItemAndMeta(ItemStack a, ItemStack b) {
        if (a == null && b == null)
            return true;
        if (a == null && b != null)
            return false;
        if (a != null && b == null)
            return false;
        return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage();
    }
}
