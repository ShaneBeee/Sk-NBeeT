package tk.shanebee.nbt.elements.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.event.Event;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.util.Kleenean;
import net.minecraft.server.v1_13_R1.MojangsonParser;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import javax.annotation.Nullable;

@Name("NBT - Tag")
@Description("Returns the value of the specified tag of the specified NBT")
@Examples("set {_tag} to tag \"Invulnerable\" of targeted entity's nbt")
public class ExprTagOfNBT extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprTagOfNBT.class, String.class, ExpressionType.SIMPLE, "tag %string% of %string%");
    }

    private Expression<String> a;
    private Expression<String> b;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        this.a = (Expression<String>) expressions[0];
        this.b = (Expression<String>) expressions[1];
        return true;
    }

    @Override
    @Nullable
    protected String[] get(Event e) {
        String t = a.getSingle(e);
        String n = b.getSingle(e);
        if (t == null || n == null) {
            return null;
        }
        try {
            NBTTagCompound nbt = MojangsonParser.parse(n);
            if (nbt.get(t) != null) {
                return new String[] {nbt.get(t).toString()};
            } else {
                return null;
            }
        } catch (CommandSyntaxException ex) {
            Skript.warning("NBT parse error: " + ex.getMessage());
            return null;
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Tag of NBT";
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

}