package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityMaidBeacon;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TallBlockItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.item.MaidGroup.MAIN_TAB;

public class ItemMaidBeacon extends TallBlockItem {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final String STORAGE_DATA_TAG = "StorageData";

    public ItemMaidBeacon() {
        super(InitBlocks.MAID_BEACON.get(), (new Item.Properties()).stacksTo(1).tab(MAIN_TAB));
    }

    public static ItemStack tileEntityToItemStack(TileEntityMaidBeacon beacon) {
        ItemStack stack = InitItems.MAID_BEACON.get().getDefaultInstance();
        CompoundNBT stackTag = stack.getOrCreateTag();
        stackTag.put(STORAGE_DATA_TAG, beacon.save(new CompoundNBT()));
        return stack;
    }

    public static void itemStackToTileEntity(ItemStack stack, TileEntityMaidBeacon beacon) {
        CompoundNBT tag = stack.getOrCreateTagElement(STORAGE_DATA_TAG);
        if (tag.contains("ForgeData", Constants.NBT.TAG_COMPOUND)) {
            beacon.loadData(tag.getCompound("ForgeData"));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        float numPower = 0f;
        CompoundNBT tag = stack.getOrCreateTagElement(STORAGE_DATA_TAG);
        if (tag.contains("ForgeData", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT forgeTag = tag.getCompound("ForgeData");
            if (forgeTag.contains(TileEntityMaidBeacon.STORAGE_POWER_TAG, Constants.NBT.TAG_FLOAT)) {
                numPower = forgeTag.getFloat(TileEntityMaidBeacon.STORAGE_POWER_TAG);
            }
        }
        tooltip.add(new TranslationTextComponent("tooltips.touhou_little_maid.maid_beacon.desc", DECIMAL_FORMAT.format(numPower)).withStyle(TextFormatting.GRAY));
    }
}
