package com.iafenvoy.iceandfire.item.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.sound.BlockSoundGroup;

public class FallingGenericBlock extends FallingBlock {
    private static final MapCodec<? extends FallingBlock> CODEC = createCodec(FallingGenericBlock::new);

    public FallingGenericBlock(Settings props) {
        super(props);
    }

    @Override
    protected MapCodec<? extends FallingBlock> getCodec() {
        return CODEC;
    }

    public static FallingGenericBlock builder(float hardness, float resistance, BlockSoundGroup sound, MapColor color, NoteBlockInstrument instrument) {
        Settings props = Settings.create().mapColor(color).instrument(instrument).sounds(sound).strength(hardness, resistance);
        return new FallingGenericBlock(props);
    }
}
