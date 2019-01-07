package tk.shanebee.nbt.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityType;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_13_R2.MojangsonParser;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

@Name("Spawn Entity with NBT")
@Description("Spawn an entity at a location with NBT")
@Examples({"spawn sheep at player with nbt \"{NoAI:1b}\"",
        "spawn 1 of zombie at player with nbt \"{NoGravity:1b}\""})
@Since("1.2.4")
public class EffSpawnEntityNBT extends Effect {

    static {
        Skript.registerEffect(EffSpawnEntityNBT.class,
                "spawn %entitytypes% [%directions% %locations%] with nbt %string%", "" +
                        "spawn %number% of %entitytypes% [%directions% %locations%] with nbt %string%");
    }

    @SuppressWarnings("null")
    private Expression<Location> locations;
    @SuppressWarnings("null")
    private Expression<EntityType> types;
    private Expression<String> nbtString;
    @Nullable
    private Expression<Number> amount;

    @Nullable
    public static Entity lastSpawned = null;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        amount = matchedPattern == 0 ? null : (Expression<Number>) (exprs[0]);
        types = (Expression<EntityType>) exprs[matchedPattern];
        locations = Direction.combine((Expression<? extends Direction>) exprs[1 + matchedPattern], (Expression<? extends Location>) exprs[2 + matchedPattern]);
        nbtString = (Expression<String>) exprs[3 + matchedPattern];
        return true;
    }

    @Override
    public void execute(final Event e) {
        lastSpawned = null;
        String value = this.nbtString.getSingle(e);
        final Number a = amount != null ? amount.getSingle(e) : 1;
        if (a == null)
            return;
        final EntityType[] et = types.getArray(e);
        for (final Location loc : locations.getArray(e)) {
            assert loc != null : locations;
            for (final EntityType type : et) {
                for (int i = 0; i < a.doubleValue() * type.getAmount(); i++) {
                    lastSpawned = type.data.spawn(loc);
                    net.minecraft.server.v1_13_R2.Entity nms = ((CraftEntity) lastSpawned).getHandle();
                    NBTTagCompound nbt = new NBTTagCompound();
                    nms.c(nbt);
                    try {
                        NBTTagCompound nbtv = MojangsonParser.parse(value);
                        nbt.a(nbtv);
                        nms.f(nbt);
                    } catch (CommandSyntaxException ex) {
                        Skript.warning("NBT parse error: " + ex.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "spawn " + (amount != null ? amount.toString(e, debug) + " " : "") + types.toString(e, debug) +
                " " + locations.toString(e, debug) + " " + nbtString.toString(e, debug);
    }
}
