package tk.shanebee.nbt.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.util.coll.CollectionUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_13_R1.*;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import javax.annotation.Nullable;

@Name("NBT - Tile Entity")
@Description("NBT of a tile entity, such as a furnace")
@Examples({"add \"{RequiredPlayerRange:0s}\" to targeted block's nbt", "add \"{SpawnData:{id:\"\"minecraft:wither\"\"}}\" to nbt of clicked block"})
public class ExprTileEntityNBT extends SimplePropertyExpression<Block, String> {

    static {
        register(ExprTileEntityNBT.class, String.class, "[(block|tile[[ ]entity])( |-)]nbt", "block");
    }

    @Override
    @Nullable
    public String convert(Block b) {
        net.minecraft.server.v1_13_R1.World w = ((CraftWorld) b.getWorld()).getHandle();
        net.minecraft.server.v1_13_R1.TileEntity te = w.getTileEntity(new BlockPosition(b.getX(), b.getY(), b.getZ()));
        if (te == null) {
            return null;
        }
        NBTTagCompound nbt = new NBTTagCompound();
        te.save(nbt);
        return nbt.toString();
    }

    @Override
    public Class<?>[] acceptChange(final ChangeMode mode) {
        if (mode == ChangeMode.ADD || mode == ChangeMode.SET)
            return CollectionUtils.array(String.class);
        return null;
    }

    @Override
    public void change(Event event, Object[] delta, ChangeMode mode){
        Block b = getExpr().getSingle(event);
        String value = ((String) delta[0]);
        NBTTagCompound nbt = new NBTTagCompound();
        net.minecraft.server.v1_13_R1.World w = ((CraftWorld) b.getWorld()).getHandle();
        net.minecraft.server.v1_13_R1.TileEntity te = w.getTileEntity(new BlockPosition(b.getX(), b.getY(), b.getZ()));
        if (te != null) {
            switch (mode) {
                case ADD:
                    te.save(nbt);
                    try {
                        NBTTagCompound nbtv = MojangsonParser.parse(value);
                        nbt.a(nbtv);
                        te.load(nbt);
                    } catch (CommandSyntaxException ex) {
                        Skript.warning("NBT parse error: " + ex.getMessage());
                    }
                    break;
                case SET:
                    try {
                        NBTTagCompound nbtv = MojangsonParser.parse(value);
                        te.load(nbtv);
                    } catch (CommandSyntaxException ex) {
                        Skript.warning("NBT parse error: " + ex.getMessage());
                    }
                    break;
                default:
                    assert false;
            }
            te.update();
            IBlockData tet = w.getType(new BlockPosition(b.getX(), b.getY(), b.getZ()));
            w.notify(te.getPosition(), tet, tet, 3);
        }
    }

    @Override
    protected String getPropertyName() {
        return "tile entity nbt";
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }
}
