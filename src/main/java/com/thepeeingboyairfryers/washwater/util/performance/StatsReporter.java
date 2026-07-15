package com.thepeeingboyairfryers.washwater.util.performance;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

import java.util.HashSet;
import java.util.Set;

public class StatsReporter {
    private static final int[] TICK_AMOUNTS = new int[] {20 * 60, 20 * 60 * 5, 20 * 60 * 15 };
    private static final String[] TEXTS = new String[] {"1 Min: ", "5 Min: ", "15 Min: "};
    private final Set<PerTickMeasurements> perTickMeasurements = new HashSet<>();

    public <T extends PerTickMeasurements> T addMeasurements(T measurements) {
        perTickMeasurements.add(measurements);
        return measurements;
    }

    public Component report() {
        var result = Component.empty();
        perTickMeasurements.stream().flatMap(m -> m.getData().stream())
                .forEach(m -> result.append(reportMeasurement(m)));

        return result;
    }

    private Component reportMeasurement(PerTickMeasurements.Measurement m) {
        var result = Component.empty();

        for (int i = 0; i < 3; i++) {
            var hover = Component.literal("98%: ")
                    .append(Component.literal(m.format(m.getPercentageWise(.98, TICK_AMOUNTS[i]))))
                    .append(Component.literal(" 99.9%: "))
                    .append(m.format(m.getPercentageWise(.999, TICK_AMOUNTS[i])));

            var entry = Component.literal(
                    m.format(m.getPercentageWise(.5, TICK_AMOUNTS[i]))
            ).withStyle(s -> s
                    .applyFormat(ChatFormatting.BOLD)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
            );

            var name = Component.empty();
            String[] split = m.getName().split("\\|");
            for (int j = 0; j < split.length; j++) {
                name.append(Component.literal(split[j]).withStyle(j == split.length - 1 ? ChatFormatting.GREEN : ChatFormatting.DARK_GREEN));
                name.append(Component.literal(" > "));
            }

            result.append(name
                    .append(Component.literal(TEXTS[i]))
                    .append(entry)
                    .append(Component.literal("\n"))
            );
        }

        return result;
    }
}
