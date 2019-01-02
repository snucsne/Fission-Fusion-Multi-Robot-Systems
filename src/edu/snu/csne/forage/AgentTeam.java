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
package edu.snu.csne.forage;

// Imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.snu.csne.forage.decision.Decision;
import edu.snu.csne.forage.decision.DecisionType;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
@Deprecated
public class AgentTeam
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            AgentTeam.class.getName() );
    
    /** This team's unique ID */
    private String _id = null;
    
    /** The members of this team */
    private List<Agent> _members = new ArrayList<Agent>();
    
    /** Flag denoting whether or not this team can be removed when empty */
    private boolean _removeable = false;
    
    /** First member of the group (typically the leader) */
    private Agent _firstMember = null;
    
    private Decision _firstMemberDecision = null;
    
    private List<String> _leftMemberIDs = new LinkedList<String>();
    
    
    /**
     * Builds this AgentTeam object
     *
     * @param id The team's unique identifier
     */
    public AgentTeam( String id )
    {
        this( id, true );
    }
    
    public AgentTeam( String id, boolean removeable )
    {
        // Validate and store the id
        Validate.notEmpty( id,
                "Agent team ID may not be null or empty ["
                + id
                + "]" );
        _id = id;
        
        _removeable = removeable;
    }
    
    /**
     * Returns the unique ID for this agent team
     *
     * @return The unique ID
     */
    public String getID()
    {
        return _id;
    }

    /**
     * TODO Method description
     *
     * @param other
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( AgentTeam other )
    {
        // Simply compare the ID's
        return _id.equals( other._id );
    }
    
    /**
     * Signals the team that an agent has joined
     *
     * @param agent The joining agent
     */
    public void join( Agent agent )
    {
        Validate.notNull( agent, "Joining agent may not be null" );
        
        if( null == _firstMember )
        {
            _firstMember = agent;
            _firstMemberDecision = agent.getDecision();
        }
        
//        _members.put( agent.getID(), agent );
        _members.add( agent );
//        _LOG.warn( "Agent joined team: agent=["
//                + agent.getID()
//                + "] team=["
//                + getID()
//                + "]" );
    }
    
    /**
     * Signals the team that an agent is leaving
     *
     * @param agent The leaving agent
     */
    public void leave( Agent agent )
    {
        Validate.notNull( agent, "Leaving agent may not be null" );
        boolean success = _members.remove( agent );
        if( success )
        {
            _leftMemberIDs.add( agent.getID() );
        }
        Validate.isTrue( success, "Leaving agent ["
                + agent.getID()
                + "] was not a member of team ["
                + getID()
                + "]" );
    }
    
    public void memberWasPrey( Agent prey )
    {
        // If the prey is the only member of the group, no bookkeeping is necessary
        if( 1 == _members.size() )
        {
            return;
        }
        
        // Get the index of the prey agent
        int preyIdx = _members.indexOf( prey );
        Validate.isTrue( preyIdx >= 0, "Prey agent is not a member of this team" );

        // Get the prey's decision
        Decision preyDecision = prey.getDecision();

        // Are they the leader and is there more than one??
        if( 0 == preyIdx )
        {
            /* Yup, make the next in line the leader.  This is HACKEY and not
             * accurate, but it is simple */
            Decision leaderDecision = prey.getDecision();
            Agent replacement = _members.get( 1 );
            replacement.replaceDecision( leaderDecision );
            
            // Iterate through the team members
            Iterator<Agent> memberIter = _members.iterator();
            while( memberIter.hasNext() )
            {
                Agent current = memberIter.next();
                
                // Are they following the leader?
                if( prey.equals( current.getDecision().getLeader() ) )
                {
                    // Yup
                    current.getDecision().replaceLeader( replacement );
                }
            }

        }
        
        // If it isn't foraging, there are leaders
        else if( !DecisionType.FORAGE.equals( preyDecision.getType() ) )
        {
            /* Nope.  Tell all the prey's followers to instead follow the
             * prey's leader.  This is HACKEY and not accurate, but it is simple */
            Agent preysLeader = preyDecision.getLeader();
            Validate.notNull( preysLeader, "Nonleader agent had no leader: teamSize=["
                    + _members.size()
                    + "] decision=["
                    + prey.getDecision().getType()
                    + "] team=["
                    + getID()
                    + "]" );
            
            // Iterate through the team members
            Iterator<Agent> memberIter = _members.iterator();
            while( memberIter.hasNext() )
            {
                Agent current = memberIter.next();
                
                // Are they following the prey?
                if( prey.equals( current.getDecision().getLeader() ) )
                {
                    // Yup
                    current.getDecision().replaceLeader( preysLeader );
                }
            }
        }
    }
    
    /**
     * Returns the size of the team
     *
     * @return The size
     */
    public int getSize()
    {
        return _members.size();
    }
    
    /**
     * Returns all the members of this team
     *
     * @return All the members
     */
    public List<Agent> getMembers()
    {
        return new ArrayList<Agent>( _members );
    }
    
    /**
     * Indicates whether or not the team is active with members
     *
     * @return Returns <code>true</code> if the team is active and has members,
     * otherwise <code>false</code>
     */
    public boolean isActive()
    {
        return (_members.size() > 0);
    }
    
    public boolean isRemoveable()
    {
        return _removeable;
    }
    
    public Agent getFirstMember()
    {
        return _firstMember;
    }
    
    public Decision getFirstMemberDecision()
    {
        return _firstMemberDecision;
    }
    
    public List<String> getLeftMemberIDs()
    {
        return _leftMemberIDs;
    }
}
