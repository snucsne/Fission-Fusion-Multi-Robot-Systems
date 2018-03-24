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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.snu.csne.forage.decision.DecisionType;
import edu.snu.csne.forage.decision.DefaultProbabilityDecisionCalculator;
import edu.snu.csne.forage.event.DecisionCountEventListener;
import edu.snu.csne.forage.event.PatchDepletionListener;
import edu.snu.csne.forage.event.TeamSizeEventListener;
import edu.snu.csne.forage.evolve.FoldProperties;
import edu.snu.csne.forage.evolve.FoldProperties.FoldType;
import edu.snu.csne.forage.evolve.FoldProperties.PropertyType;
import edu.snu.csne.util.MiscUtils;

/**
 * TODO Class description
 *
 * @author Brent Eskridge
 */
public class EvolvedAgentAnalyzer
{
    /** Our logger */
    private static final Logger _LOG = LogManager.getLogger(
            EvolvedAgentAnalyzer.class.getName() );

    /** Parameter key for the results analysis filename */
    private static final String ANALYSIS_FILENAME = "analysis-filename";
    
    /** Parameter key for the fold properties file */
    private static final String FOLD_PROPERTIES_FILE = "fold-properties";

    /** Spacer comment for position history file */
    private static final String _SPACER =
            "# =========================================================";


    /** Filename for the analysis results */
    private String _analysisFilename = null;
    
    /** Default simulator properties */
    private Properties _defaultSimProperties = null;

    /** The fold properties definition file */
    private String _foldPropertiesFile = null;
    
    /** Fold property files */
    private FoldProperties _foldProps = new FoldProperties();

    /** The resources foraged in each simulation */
    private List<Float> _resourceHistory = new LinkedList<Float>();
    
    /** Histories of all the team sizes in each simulation */
    private List<List<int[]>> _teamSizeHistories = new LinkedList<List<int[]>>();
    
    /** Histories of all the decsion counts in each simulation */
    private List<List<int[]>> _decisionCountHistories = new LinkedList<List<int[]>>();
    
    
    /**
     * Main entry to the analyzer
     *
     * @param args The command line arguments
     */
    public static void main( String[] args )
    {
        try
        {
            _LOG.debug( "Starting analyzer..." );
            EvolvedAgentAnalyzer analyzer = new EvolvedAgentAnalyzer();
            analyzer.initialize();
            analyzer.runSimulations();
            analyzer.saveAnalysisResults();
        }
        catch( Exception e )
        {
            _LOG.error( "Unknown error", e );
            System.exit(1);
        }
    }


    /**
     * Initializes the analyzer
     */
    public void initialize()
    {
        _LOG.trace( "Entering initialize()" );

        // Get the analysis results filename
        _analysisFilename = MiscUtils.loadNonEmptyStringProperty(
                System.getProperties(),
                ANALYSIS_FILENAME,
                "results analysis filename" );
        
        // Load the default simulator properties
        _defaultSimProperties = MiscUtils.loadProperties( Simulator.PROPS_FILE_KEY );
        
        // Load the fold properties
        _foldPropertiesFile = MiscUtils.loadNonEmptyStringProperty(
                System.getProperties(),
                FOLD_PROPERTIES_FILE,
                "fold properties file" );
        _foldProps.initialize( _foldPropertiesFile );


        _LOG.trace( "Leaving initialize()" );
    }
    
    public void runSimulations()
    {
        _LOG.trace( "Entering runSimulations()" );

        // Get the validation fold
        String[] foldAgentProperties = _foldProps.getProperties(
                FoldType.VALIDATION,
                PropertyType.AGENT );
        String[] foldPatchProperties = _foldProps.getProperties(
                FoldType.VALIDATION,
                PropertyType.PATCH );
        
        // Run through each property
        for( int i = 0; i < foldAgentProperties.length; i++ )
        {
            // Get the default properties
            Properties simProps = new Properties();
            simProps.putAll( _defaultSimProperties );
            
            // Override the fold specific properties
            simProps.setProperty( SimulationState._AGENT_PROPS_FILE_KEY,
                    foldAgentProperties[i] );
            simProps.setProperty( SimulationState._PATCH_PROPS_FILE_KEY,
                    foldPatchProperties[i] );
            
            // Create the simulator
            Simulator sim = new Simulator();
            sim.initialize( simProps );
            SimulationState simState = sim.getSimState();

            // Add our own patch depletion listener
            PatchDepletionListener patchListener = new PatchDepletionListener();
            simState.addEventListener( patchListener );

            // Add our own team size event listener
            TeamSizeEventListener teamSizeListener = new TeamSizeEventListener();
            simState.addEventListener( teamSizeListener );
            
            // Add our own decision count event listener
            DecisionCountEventListener decisionListener = new DecisionCountEventListener();
            simState.addEventListener( decisionListener );
            
            // Run the simulation
            sim.run();
            
            // Get the results
            float resourcesForaged = patchListener.getTotalResourcesForaged();
            _resourceHistory.add( Float.valueOf( resourcesForaged ) );
            List<int[]> teamSizeHistory = teamSizeListener.getTeamSizeHistory();
            _teamSizeHistories.add( teamSizeHistory );
            List<int[]> decisionCountHistory = decisionListener.getDecisionCountHistory();
            _decisionCountHistories.add( decisionCountHistory );
        }
        
        _LOG.trace( "Leaving runSimulations()" );
    }
    
