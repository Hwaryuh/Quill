package io.quill.paper.command.argument;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public sealed interface ArgType<T> {

    RequiredArgumentBuilder<CommandSourceStack, ?> createBrigadierArgument(String name);

    Class<T> javaType();

    record PlayerType() implements ArgType<Player> {
        @Override
        public RequiredArgumentBuilder<CommandSourceStack, ?> createBrigadierArgument(String name) {
            return Commands.argument(name, ArgumentTypes.player());
        }

        @Override
        public Class<Player> javaType() {
            return Player.class;
        }
    }

    record IntegerType(Integer min, Integer max) implements ArgType<Integer> {
        public IntegerType() {
            this(null, null);
        }

        @Override
        public RequiredArgumentBuilder<CommandSourceStack, ?> createBrigadierArgument(String name) {
            if (min != null && max != null) {
                return Commands.argument(name, IntegerArgumentType.integer(min, max));
            }
            return Commands.argument(name, IntegerArgumentType.integer());
        }

        @Override
        public Class<Integer> javaType() {
            return Integer.class;
        }
    }

    record StringType(StringMode mode) implements ArgType<String> {
        public StringType() {
            this(StringMode.WORD);
        }

        @Override
        public RequiredArgumentBuilder<CommandSourceStack, ?> createBrigadierArgument(String name) {
            return Commands.argument(name, switch (mode) {
                case WORD -> StringArgumentType.word();
                case GREEDY -> StringArgumentType.greedyString();
                case QUOTABLE_PHRASE -> StringArgumentType.string();
            });
        }

        @Override
        public Class<String> javaType() {
            return String.class;
        }

        public enum StringMode { WORD, GREEDY, QUOTABLE_PHRASE }
    }

    record DoubleType(Double min, Double max) implements ArgType<Double> {
        public DoubleType() {
            this(null, null);
        }

        @Override
        public RequiredArgumentBuilder<CommandSourceStack, ?> createBrigadierArgument(String name) {
            if (min != null && max != null) {
                return Commands.argument(name, DoubleArgumentType.doubleArg(min, max));
            }
            return Commands.argument(name, DoubleArgumentType.doubleArg());
        }

        @Override
        public Class<Double> javaType() {
            return Double.class;
        }
    }

    record BooleanType() implements ArgType<Boolean> {
        @Override
        public RequiredArgumentBuilder<CommandSourceStack, ?> createBrigadierArgument(String name) {
            return Commands.argument(name, BoolArgumentType.bool());
        }

        @Override
        public Class<Boolean> javaType() {
            return Boolean.class;
        }
    }

    record LocationType() implements ArgType<Location> {
        @Override
        public RequiredArgumentBuilder<CommandSourceStack, ?> createBrigadierArgument(String name) {
            return Commands.argument(name, ArgumentTypes.blockPosition());
        }

        @Override
        public Class<Location> javaType() {
            return Location.class;
        }
    }

    static PlayerType player() {
        return new PlayerType();
    }

    static IntegerType integer() {
        return new IntegerType();
    }

    static IntegerType integer(int min, int max) {
        return new IntegerType(min, max);
    }

    static StringType string() {
        return new StringType();
    }

    static StringType stringGreedy() {
        return new StringType(StringType.StringMode.GREEDY);
    }

    static StringType stringQuotable() {
        return new StringType(StringType.StringMode.QUOTABLE_PHRASE);
    }

    static DoubleType doubleValue() {
        return new DoubleType();
    }

    static DoubleType doubleValue(double min, double max) {
        return new DoubleType(min, max);
    }

    static BooleanType bool() {
        return new BooleanType();
    }

    static LocationType location() {
        return new LocationType();
    }
}