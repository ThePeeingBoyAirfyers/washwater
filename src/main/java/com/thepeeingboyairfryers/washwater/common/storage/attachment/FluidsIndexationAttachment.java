package com.thepeeingboyairfryers.washwater.common.storage.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ShortAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ShortAVLTreeMap;
import it.unimi.dsi.fastutil.shorts.Short2ShortFunction;
import it.unimi.dsi.fastutil.shorts.Short2ShortMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
    private final Int2ObjectMap<List<Fluid>> relatedFluids;

    private FluidsIndexationAttachment(Int2ShortMap fld2id, Short2ObjectMap<FluidType> id2fld, Short2ShortMap fxedIds, Int2ObjectMap<List<Fluid>> irelatedFluids) {
        this.fluid2id = fld2id;
        this.id2fluid = id2fld;
        this.fixedIds = fxedIds;
        this.relatedFluids = irelatedFluids;
        fluid2id.defaultReturnValue(Short.MAX_VALUE);
        id2fluid.defaultReturnValue(null);
        fixedIds.defaultReturnValue(Short.MAX_VALUE);
    }

    public static FluidsIndexationAttachment create() {
        return createIndexation(new HashMap<>());
    }

    private static FluidsIndexationAttachment createIndexation(Map<Holder<FluidType>, Short> fluidIndexation) {
        var fluid2id = new Int2ShortAVLTreeMap();
        var id2fluid = new Short2ObjectAVLTreeMap<FluidType>();
        var fixedIds = new Short2ShortAVLTreeMap();
        var relatedFluids = new Int2ObjectAVLTreeMap<List<Fluid>>();
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

        BuiltInRegistries.FLUID.holders().forEach(f -> {
            Fluid fluid = f.value();
            List<Fluid> list = relatedFluids.computeIfAbsent(registry.getId(fluid.getFluidType()), ArrayList::new);
            list.add(fluid);
        });

        return new FluidsIndexationAttachment(fluid2id, id2fluid, fixedIds, relatedFluids);
    }

    public Short2ShortFunction getIdFixer() {
        return fixedIds;
    }

    public short getId(@NotNull FluidType fluidType) {
        int id = NeoForgeRegistries.FLUID_TYPES.getId(fluidType);
        if (!fluid2id.containsKey(id)) throw new IllegalArgumentException("No id for fluid " + fluidType);

        return fluid2id.get(id);
    }

    public @NotNull FluidType getFluid(short id) {
        if (!id2fluid.containsKey(id)) throw new IllegalArgumentException("No fluid with id " + id);

        return id2fluid.get(id);
    }

    public @NotNull Collection<Fluid> getRelatedFluids(FluidType fluidType) {
        return relatedFluids.get(NeoForgeRegistries.FLUID_TYPES.getId(fluidType));
    }

    private Map<Holder<FluidType>, Short> asIndexation() {
        var result = new HashMap<Holder<FluidType>, Short>();
        for (var entry : fluid2id.int2ShortEntrySet()) {
            result.put(NeoForgeRegistries.FLUID_TYPES.getHolder(entry.getIntKey()).orElseThrow(), entry.getShortValue());
        }

        return result;
    }
}
