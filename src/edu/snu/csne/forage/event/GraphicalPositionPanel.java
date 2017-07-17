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
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jme3.math.Vector3f;

import edu.snu.csne.forage.Agent;
import edu.snu.csne.forage.SimulationState;
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

    /** The height of the panel */
    private int _width = 0;
    
    /** The width of the panel */
    private int _height = 0;
    
    /** Center of mass of all agents */
    private Vector3f _agentCenterOfMass = new Vector3f();
    
    /** The offset from the origin in the X dimension */
    private int _xOffset = 0;
    
    /** The offset from the origin in the Y dimension */
    private int _yOffset = 0;
    
    /** The step size of the grid lines */
    private int _gridStep = 0;
    
    /** The display scale */
    private float _displayScale = 40.0f;
    
    /** Background color */
    private Color _bgColor = null;
    
    /** Grid color */
    private Color _gridColor = null;
    
    /** All the agents */
    private List<Agent> _agents = new LinkedList<Agent>();
    
    /** Agent draw size */
    private float _agentDrawSize = 3.0f;
    
    
    public GraphicalPositionPanel( int width,
            int height,
            int gridStep,
            Color bgColor,
            Color gridColor,
            SimulationState simState )
    {
        // Set the preferred size
        setPreferredSize( new Dimension( width, height ) );
        _width = width;
        _height = height;
        _gridStep = gridStep;
        _bgColor = bgColor;
        _gridColor = gridColor;
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

        // Get the current panel size
        Dimension panelSize = getSize();
        _width = panelSize.width;
        _height = panelSize.height;
        
        // Adjust the offset so it is in the center of the panel
//        _xOffset = (int) (_agentCenterOfMass.x * _displayScale + _width / 2);
//        _yOffset = (int) (_agentCenterOfMass.y * _displayScale + _height / 2);
        _LOG.debug( "Scaled and centered: xOffset=[" + _xOffset + "] yOffset=[" + _yOffset + "]" );

        // Cast it to a 2d object for easier drawing
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Turn on anti-aliasing
        g2d.setRenderingHint( RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY );
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );

//        // Flip the coordinate system
//        g2d.scale(1, -1);
////        g2d.setTransform( AffineTransform.getTranslateInstance( 0, getHeight() ) );
//        
//        // Center the view on the center of mass of the agents
//        g2d.translate( _xOffset, _yOffset );
//        
//        // Draw a red circle at the center for TESTING
//        g2d.setColor( Color.GREEN ); 
//        g2d.fill( new Rectangle2D.Double( -5, convertY(10), 10, 20 ) );
//        g2d.setColor( Color.RED ); 
////        g2d.fillOval( _xOffset + _width / 2 - 5, _yOffset + _height / 2 - 5, 10, 10 );
//        g2d.fillOval( _width / 2 - 5, convertY(_height / 2 - 5), 10, 10 );
//        
        // Draw the grid
        drawGrid( g2d );
        
        // Draw the patches
//        drawPatches( g2d );
        
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
        // Get all the agents
        Map<String,Agent> agents = simState.getAllAgents();
        _agents = new LinkedList<Agent>( agents.values() );
        
        // Calculate the center of mass of the agents so we can offset
        Vector3f centerOfMass = new Vector3f();
        Iterator<Agent> agentIter = _agents.iterator();
        while( agentIter.hasNext() )
        {
            centerOfMass.addLocal( agentIter.next().getPosition() );
//            _LOG.debug( "centerOfMass=[" + centerOfMass + "]" );
        }
        _agentCenterOfMass = centerOfMass.divide( (float) _agents.size() );
        _LOG.debug( "Agent center of mass [" + _agentCenterOfMass + "]" );
        
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
        float xGridCenter = round( _agentCenterOfMass.x, _gridStep );
        float yGridCenter = round( _agentCenterOfMass.y, _gridStep );
//        System.out.println( "Grid center [" + xGridCenter + ","
//                + yGridCenter + "]" );

        // Calculate the screen coordinates of the center of the grid
        int xGridCenterScreen = getWidth() / 2
                + (int) Math.round( (xGridCenter - _agentCenterOfMass.x) * _displayScale );
        int yGridCenterScreen = getHeight() - (getHeight() / 2
                + (int) Math.round( (yGridCenter - _agentCenterOfMass.y) * _displayScale ));
//        System.out.println( "Grid center screen [" + xGridCenterScreen + ","
//                + yGridCenterScreen + "]" );

        // How many lines can fit on the screen?
        float gridStepPixels = _gridStep * _displayScale;
        int xLineCount = (int) Math.ceil( ((float) getWidth()) / gridStepPixels )
                + 2;
        int yLineCount = (int) Math.ceil( ((float) getHeight()) / gridStepPixels )
                + 2;
//        System.out.println( "Line count ["
//                + xLineCount
//                + ","
//                + yLineCount
//                + "]" );

        // Calculate the starting values of the first grid line
        int xGridStart = (int) (xGridCenterScreen - (-1 + xLineCount / 2) * gridStepPixels);
        int yGridStart = (int) (yGridCenterScreen - (-1 + yLineCount / 2) * gridStepPixels);
//        System.out.println( "Grid start [" + xGridStart + ","
//                + xGridStart + "]" );

        // Draw the vertical lines
        g2d.setColor( _gridColor );
        for( int i = 0; i < xLineCount; i++ )
        {
            // Calculate the x value and draw the line
            int x = (int) Math.round( xGridStart + i * gridStepPixels );
            g2d.drawLine( x, -1, x, getHeight() + 1 );
//            System.out.println( "x=[" + x + "]" );
        }
        
        // Draw the horizontal lines
        for( int i = 0; i < yLineCount; i++ )
        {
            // Calculate the y value and draw the line
            int y = (int) Math.round( yGridStart + i * gridStepPixels );
            g2d.drawLine( -1, y, getWidth() + 1, y );
//            System.out.println( "y=[" + y + "]" );
        }
    }
    
    /**
     * Draws all the patches in the simulation
     *
     * @param g2d The graphics drawing object
     */
    private void drawPatches( Graphics2D g2d )
    {
        // TODO
    }
    
    /**
     * Draws all the agents in the simulation
     *
     * @param g2d The graphics drawing object
     */
    private void drawAgents( Graphics2D g2d )
    {
        // TEMP
        g2d.setColor( Color.BLUE );
        
        // At the moment, just draw a circle for each agent
        Iterator<Agent> agentIter = _agents.iterator();
        while( agentIter.hasNext() )
        {
            Agent current = agentIter.next();
            _LOG.debug( "Converting position of agent ["
                    + current.getID()
                    + "]" );
            Vector3f converted = convert( current.getPosition() );
            drawAgent( g2d, converted, current.getVelocity(), 10 );
        }
    }
    
    private void drawAgent( Graphics2D g2d,
            Vector3f position,
            Vector3f velocity,
            float size )
    {
        // Simply draw a circle
        int diameter = (int) size;
        int halfDiameter = diameter / 2;
        
        g2d.fillOval( (int) position.x - halfDiameter,
                (int) position.y - halfDiameter,
                diameter,
                diameter );
    }
    
    private int convertY( int y )
    {
        return getHeight() - y;
    }

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
        
        _LOG.debug( "Original=[" + original + "]  converted=[" + converted + "]" );
        
        return converted;
    }

    public static float round(float input, float step) 
    {
        return (float) ((Math.round(input / step)) * step);
    }

}
