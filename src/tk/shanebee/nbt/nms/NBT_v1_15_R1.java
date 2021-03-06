package tk.shanebee.nbt.nms;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.MojangsonParser;
import net.minecraft.server.v1_15_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.TileEntity;
import net.minecraft.server.v1_15_R1.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NBT_v1_15_R1 implements NBTApi {

    @Override
    public Class<?> getCompoundClass() {
        return NBTTagCompound.class;
    }

    public String getNBT(Block b) {
        World w = ((CraftWorld) b.getWorld()).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        TileEntity tile = w.getTileEntity(new BlockPosition(b.getX(), b.getY(), b.getZ()));
        if (tile == null) return null;
        tile.save(nbt);
        return nbt.toString();
    }

    public void setNBT(Block b, String value) {
        World w = ((CraftWorld) b.getWorld()).getHandle();
        TileEntity tile = w.getTileEntity(new BlockPosition(b.getX(), b.getY(), b.getZ()));
        if (tile == null) return;
        try {
            NBTTagCompound nbt = MojangsonParser.parse(value);
            tile.load(nbt);
        } catch (CommandSyntaxException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
        tile.update();
    }

    public void addNBT(Block b, String value) {
        World w = ((CraftWorld) b.getWorld()).getHandle();
        TileEntity tile = w.getTileEntity(new BlockPosition(b.getX(), b.getY(), b.getZ()));
        NBTTagCompound nbt = new NBTTagCompound();
        if (tile == null) return;
        tile.save(nbt);
        try {
            NBTTagCompound nbtv = MojangsonParser.parse(value);
            nbt.a(nbtv);
            tile.load(nbt);
        } catch (CommandSyntaxException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
        tile.update();
    }

    public String getNBT(Entity e) {
        net.minecraft.server.v1_15_R1.Entity nms = ((CraftEntity) e).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        if (e instanceof Player) {
            nms.save(nbt);
        } else {
            nms.c(nbt);
        }
        return nbt.toString();
    }

    public void addNBT(Entity e, String value) {
        net.minecraft.server.v1_15_R1.Entity nms = ((CraftEntity) e).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        if (e instanceof Player) {
            nms.save(nbt);
        } else {
            nms.c(nbt);
        }
        try {
            NBTTagCompound nbtv = MojangsonParser.parse(value);
            nbt.a(nbtv);
            nms.f(nbt);
        } catch (CommandSyntaxException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
    }

    public void setNBT(Entity e, String value) {
        net.minecraft.server.v1_15_R1.Entity nms = ((CraftEntity) e).getHandle();
        try {
            NBTTagCompound nbtv = MojangsonParser.parse(value);
            nms.f(nbtv);
        } catch (CommandSyntaxException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
    }

    public String getNBT(ItemType i) {
        if (i == null) return null;
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
        } catch (CommandSyntaxException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
        Reflection.setMeta(i, CraftItemStack.asBukkitCopy(nms).getItemMeta());
    }

    public void setNBT(ItemType i, String value) {
        ItemStack nms = CraftItemStack.asNMSCopy(i.getRandom());
        try {
            nms.setTag(MojangsonParser.parse(value));
        } catch (CommandSyntaxException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
        Reflection.setMeta(i, CraftItemStack.asBukkitCopy(nms).getItemMeta());
    }

    public String getNBT(org.bukkit.inventory.ItemStack i) {
        NBTTagCompound nbt = CraftItemStack.asNMSCopy(i).getTag();
        if (nbt == null) return null;
        return nbt.toString();
    }

    public void addNBT(org.bukkit.inventory.ItemStack i, String value) {
        ItemStack nms = CraftItemStack.asNMSCopy(i);
        NBTTagCompound nbt = new NBTTagCompound();
        if (nms.getTag() != null) {
            nbt = nms.getTag();
        }
        try {
            NBTTagCompound nbtv = MojangsonParser.parse(value);
            nbt.a(nbtv);
            nms.setTag(nbt);
        } catch (CommandSyntaxException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
        i.setItemMeta(CraftItemStack.asBukkitCopy(nms).getItemMeta());
    }

    public void setNBT(org.bukkit.inventory.ItemStack i, String value) {
        ItemStack nms = CraftItemStack.asNMSCopy(i);
        try {
            nms.setTag(MojangsonParser.parse(value));
        } catch (CommandSyntaxException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
        i.setItemMeta(CraftItemStack.asBukkitCopy(nms).getItemMeta());
    }

    // NBT for Files
    public String getNBT(String fileName) {
        FileInputStream fis = null;
        File file = getFile(fileName);
        if (file == null) return null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            return null;
        }
        NBTTagCompound fileNBT = null;
        try {
            fileNBT = NBTCompressedStreamTools.a(fis);
            fis.close();
        } catch (IOException ex) {
            if (!(ex instanceof EOFException)) {
                Skript.warning("NBT file error: " + ex.getMessage());
            }
            return null;
        }
        return fileNBT.toString();
    }

    public void addNBT(String file, String value) {
        NBTTagCompound nbt;
        try {
            NBTTagCompound nbtN = MojangsonParser.parse(value);
            nbt = MojangsonParser.parse(getNBT(file));
            nbt.a(nbtN);
            setFileNBT(file, nbt);
        } catch (CommandSyntaxException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
    }

    public void setNBT(String file, String value) {
        try {
            NBTTagCompound nbt = MojangsonParser.parse(value);
            setFileNBT(file, nbt);
        } catch (CommandSyntaxException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
    }

    // Internal use to set file NBT
    private void setFileNBT(String file, NBTTagCompound compound) {
        File f = getFile(file);
        if (f == null) return;
        OutputStream os = null;
        try {
            os = new FileOutputStream(f);
        } catch (FileNotFoundException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }
        try {
            NBTCompressedStreamTools.a(compound, os);
        } catch (IOException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
        }

    }

    // Internal use to get file NBT
    private File getFile(String fileName) {
        fileName = !fileName.endsWith(".dat") ? fileName + ".dat" : fileName;
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        } else {
            return file;
        }
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
        } catch (CommandSyntaxException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
            return null;
        }
    }

    public boolean getEntityNoClip(Entity e) {
        net.minecraft.server.v1_15_R1.Entity entity = ((CraftEntity) e).getHandle();
        return entity.noclip;
    }

    public void setEntityNoClip(Entity e, Boolean value) {
        net.minecraft.server.v1_15_R1.Entity entity = ((CraftEntity) e).getHandle();
        entity.noclip = value;
    }

    public String[] getJoinedNBTList(String[] list) {
        try {
            NBTTagCompound nbto = new NBTTagCompound();
            NBTTagCompound nbtj;
            for (String nbt : list) {
                nbtj = MojangsonParser.parse(nbt);
                nbto.a(nbtj);
            }
            return new String[] {
                    nbto.toString()
            };
        } catch (CommandSyntaxException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
            return null;
        }
    }

}
