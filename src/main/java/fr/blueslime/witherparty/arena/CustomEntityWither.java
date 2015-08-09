package fr.blueslime.witherparty.arena;

import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.World;

public class CustomEntityWither extends EntityWither
{
    public CustomEntityWither(World world)
    {
        super(world);
    }

    @Override
    public void g(float sideMot, float forMot)
    {
        this.k(0.0F);
        this.motY = 0.0D;
        this.motX = 0.0D;
        this.motZ = 0.0D;

        super.g(sideMot, forMot);
    }

    @Override
    protected String z()
    {
        return "";
    }

    @Override
    protected String bp()
    {
        return "";
    }

    @Override
    protected String bo()
    {
        return "";
    }
}
