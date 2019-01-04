package tk.shanebee.nbt.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_13_R2.ItemStack;
import net.minecraft.server.v1_13_R2.MojangsonParser;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

@Name("NBT - Item with NBT")
@Description("Give players items with NBT or even use items with NBT in GUIs")
@Examples({"give player diamond sword with nbt \"{Unbreakable:1}\"",
        "format gui slot 1 of player with diamond axe with nbt \"{Enchantments:[{id:\\\"\\\"unbreaking\\\"\\\",lvl:5s}]}\""})
@Since("1.2.3")
public class ExprItemWithNBT extends PropertyExpression<ItemType, ItemType> {

    static {
        Skript.registerExpression(ExprItemWithNBT.class, ItemType.class, ExpressionType.PROPERTY,
                "%itemtype% with [item( |-)]nbt %string%");
    }

    @SuppressWarnings("null")
    private Expression<String> nbt;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<ItemType>) exprs[0]);
        nbt = (Expression<String>) exprs[1];
        return true;
    }

    @Override
    protected ItemType[] get(Event e, ItemType[] source) {
        String nbt = this.nbt.getSingle(e);
        return get(source, item -> {
            ItemStack nms = CraftItemStack.asNMSCopy(item.getRandom());
            try {
                nms.setTag(MojangsonParser.parse(nbt));
            } catch (CommandSyntaxException ex) {
                Skript.warning("NBT parse error: " + ex.getMessage());
            }
            item.setItemMeta(CraftItemStack.asBukkitCopy(nms).getItemMeta());
            return item;
        });
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return getExpr().toString(e, debug) + " with nbt " + nbt.toString(e, debug);
    }
}
