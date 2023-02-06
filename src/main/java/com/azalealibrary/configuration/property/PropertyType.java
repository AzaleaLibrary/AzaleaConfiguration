package com.azalealibrary.configuration.property;

import com.azalealibrary.configuration.AzaleaException;
import com.azalealibrary.configuration.command.Arguments;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class PropertyType<T> {

    public static final PropertyType<String> STRING = new PropertyType<>(String.class, "text");
    public static final PropertyType<Integer> INTEGER = new PropertyType<>(Integer.class) {
        @Override
        public Integer parse(CommandSender sender, Arguments arguments, @Nullable Integer currentValue) {
            return Integer.parseInt(arguments.getLast());
        }
    };
    public static final PropertyType<Double> DOUBLE = new PropertyType<>(Double.class, "decimal") {
        @Override
        public Double parse(CommandSender sender, Arguments arguments, @Nullable Double currentValue) {
            return Double.parseDouble(arguments.getLast());
        }
    };
    public static final PropertyType<Boolean> BOOLEAN = new PropertyType<>(Boolean.class) {
        @Override
        public Boolean parse(CommandSender sender, Arguments arguments, @Nullable Boolean currentValue) {
            if (!arguments.is(0, "true") && !arguments.is(0, "false")) {
                throw new AzaleaException(); // ensure explicit "true" or "false" text has been provided
            }
            return Boolean.parseBoolean(arguments.getLast());
        }

        @Override
        public List<String> complete(CommandSender sender, Arguments arguments, @Nullable Boolean currentValue) {
            return List.of(Boolean.toString(Boolean.FALSE.equals(currentValue)));
        }
    };
    public static final PropertyType<Vector> VECTOR = new PropertyType<>(Vector.class) {
        @Override
        public List<String> complete(CommandSender sender, Arguments arguments, @Nullable Vector currentValue) {
            if (sender instanceof Player player) {
                Location location = player.getLocation();
                double x = location.getBlockX() + .5;
                double y = location.getBlockY() + .5;
                double z = location.getBlockZ() + .5;
                return List.of(x + " " + y + " " + z);
            }
            return super.complete(sender, arguments, currentValue);
        }

        @Override
        public Vector parse(CommandSender sender, Arguments arguments, @Nullable Vector currentValue) {
            double x = arguments.find(0, "x", Double::parseDouble);
            double y = arguments.find(1, "y", Double::parseDouble);
            double z = arguments.find(2, "z", Double::parseDouble);
            return new Vector(x, y, z);
        }
    };
    public static final PropertyType<Player> PLAYER = new PropertyType<>(Player.class) {
        @Override
        public List<String> complete(CommandSender sender, Arguments arguments, @Nullable Player currentValue) {
            if (sender instanceof Player player) {
                return player.getWorld().getPlayers().stream().map(Player::getDisplayName).toList();
            }
            return super.complete(sender, arguments, currentValue);
        }

        @Override
        public Player parse(CommandSender sender, Arguments arguments, @Nullable Player currentValue) {
            return (Player) sender;
        }

        @Override
        public Object serialize(Player object) {
            return object.getUniqueId().toString();
        }

        @Override
        public Player deserialize(Object object) {
            return Bukkit.getPlayer(UUID.fromString((String) object));
        }

        @Override
        public String print(Player object) {
            return object.getDisplayName();
        }
    };
    public static final PropertyType<World> WORLD = new PropertyType<>(World.class) {
        @Override
        public List<String> complete(CommandSender sender, Arguments arguments, @Nullable World currentValue) {
            return Bukkit.getServer().getWorlds().stream().map(World::getName).toList();
        }

        @Override
        public World parse(CommandSender sender, Arguments arguments, @Nullable World currentValue) {
            return Bukkit.getWorld(arguments.getLast());
        }

        @Override
        public Object serialize(World object) {
            return object.getName();
        }

        @Override
        public World deserialize(Object object) {
            return Bukkit.getWorld((String) object);
        }

        @Override
        public String print(World object) {
            return object.getName();
        }
    };

    private final Class<?> type;
    private final String expected;

    public PropertyType(Class<?> type) {
        this(type, type.getSimpleName());
    }

    public PropertyType(Class<?> type, String expected) {
        this.expected = expected;
        this.type = type;
    }

    public final Class<?> getType() {
        return type;
    }

    public final String getExpected() {
        return expected;
    }

    public List<String> complete(CommandSender sender, Arguments arguments, @Nullable T currentValue) {
        return arguments.size() == 1 ? List.of("<" + expected + ">") : List.of();
    }

    public T parse(CommandSender sender, Arguments arguments, @Nullable T currentValue) {
        return (T) arguments.getLast();
    }

    public Object serialize(T object) {
        return object;
    }

    public T deserialize(Object object) {
        return (T) object;
    }

    public String print(T object) {
        return object.toString();
    }
}
