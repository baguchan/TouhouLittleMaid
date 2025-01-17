package com.github.tartaricacid.touhoulittlemaid.compat.jei;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.compat.jei.altar.AltarRecipeCategory;
import com.github.tartaricacid.touhoulittlemaid.compat.jei.altar.AltarRecipeMaker;
import com.github.tartaricacid.touhoulittlemaid.compat.jei.altar.EntityPlaceholderSubtype;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class MaidPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(TouhouLittleMaid.MOD_ID, "jei");

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AltarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(AltarRecipeMaker.getInstance().getAltarRecipes(), AltarRecipeCategory.UID);
        registration.addIngredientInfo(InitItems.GARAGE_KIT.get().getDefaultInstance(), VanillaTypes.ITEM, "jei.touhou_little_maid.garage_kit.info");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(InitItems.HAKUREI_GOHEI.get().getDefaultInstance(), AltarRecipeCategory.UID);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(InitItems.ENTITY_PLACEHOLDER.get(), new EntityPlaceholderSubtype());
    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }
}
