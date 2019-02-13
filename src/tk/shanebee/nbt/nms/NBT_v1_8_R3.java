package tk.shanebee.nbt.nms;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;

public class NBT_v1_8_R3 implements NBTApi {

    @Override
    public Class<?> getCompoundClass() {
        return NBTTagCompound.class;
    }

    public String getNBT(Block b) {
        World w = ((CraftWorld) b.getWorld()).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        TileEntity tile = w.getTileEntity(new BlockPosition(b.getX(), b.getY(), b.getZ()));
        if (tile == null) return null;
        //tile.save(nbt);
        tile.b(nbt);
        return nbt.toString();
    }

    public void setNBT(Block b, String value) {
        World w = ((CraftWorld) b.getWorld()).getHandle();
        TileEntity tile = w.getTileEntity(new BlockPosition(b.getX(), b.getY(), b.getZ()));
        if (tile == null) return;
        try {
            NBTTagCompound nbt = MojangsonParser.parse(value);
            tile.a(nbt);
        } catch (MojangsonParseException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
        tile.update();
    }

    public void addNBT(Block b, String value) {
        World w = ((CraftWorld) b.getWorld()).getHandle();
        TileEntity tile = w.getTileEntity(new BlockPosition(b.getX(), b.getY(), b.getZ()));
        NBTTagCompound nbt = new NBTTagCompound();
        if (tile == null) return;
        tile.b(nbt);
        try {
            NBTTagCompound nbtv = MojangsonParser.parse(value);
            nbt.a(nbtv);
            tile.a(nbt);
        } catch (MojangsonParseException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
        tile.update();
    }

    public String getNBT(Entity e) {
        net.minecraft.server.v1_8_R3.Entity nms = ((CraftEntity) e).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        nms.c(nbt);
        return nbt.toString();
    }

    public void addNBT(Entity e, String value) {
        net.minecraft.server.v1_8_R3.Entity nms = ((CraftEntity) e).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        nms.c(nbt);
        try {
            NBTTagCompound nbtv = MojangsonParser.parse(value);
            nbt.a(nbtv);
            nms.f(nbt);
        } catch (MojangsonParseException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
    }

    public void setNBT(Entity e, String value) {
        net.minecraft.server.v1_8_R3.Entity nms = ((CraftEntity) e).getHandle();
        try {
            NBTTagCompound nbtv = MojangsonParser.parse(value);
            nms.f(nbtv);
        } catch (MojangsonParseException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
    }

    public String getNBT(ItemType i) {
        if (i == null || i.getRandom().getType() == org.bukkit.Material.AIR) return null;
        NBTTagCompound nbt = CraftItemStack.asNMSCopy(i.getRandom()).getTag();
        if (nbt == null) return null;
        return nbt.toString();
    }

    public void addNBT(ItemType i, String value) {
        ItemStack nms = CraftItemStack.asNMSCopy(i.getRandom());
        NBTTagCompound nbt = new NBTTagCompound();
        if (nms.getTag() != null) {
            nbt = nms.getTag();
        }
        try {
            NBTTagCompound nbtv = MojangsonParser.parse(value);
            nbt.a(nbtv);
            nms.setTag(nbt);
        } catch (MojangsonParseException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
        i.setItemMeta(CraftItemStack.asBukkitCopy(nms).getItemMeta());
    }

    public void setNBT(ItemType i, String value) {
        ItemStack nms = CraftItemStack.asNMSCopy(i.getRandom());
        try {
            nms.setTag(MojangsonParser.parse(value));
        } catch (MojangsonParseException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
        i.setItemMeta(CraftItemStack.asBukkitCopy(nms).getItemMeta());
    }

    public String[] getNBTTag(String a, String b) {
        if (a == null || b == null) {
            return null;
        }
        try {
            NBTTagCompound nbt = MojangsonParser.parse(b);
            if (nbt.get(a) != null) {
                return new String[] {nbt.get(a).toString()};
            } else {
                return null;
            }
        } catch (MojangsonParseException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
            return null;
        }
    }

    public boolean getEntityNoClip(Entity e) {
        net.minecraft.server.v1_8_R3.Entity entity = ((CraftEntity) e).getHandle();
        return entity.noclip;
    }

    public void setEntityNoClip(Entity e, Boolean value) {
        net.minecraft.server.v1_8_R3.Entity entity = ((CraftEntity) e).getHandle();
        entity.noclip = value;
    }

    public String[] getJoinedNBT(String a, String b) {
        try {
            NBTTagCompound nbto = MojangsonParser.parse(a);
            NBTTagCompound nbtj = MojangsonParser.parse(b);
            nbto.a(nbtj);
            return new String[] {
                    nbto.toString()
            } ;
        } catch (MojangsonParseException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
            return null;
        }
    }

}