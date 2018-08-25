package de.jaylawl.jnbt.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import net.minecraft.server.v1_12_R1.MojangsonParser;

import javax.annotation.Nullable;

public class ExprItemNBT extends SimplePropertyExpression<ItemStack, String> {

    static {
        register(ExprItemNBT.class, String.class, "[item[stack]( |-)]nbt", "itemstack");
    }

    @Override
    @Nullable
    public String convert(ItemStack i) {
        if (i == null || i.getType() == Material.AIR) {
            return null;
        }
        NBTTagCompound nbt = CraftItemStack.asNMSCopy(i).getTag();
        if (nbt == null) {
            return null;
        }
        return nbt.toString();
    }

    @Override
    public Class<?>[] acceptChange(final ChangeMode mode) {
        if (
            mode == ChangeMode.ADD ||
            mode == ChangeMode.REMOVE ||
            mode == ChangeMode.SET ||
            mode == ChangeMode.DELETE ||
            mode == ChangeMode.RESET
        ) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }

    @Override
    public void change(Event e, Object[] delta, ChangeMode mode) {
        ItemStack i = getExpr().getSingle(e);
        if (i != null || i.getType() != Material.AIR) {
            net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(i);
            NBTTagCompound nbt;
            switch (mode) {
                case ADD:
                    nbt = new NBTTagCompound();
                    if (nms.getTag() != null) {
                        nbt = nms.getTag();
                    }
                    try {
                        NBTTagCompound nbtv = MojangsonParser.parse(((String) delta[0]));
                        nbt.a(nbtv);
                        nms.setTag(nbt);
                    } catch (MojangsonParseException ex) {
                        Skript.warning("NBT parse error: " + ex.getMessage());
                    }
                    break;
                case REMOVE:
                    if (nms.getTag() != null) {
                        nbt = nms.getTag();
                    } else {
                        return;
                    }
                    nbt.remove(((String) delta[0]));
                    nms.setTag(nbt);
                    break;
                case SET:
                    try {
                        nms.setTag(MojangsonParser.parse(((String) delta[0])));
                    } catch (MojangsonParseException ex) {
                        Skript.warning("NBT parse error: " + ex.getMessage());
                    }
                    break;
                case DELETE:
                case RESET:
                    nms.setTag(new NBTTagCompound());
                    break;
                default:
                    assert false;
            }
            i.setItemMeta(CraftItemStack.asBukkitCopy(nms).getItemMeta());
        }
    }

    @Override
    protected String getPropertyName() {
        return "itemstack nbt";
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }
}
