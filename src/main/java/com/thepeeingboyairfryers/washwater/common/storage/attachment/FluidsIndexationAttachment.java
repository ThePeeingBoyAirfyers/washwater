package com.thepeeingboyairfryers.washwater.common.storage.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ShortAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.objects.Object2ShortAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.shorts.*;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FluidsIndexationAttachment {
    public static final MapCodec<FluidsIndexationAttachment> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.simpleMap(NeoForgeRegistries.FLUID_TYPES.holderByNameCodec(), Codec.SHORT, NeoForgeRegistries.FLUID_TYPES)
                        .fieldOf("fluid_indexation")
                        .forGetter(FluidsIndexationAttachment::asIndexation)
            ).apply(i, FluidsIndexationAttachment::createIndexation)
    );

    private final Int2ShortMap fluid2id;
    private final Short2ObjectMap<FluidType> id2fluid;
    private final Short2ShortMap fixedIds;

    public static FluidsIndexationAttachment create() {
        return new FluidsIndexationAttachment(new Int2ShortAVLTreeMap(), new Short2ObjectAVLTreeMap<>(), new Short2ShortAVLTreeMap());
    }

    private FluidsIndexationAttachment(Int2ShortMap fld2id, Short2ObjectMap<FluidType> id2fld, Short2ShortMap fxedIds) {
        this.fluid2id = fld2id;
        this.id2fluid = id2fld;
        this.fixedIds = fxedIds;
        fluid2id.defaultReturnValue(Short.MAX_VALUE);
        id2fluid.defaultReturnValue(null);
        fixedIds.defaultReturnValue(Short.MAX_VALUE);
    }

    public Short2ShortFunction getIdFixer() {
        return fixedIds;
    }

    public short getId(FluidType fluidType) {
        int id = NeoForgeRegistries.FLUID_TYPES.getId(fluidType);

        assert fluid2id.containsKey(id);
        return fluid2id.get(id);
    }

    public FluidType getFluid(short id) {
        assert id2fluid.containsKey(id);

        return id2fluid.get(id);
    }

    private Map<Holder<FluidType>, Short> asIndexation() {
        var result = new HashMap<Holder<FluidType>, Short>();
        for (var entry : fluid2id.int2ShortEntrySet()) {
            result.put(NeoForgeRegistries.FLUID_TYPES.getHolder(entry.getIntKey()).orElseThrow(), entry.getShortValue());
        }

        return result;
    }

    private static FluidsIndexationAttachment createIndexation(Map<Holder<FluidType>, Short> fluidIndexation) {
        var fluid2id = new Int2ShortAVLTreeMap();
        var id2fluid = new Short2ObjectAVLTreeMap<FluidType>();
        var fixedIds = new Short2ShortAVLTreeMap();
        //registries.lookupOrThrow(NeoForgeRegistries.Keys.FLUID_TYPES);
        var registry = NeoForgeRegistries.FLUID_TYPES;
        var orderedIterator = Stream.concat(fluidIndexation.keySet().stream(), registry.holders())
                .distinct()
                .sorted(Comparator.comparingInt(h -> h.value().getDensity()))
                .iterator();

        short givenId = Short.MIN_VALUE;
        while (orderedIterator.hasNext()) {
            var entry = orderedIterator.next();
            Short prevId = fluidIndexation.get(entry);
            short newId = givenId++;

            if (prevId != null && prevId != newId) {
                fixedIds.put((short) prevId, newId);
            }

            fluid2id.put(registry.getId(entry.value()), newId);
            id2fluid.put(newId, entry.value());
        }

        return new FluidsIndexationAttachment(fluid2id, id2fluid, fixedIds);
    }
}
