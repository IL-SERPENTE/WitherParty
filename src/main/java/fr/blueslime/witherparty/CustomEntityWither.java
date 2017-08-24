package fr.blueslime.witherparty;

import net.minecraft.server.v1_9_R2.EntityWither;
import net.minecraft.server.v1_9_R2.SoundEffect;
import net.minecraft.server.v1_9_R2.World;

/*
 * This file is part of WitherParty.
 *
 * WitherParty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WitherParty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WitherParty.  If not, see <http://www.gnu.org/licenses/>.
 */
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