    public void saveAnalysisResults()
    {
        _LOG.trace( "Entering saveAnalysisResults()" );
        
        // Create the writer
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter( new BufferedWriter(
                    new FileWriter( _analysisFilename ) ) );
        }
        catch( IOException ioe )
        {
            _LOG.error( "Unable to open results analysis file ["
                    + _analysisFilename
                    + "]", ioe );
            throw new RuntimeException( "Unable to open results analysis file ["
                    + _analysisFilename
                    + "]", ioe );
        }
        
        // Log the system properties to the stats file for future reference
        writer.println( "# Started: " + (new Date()) );
        writer.println( _SPACER );
        writer.println( "# Simulation properties" );
        writer.println( _SPACER );
        List<String> keyList = new ArrayList<String>(
                _defaultSimProperties.stringPropertyNames() );
        Collections.sort( keyList );
        Iterator<String> iter = keyList.iterator();
        while( iter.hasNext() )
        {
            String key = iter.next();
            String value = _defaultSimProperties.getProperty( key );

            writer.println( "# " + key + " = " + value );
        }
        writer.println( "# "
                + DefaultProbabilityDecisionCalculator.PROB_DECISION_CALC_PROPS_FILE_KEY
                + " = "
                + System.getProperty( DefaultProbabilityDecisionCalculator.PROB_DECISION_CALC_PROPS_FILE_KEY ) );
        writer.println( "# " + FOLD_PROPERTIES_FILE + " = " + _foldPropertiesFile );
        writer.println( _SPACER );
        writer.println();
        writer.flush();

        // Output our results
        for( int i = 0; i < _resourceHistory.size(); i++ )
        {
            String foldID = String.format( "%02d", i );
            
            Float resources = _resourceHistory.get( i );
            List<int[]> teamSizes = _teamSizeHistories.get( i );
            List<int[]> decisionCounts = _decisionCountHistories.get( i );
            
            // Output the resources
            writer.println( "resources." + foldID + " = " + resources );
            
            // Output the team sizes
            StringBuilder teamSizeBuilder = new StringBuilder();
            Iterator<int[]> teamSizeIter = teamSizes.iterator();
            while( teamSizeIter.hasNext() )
            {
                int[] currentTeamSizes = teamSizeIter.next();
                StringJoiner joiner = new StringJoiner( "," );
                IntStream.of( currentTeamSizes ).forEach( x -> joiner.add( String.valueOf( x ) ) );
                teamSizeBuilder.append( joiner.toString() );
                if( teamSizeIter.hasNext() )
                {
                    teamSizeBuilder.append( " " );
                }
            }
            writer.println( "team-sizes."
                    + foldID
                    + " = "
                    + teamSizeBuilder.toString() );
            
            // Output the decision counts
            int decisionCountsSize = decisionCounts.size();
            DecisionType[] decisionTypes = DecisionType.values();
            StringBuilder[] decisionCountsBuilders =
                    new StringBuilder[ decisionTypes.length ];
            for( int j = 0; j < decisionCountsBuilders.length; j++ )
            {
                decisionCountsBuilders[j] = new StringBuilder();
            }
            for( int j = 0; j < decisionCountsSize; j++ )
            {
                int[] counts = decisionCounts.get(j);
                for( int k = 0; k < counts.length; k++ )
                {
                    decisionCountsBuilders[k].append( String.format( "%2d", counts[k] ) );
                    if( j < (decisionCountsSize - 1) )
                    {
                        decisionCountsBuilders[k].append( " " );
                    }
                }
            }
            for( int j = 0; j < decisionCountsBuilders.length; j++ )
            {
                writer.println( "decision-counts."
                        + decisionTypes[j].name().toLowerCase()
                        + "."
                        + foldID
                        + " = "
                        + decisionCountsBuilders[j].toString() );
            }
            writer.println();
        }
        
        // Clean up
        writer.flush();
        writer.close();
        
        _LOG.trace( "Leaving saveAnalysisResults()" );
    }
}
