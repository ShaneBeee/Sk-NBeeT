package tk.shanebee.nbt.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

@Name("Persistent Data Container")
@Description({"Persistent data containers are custom bits of data stored in the NBT of an entity, itemstack or tile entity,",
        "which are persistent thru server restarts. Currently supports integers, doubles and strings. Supported on 1.14+"})
@Examples({"set data container value \"owner\" of target block to name of player",
        "set {_points} to data container value \"points\" of player",
        "set data container value \"owner\" of target entity to uuid of player",
        "delete data container value \"money\" of player"})
@RequiredPlugins("1.14+")
@Since("2.10.0")
public class ExprDataContainer extends PropertyExpression<Object, Object> {

    static {
        if (Skript.classExists("org.bukkit.persistence.PersistentDataContainer")) {
            Skript.registerExpression(ExprDataContainer.class, Object.class, ExpressionType.PROPERTY,
                    "[persistent] data container [value] %string% of %itemstack/entity/block%",
                    "%itemstack/entity/block%'s [persistent] data container [value] %string%");
        }
    }

    private Expression<String> value;
    private Expression<Object> object;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        value = (Expression<String>) exprs[i == 0 ? 0 : 1];
        object = (Expression<Object>) exprs[i == 0 ? 1 : 0];
        return true;
    }


    @Override
    protected Object[] get(Event e, Object[] objects) {
        String value = this.value.getSingle(e);
        if (value == null) return null;
        NamespacedKey key = new NamespacedKey(Skript.getInstance(), value);
        return get(objects, new Getter<Object, Object>() {
            @Override
            public Object get(Object o) {
                PersistentDataContainer container = null;
                if (o instanceof ItemStack) {
                    ItemMeta meta = ((ItemStack) o).getItemMeta();
                    assert meta != null;
                    container = meta.getPersistentDataContainer();
                } else if (o instanceof Entity) {
                    container = ((Entity) o).getPersistentDataContainer();
                } else if (o instanceof Block && ((Block) o).getState() instanceof PersistentDataHolder) {
                    container = ((PersistentDataHolder) ((Block) o).getState()).getPersistentDataContainer();
                }
                if (container == null) return null;
                if (container.has(key, PersistentDataType.LONG)) {
                    return container.get(key, PersistentDataType.LONG);
                } else if (container.has(key, PersistentDataType.DOUBLE)) {
                    return container.get(key, PersistentDataType.DOUBLE);
                } else if (container.has(key, PersistentDataType.STRING)) {
                    return container.get(key, PersistentDataType.STRING);
                }
                return null;
            }
        });
    }

    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) {
            return CollectionUtils.array(Object.class);
        }
        return null;
    }

    @Override
    public void change(Event e, Object[] delta, ChangeMode mode) {
        String k = this.value.getSingle(e);
        if (k == null) return;
        Object object = this.object.getSingle(e);
        NamespacedKey key = new NamespacedKey(Skript.getInstance(), k);
        if (object == null) return;

        PersistentDataContainer container = null;
        ItemMeta meta = null;
        BlockState state = null;
        if (object instanceof ItemStack) {
            meta = ((ItemStack) object).getItemMeta();
            assert meta != null;
            container = meta.getPersistentDataContainer();
        } else if (object instanceof Entity) {
            container = ((Entity) object).getPersistentDataContainer();
        } else if (object instanceof Block && ((Block) object).getState() instanceof PersistentDataHolder) {
            state = ((Block) object).getState();
            container = ((PersistentDataHolder) state).getPersistentDataContainer();
        }
        if (container == null) return;
        switch (mode) {
            case SET:
                Object value = delta[0];
                if (value == null) return;
                if (value instanceof Long) {
                    container.set(key, PersistentDataType.LONG, ((Long) value));
                } else if (value instanceof Double) {
                    container.set(key, PersistentDataType.DOUBLE, ((Double) value));
                } else if (value instanceof String) {
                    container.set(key, PersistentDataType.STRING, ((String) value));
                } else {
                    container.set(key, PersistentDataType.STRING, value.toString());
                }
                break;
            case DELETE:
                container.remove(key);
        }
        if (object instanceof ItemStack) {
            ((ItemStack) object).setItemMeta(meta);
        } else if (object instanceof Block && state != null) {
            state.update(true);
        }
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "persistent data container \"" + this.value.toString(e, d) + "\" of " + this.object.toString(e, d);
    }

}
