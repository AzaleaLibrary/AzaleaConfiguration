package net.azalealibrary.configuration.property;

import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.AzaleaException;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class PropertyType<T> {

    public static final PropertyType<String> STRING = new PropertyType<>(String.class, "text");
    public static final PropertyType<Integer> INTEGER = new PropertyType<>(Integer.class) {
        @Override
        public Integer parse(CommandSender sender, Arguments arguments) {
            return Integer.parseInt(arguments.get(0));
        }
    };
    public static final PropertyType<Double> DOUBLE = new PropertyType<>(Double.class, "decimal") {
        @Override
        public Double parse(CommandSender sender, Arguments arguments) {
            return Double.parseDouble(arguments.get(0));
        }
    };
    public static final PropertyType<Boolean> BOOLEAN = new PropertyType<>(Boolean.class) {
        @Override
        public Boolean parse(CommandSender sender, Arguments arguments) {
            if (!arguments.is(0, "true") && !arguments.is(0, "false")) {
                throw new AzaleaException(); // ensure explicit "true" or "false" text has been provided
            }
            return Boolean.parseBoolean(arguments.get(0));
        }

        @Override
        public List<String> complete(CommandSender sender, Arguments arguments) {
            return List.of("true", "false");
        }
    };
    public static final PropertyType<Vector> VECTOR = new PropertyType<>(Vector.class) {
        @Override
        public List<String> complete(CommandSender sender, Arguments arguments) {
            if (sender instanceof Player player) {
                Location location = player.getLocation();
                double x = location.getBlockX() + .5;
                double y = location.getBlockY() + .5;
                double z = location.getBlockZ() + .5;
                return List.of(x + " " + y + " " + z);
            }
            return super.complete(sender, arguments);
        }

        @Override
        public Vector parse(CommandSender sender, Arguments arguments) {
            double x = arguments.find(0, "x", Double::parseDouble);
            double y = arguments.find(1, "y", Double::parseDouble);
            double z = arguments.find(2, "z", Double::parseDouble);
            return new Vector(x, y, z);
        }
    };
    public static final PropertyType<Location> LOCATION = new PropertyType<>(Location.class, "position") {
        @Override
        public List<String> complete(CommandSender sender, Arguments arguments) {
            if (sender instanceof Player player) {
                Location location = player.getLocation();
                double x = location.getBlockX() + .5;
                double y = location.getBlockY() + .5;
                double z = location.getBlockZ() + .5;
                float yaw = location.getYaw();
                float pitch = location.getPitch();
                return List.of(x + " " + y + " " + z + " " + yaw + " " + pitch);
            }
            return super.complete(sender, arguments);
        }

        @Override
        public Location parse(CommandSender sender, Arguments arguments) {
            if (sender instanceof Player player) {
                double x = arguments.find(0, "x", Double::parseDouble);
                double y = arguments.find(1, "y", Double::parseDouble);
                double z = arguments.find(2, "z", Double::parseDouble);
                float yaw = arguments.find(3, "yaw", Float::parseFloat);
                float pitch = arguments.find(4, "pitch", Float::parseFloat);
                return new Location(player.getWorld(), x, y, z, yaw, pitch);
            }
            return super.parse(sender, arguments);
        }
    };
    public static final PropertyType<Player> PLAYER = new PropertyType<>(Player.class) {
        @Override
        public List<String> complete(CommandSender sender, Arguments arguments) {
            if (sender instanceof Player player) {
                return player.getWorld().getPlayers().stream().map(Player::getDisplayName).toList();
            }
            return super.complete(sender, arguments);
        }

        @Override
        public Player parse(CommandSender sender, Arguments arguments) {
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
        public List<String> complete(CommandSender sender, Arguments arguments) {
            return Bukkit.getServer().getWorlds().stream().map(World::getName).toList();
        }

        @Override
        public World parse(CommandSender sender, Arguments arguments) {
            return Bukkit.getWorld(arguments.get(0));
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
        return StringUtils.capitalize(expected.toLowerCase());
    }

    public List<String> complete(CommandSender sender, Arguments arguments) {
        return arguments.size() == 1 ? List.of("<" + expected + ">") : List.of();
    }

    public T parse(CommandSender sender, Arguments arguments) {
        return (T) arguments.get(0);
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

    // TODO - review
    public boolean test(CommandSender sender, Arguments arguments) {
        try {
            parse(sender, arguments);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof PropertyType<?> propertyType) {
            return propertyType.type.equals(type) && propertyType.expected.equals(expected);
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "PropertyType{" + expected + "}";
    }
}
