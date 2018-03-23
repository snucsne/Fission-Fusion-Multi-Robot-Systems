/*
 *  The Fission-Fusion in Multi-Robot Systems Toolkit is open-source
 *  software for for investigating fission-fusion processes in
 *  multi-robot systems.
 *  Copyright (C) 2017 Southern Nazarene University
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.snu.csne.forage.util;

import edu.snu.csne.forage.util.PatchDepletionCalculator.PatchDepletionData;

/**
 * TODO Class description
 * @author  Brent Eskridge
 */
public class PatchValue
{
    /** The give-up time based on individual resources */
    public int _giveUpTimeInd;

    /** The give-up slope based on individual resources */
    public float _giveUpSlopeInd;

    /** The give-up time based on group resources */
    public int _giveUpTimeGroup;

    /** The give-up time based on group resources */
    public float _giveUpSlopeGroup;

    /** The amount of individual resources gained if leaving at give-up time */
    public float _indResources;

    /** The amount of group resources if leaving at give-up time */
    public float _groupResources;

    /** All the raw patch depletion data */
    public PatchDepletionData[] _allDepletionData;

    
    /**
     * Builds this PatchValue object
     *
     * @param giveUpTimeInd
     * @param giveUpSlopeInd
     * @param giveUpTimeGroup
     * @param giveUpSlopeGroup
     * @param indResources
     * @param groupResources
     * @param allDepletionData
     */
    public PatchValue( int giveUpTimeInd,
            float giveUpSlopeInd,
            int giveUpTimeGroup,
            float giveUpSlopeGroup,
            float indResources,
            float groupResources,
            PatchDepletionData[] allDepletionData )
    {
        _giveUpTimeInd = giveUpTimeInd;
        _giveUpSlopeInd = giveUpSlopeInd;
        _giveUpTimeGroup = giveUpTimeGroup;
        _giveUpSlopeGroup = giveUpSlopeGroup;
        _indResources = indResources;
        _groupResources = groupResources;
        _allDepletionData = allDepletionData;
    }
    
    /**
     * Returns the give-up time based on individual resources
     *
     * @return The individual give-up time
     */
    public int getGiveUpTimeInd()
    {
        return _giveUpTimeInd;
    }

    /**
     * Returns the give-up slope based on individual resources
     *
     * @return The individual give-up slope
     */
    public float getGiveUpSlopeInd()
    {
        return _giveUpSlopeInd;
    }

    /**
     * Returns the give-up time based on group resources
     *
     * @return The group give-up time
     */
    public int getGiveUpTimeGroup()
    {
        return _giveUpTimeGroup;
    }

    /**
     * Returns the give-up slope based on group resources
     *
     * @return The group give-up slope
     */
    public float getGiveUpSlopeGroup()
    {
        return _giveUpSlopeGroup;
    }

    /**
     * Returns the amount of individual resources gained if leaving at
     * give-up time
     *
     * @return The indResources
     */
    public float getIndResources()
    {
        return _indResources;
    }

    /**
     * Returns the amount of group resources if leaving at give-up time
     *
     * @return The GroupResources
     */
    public float getGroupResources()
    {
        return _groupResources;
    }
    
    /**
     * Returns all the raw patch depletion data
     *
     * @return The patch depletion data
     */
    public PatchDepletionData[] getAllDepletionData()
    {
        return _allDepletionData;
    }

    /**
     * TODO Method description
     *
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append( "PatchValue: giveUpTimeInd=[" );
        builder.append( _giveUpTimeInd );
        builder.append( "] _giveUpSlopeInd=[" );
        builder.append( _giveUpSlopeInd );
        builder.append( "] _giveUpTimeGroup=[" );
        builder.append( _giveUpTimeGroup );
        builder.append( "] _giveUpSlopeGroup=[" );
        builder.append( _giveUpSlopeGroup );
        builder.append( "] _indResources=[" );
        builder.append(_indResources  );
        builder.append( "]" );
        
        return builder.toString();
    }
    
    
}