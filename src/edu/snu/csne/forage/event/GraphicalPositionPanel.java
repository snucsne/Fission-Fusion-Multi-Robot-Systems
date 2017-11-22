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
package edu.snu.csne.forage.event;

// Imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.Patch;
import edu.snu.csne.forage.SimulationState;
import edu.snu.csne.forage.decision.Decision;
import edu.snu.csne.forage.decision.DecisionType;
import edu.snu.csne.mates.math.SphericalVector;
import edu.snu.csne.util.MathUtils;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class GraphicalPositionPanel extends JPanel
{
    /** Default serial UID */
    private static final long serialVersionUID = 1L;
    
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            GraphicalPositionPanel.class.getName() );

    /** Center of mass of all agents */
    private Vector3f _agentCenterOfMass = new Vector3f();
    
    /** The step size of the grid lines */
    private int _gridStep = 0;
    
    /** The display scale */
    private float _displayScale = 0.0f;
    
    /** Background color */
    private Color _bgColor = null;
    
    /** Grid color */
    private Color _gridColor = null;

    /** All the patches */
    private List<Patch> _patches = new LinkedList<Patch>();
    
    /** Agent color */
    private Color _agentColor = null;
    
    /** Leader line color */
    private Color _leaderLineColor = null;
    
    /** All the agents */
    private List<Agent> _agents = new LinkedList<Agent>();
    
    /** Agent draw size */
    private float _agentDrawSize = 0.0f;
    
    /** Polygon for an agent */
    private Polygon _agentPoly = null;

    
    public GraphicalPositionPanel( int width,
            int height,
            int gridStep,
            Color bgColor,
            Color gridColor,
            Color agentColor,
            Color leaderLineColor,
            float displayScale,
            float agentDrawSize,
            SimulationState simState )
    {
        // Set the preferred size
        setPreferredSize( new Dimension( width, height ) );
        _gridStep = gridStep;
        _bgColor = bgColor;
        _gridColor = gridColor;
        _agentColor = agentColor;
        _leaderLineColor = leaderLineColor;
        _displayScale = displayScale;
        _agentDrawSize = agentDrawSize;
        
        int[] xPoints = { 0,
                (int) (0.707f * _agentDrawSize),
                0,
                (int) (-0.707f * _agentDrawSize)};
        int[] yPoints = { (int) (_agentDrawSize),
                (int) (-0.707f * _agentDrawSize),
                (int) (-1.0f * _agentDrawSize),
                (int) (-0.707f * _agentDrawSize)};
        _agentPoly = new Polygon( xPoints, yPoints, xPoints.length );

        _LOG.debug( "xPoints: " + Arrays.toString( xPoints ) );
        _LOG.debug( "yPoints: " + Arrays.toString( yPoints ) );
        
        simUpdate( simState );
    }

    /**
     * TODO Method description
     *
     * @param g
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent( Graphics g )
    {
        // Call the superclass implementation
        super.paintComponent( g );

        // Cast it to a 2d object for easier drawing
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Set the background color
        g2d.setBackground( _bgColor );
        
        // Turn on anti-aliasing
        g2d.setRenderingHint( RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY );
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );

        // Draw the grid
        drawGrid( g2d );
        
        // Draw the patches
        drawPatches( g2d );
        
        // Draw the lines from follower to leader
        drawLeaderLines( g2d );
        
        // Draw the agents
        drawAgents( g2d );
        
        // Cleanup
        g2d.dispose();
    }

    /**
     * Called to signal the panel that the agents have moved
     *
     * @param simState The current state of the simulation
     */
    public void simUpdate( SimulationState simState )
    {
        // Get all the patches
        Map<String,Patch> patches = simState.getAllPatches();
        _patches = new LinkedList<Patch>( patches.values() );
        
        // Get all the agents
        Map<String,Agent> agents = simState.getAllAgents();
        _agents = new LinkedList<Agent>( agents.values() );
        
        // Calculate the center of mass of the agents so we can offset
        Vector3f centerOfMass = new Vector3f();
        Iterator<Agent> agentIter = _agents.iterator();
        while( agentIter.hasNext() )
        {
            centerOfMass.addLocal( agentIter.next().getPosition() );
        }
        _agentCenterOfMass = centerOfMass.divide( (float) _agents.size() );
        
        // Repaint the view
        if( isVisible() )
        {
            repaint();
        }
        
        try
        {
            Thread.sleep( 100 );
        }
        catch( InterruptedException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Draws the grid
     *
     * @param g2d The graphics drawing object
     */
    private void drawGrid( Graphics2D g2d )
    {
        // Round the center of mass to the nearest grid step
        float xGridCenter = MathUtils.round( _agentCenterOfMass.x, _gridStep );
        float yGridCenter = MathUtils.round( _agentCenterOfMass.y, _gridStep );

        // Calculate the screen coordinates of the center of the grid
        int xGridCenterScreen = getWidth() / 2
                + (int) Math.round( (xGridCenter - _agentCenterOfMass.x) * _displayScale );
        int yGridCenterScreen = getHeight() - (getHeight() / 2
                + (int) Math.round( (yGridCenter - _agentCenterOfMass.y) * _displayScale ));

        // How many lines can fit on the screen?
        float gridStepPixels = _gridStep * _displayScale;
        int xLineCount = (int) Math.ceil( ((float) getWidth()) / gridStepPixels )
                + 2;
        int yLineCount = (int) Math.ceil( ((float) getHeight()) / gridStepPixels )
                + 2;

        // Calculate the starting values of the first grid line
        int xGridStart = (int) (xGridCenterScreen - (-1 + xLineCount / 2) * gridStepPixels);
        int yGridStart = (int) (yGridCenterScreen - (-1 + yLineCount / 2) * gridStepPixels);

        // Draw the vertical lines
        g2d.setColor( _gridColor );
        for( int i = 0; i < xLineCount; i++ )
        {
            // Calculate the x value and draw the line
            int x = (int) Math.round( xGridStart + i * gridStepPixels );
            g2d.drawLine( x, -1, x, getHeight() + 1 );
        }
        
        // Draw the horizontal lines
        for( int i = 0; i < yLineCount; i++ )
        {
            // Calculate the y value and draw the line
            int y = (int) Math.round( yGridStart + i * gridStepPixels );
            g2d.drawLine( -1, y, getWidth() + 1, y );
        }
    }
    
    /**
     * Draws all the patches in the simulation
     *
     * @param g2d The graphics drawing object
     */
    private void drawPatches( Graphics2D g2d )
    {
        Iterator<Patch> patchIter = _patches.iterator();
        while( patchIter.hasNext() )
        {
            Patch current = patchIter.next();
            
            // For now, just use green for the patch
            // Later the patch color will depend on the resources
            g2d.setColor( new Color( 0.0f, 0.8f, 0.0f, 0.5f ) );
            
            // Get the converted position
            Vector3f patchPosition = current.getPosition();
            Vector3f convertedPosition = convert( patchPosition );
            _LOG.debug( "Patch: position=[" + patchPosition + "]  converted=[" + convertedPosition + "]" );
            
            float radius = _displayScale * current.getRadius();
            float diameter = radius * 2.0f;
            Ellipse2D patchShape = new Ellipse2D.Float( -radius,
                    -radius,
                    diameter,
                    diameter );
            
            // Get the original transformation
            AffineTransform original = g2d.getTransform();

            // Calculate the transform
            AffineTransform patchTransform = new AffineTransform();
            patchTransform.translate( convertedPosition.x, convertedPosition.y );
            g2d.transform( patchTransform );
            
            // Draw the patch
            g2d.fill( patchShape );
            
            // Restore the original transform
            g2d.setTransform( original );

        }
    }
    
    /**
     * Draw lines from followers to leaders
     * 
     * @param g2d The graphics drawing object
     */
    private void drawLeaderLines( Graphics2D g2d )
    {
        Iterator<Agent> agentIter = _agents.iterator();
        g2d.setColor( _leaderLineColor );
        while( agentIter.hasNext() )
        {
            Agent current = agentIter.next();
            Decision currentDecision = current.getDecision();
            if( DecisionType.FOLLOW.equals( currentDecision.getType() ) )
            {
                // Get the original transformation
                AffineTransform original = g2d.getTransform();

                Agent leader = currentDecision.getLeader();
                Vector3f followerPosition = convert( current.getPosition() );
                Vector3f leaderPosition = convert( leader.getPosition() );
                Vector3f leaderOffset = leaderPosition.subtract( followerPosition );
                Line2D line = new Line2D.Float( 0, 0, leaderOffset.x, leaderOffset.y );
                
                AffineTransform followerTransform = new AffineTransform();
                followerTransform.translate( followerPosition.x, followerPosition.y );
                g2d.transform( followerTransform );
                g2d.draw( line );

                // Restore the original
                g2d.setTransform( original );
            }
        }
    }

    /**
     * Draws all the agents in the simulation
     *
     * @param g2d The graphics drawing object
     */
    private void drawAgents( Graphics2D g2d )
    {
        Iterator<Agent> agentIter = _agents.iterator();
        while( agentIter.hasNext() )
        {
            g2d.setColor( _agentColor );
            Agent current = agentIter.next();
            Vector3f converted = convert( current.getPosition() );
            drawAgent( g2d, converted, current.getVelocity() );
            
        }
    }
    
    /**
     * Draws a single agent
     *
     * @param g2d
     * @param position
     * @param velocity
     * @param size
     */
    private void drawAgent( Graphics2D g2d,
            Vector3f position,
            Vector3f velocity )
    {
        // Get the original transformation
        AffineTransform original = g2d.getTransform();
        
        // Calculate the rotation angle
        AffineTransform agentTransform = new AffineTransform();
        SphericalVector spherical = new SphericalVector( velocity );
        agentTransform.translate( position.x, position.y );
        agentTransform.rotate( Math.PI / -2.0f - spherical.theta );
        g2d.transform( agentTransform );
        g2d.fill( _agentPoly );
        
        // Restore the original
        g2d.setTransform( original );
    }

    /**
     * Convert regular y coordinate into display coordinate 
     *
     * @param y
     * @return
     */
    private int convertY( int y )
    {
        return getHeight() - y;
    }

    /**
     * Convert a simulation coordinate into a display coordinate
     *
     * @param original The original simulation coordinate
     * @return The display coordinate
     */
    private Vector3f convert( Vector3f original )
    {
        Vector3f converted = original.clone();

        // Translate it to center of mass
        converted.subtractLocal( _agentCenterOfMass );
        
        // Scale it
        converted.multLocal( _displayScale );
        
        // Translate it so the screen is centered
        Vector3f screenCenter = new Vector3f( getWidth() / 2, getHeight() / 2, 0 );
        converted.addLocal( screenCenter );
        
        // Reverse the y coordinate
        converted.y = getHeight() - converted.y;
        
//        _LOG.debug( "Original=[" + original + "]  converted=[" + converted + "]" );
        
        return converted;
    }

}
