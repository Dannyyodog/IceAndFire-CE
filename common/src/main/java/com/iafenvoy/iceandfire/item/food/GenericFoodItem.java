package com.iafenvoy.iceandfire.item.food;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class GenericFoodItem extends Item {
    public GenericFoodItem(int amount, float saturation, boolean eatFast, boolean alwaysEdible) {
        super(new Settings().food(createFood(amount, saturation, eatFast, alwaysEdible, null)));
    }

    public GenericFoodItem(int amount, float saturation, boolean eatFast, boolean alwaysEdible, int stackSize) {
        super(new Settings().food(createFood(amount, saturation, eatFast, alwaysEdible, null)).maxCount(stackSize));
    }

    public static FoodComponent createFood(int amount, float saturation, boolean eatFast, boolean alwaysEdible, StatusEffectInstance potion) {
        FoodComponent.Builder builder = new FoodComponent.Builder();
        builder.nutrition(amount);
        builder.saturationModifier(saturation);
        if (eatFast) builder.snack();
        if (alwaysEdible) builder.alwaysEdible();
        if (potion != null) builder.statusEffect(potion, 1.0F);
        return builder.build();
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World worldIn, LivingEntity LivingEntity) {
        this.onFoodEaten(stack, worldIn, LivingEntity);
        return super.finishUsing(stack, worldIn, LivingEntity);
    }

    public abstract void onFoodEaten(ItemStack stack, World worldIn, LivingEntity livingEntity);
}
