package tk.shanebee.nbt.elements.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import org.bukkit.event.Event;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.util.Kleenean;
import net.minecraft.server.v1_12_R1.MojangsonParser;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import javax.annotation.Nullable;

@Name("NBT - Joined")
@Description("Joins two different NBTs together. The latter value will overwrite conflicting values between the two.")
@Examples("set {_ex} to joined nbt from \"{Test:false,Whatever:123}\" and \"{Something:something,Test:true}\"")
public class ExprJoinedNBT extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprJoinedNBT.class, String.class, ExpressionType.SIMPLE,"joined nbt[( |-)string] from %string% and %string%");
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
        String o = a.getSingle(e);
        String j = b.getSingle(e);
        try {
            NBTTagCompound nbto = MojangsonParser.parse(o);
            NBTTagCompound nbtj = MojangsonParser.parse(j);
            nbto.a(nbtj);
            return new String[] {nbto.toString()};
        } catch (MojangsonParseException ex) {
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
        return "Joined NBT";
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

}
