package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonArmorMaterial;
import com.iafenvoy.iceandfire.data.DragonArmorPart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DragonArmorItem extends Item {
    public final DragonArmorMaterial type;
    public final DragonArmorPart dragonSlot;
    public String name;
    private Pattern baseName = Pattern.compile("[a-z]+_[a-z]+");

    public DragonArmorItem(DragonArmorMaterial type, DragonArmorPart dragonSlot) {
        super(type.fireProof() ? new Settings().fireproof() : new Settings());
        this.type = type;
        this.dragonSlot = dragonSlot;
        if (type.dragonSteel()) this.baseName = Pattern.compile("[a-z]+_[a-z]+_[a-z]+_[a-z]+");
    }

    @Override
    public String getTranslationKey() {
        String fullName = Registries.ITEM.getId(this).getPath();
        Matcher matcher = this.baseName.matcher(fullName);
        this.name = matcher.find() ? matcher.group() : fullName;
        return "item." + IceAndFire.MOD_ID + "." + this.name;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        String words = "dragon.armor_" + this.dragonSlot.name().toLowerCase(Locale.ROOT);
        tooltip.add(Text.translatable(words).formatted(Formatting.GRAY));
    }
}
