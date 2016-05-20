package fr.blueslime.witherparty;

import net.minecraft.server.v1_9_R2.EntityWither;
import net.minecraft.server.v1_9_R2.SoundEffect;
import net.minecraft.server.v1_9_R2.World;

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
    protected SoundEffect bT()
    {
        return null;
    }

    @Override
    protected SoundEffect bS()
    {
        return null;
    }

    @Override
    protected SoundEffect G()
    {
        return null;
    }
}
