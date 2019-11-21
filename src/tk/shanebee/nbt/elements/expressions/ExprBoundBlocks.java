package tk.shanebee.nbt.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import tk.shanebee.nbt.elements.objects.Bound;

import java.util.ArrayList;
import java.util.List;

@Name("Bound - Blocks")
@Description("All the blocks within a bound")
@Examples({"set {_blocks::*} to all blocks within bound {bound}",
        "set all blocks within {bound} to stone",
        "loop all blocks within bound {bound}:", "\tif loop-block is stone:", "\t\tset loop-block to grass"})
@Since("2.7.2")
public class ExprBoundBlocks extends SimpleExpression<Block> {

    static {
        Skript.registerExpression(ExprBoundBlocks.class, Block.class, ExpressionType.SIMPLE,
                "[(all [[of] the]|the)] blocks within [bound] %bound%");
    }

    private Expression<Bound> bound;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        bound = (Expression<Bound>) exprs[0];
        return true;
    }

    @Override
    protected Block[] get(Event event) {
        List<Block> list = new ArrayList<>(bound.getSingle(event).getBlocks());
        return list.toArray(new Block[0]);
    }

    @Override
    public Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "the blocks within bound " + bound.toString(e, d);
    }

}
