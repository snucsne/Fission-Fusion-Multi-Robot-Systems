# Patch depletion model for Marginal Value Theorem
from os.path import dirname, join

import numpy as np
from bokeh.plotting import figure, output_file, show
from bokeh.layouts import layout, widgetbox
from bokeh.models import ColumnDataSource, HoverTool, Div
from bokeh.models.widgets import Slider, Select, TextInput
from bokeh.io import curdoc

desc = Div( text="Patch depletion", width=800 )
resourcesSlider = Slider(title="Initial resources", value=100, start=10, end=200, step=10)
areaPatchSlider = Slider(title="Patch area", value=30, start=10, end=50, step=5)
agentCountSlider = Slider(title="Agent count", value=5, start=1, end=20, step=1)
consumptionRateMaxSlider = Slider(title="", value=25, start=10, end=50, step=5)
areaForagingSlider = Slider(title="Agent foraging area", value=1, start=1, end=10, step=1)
travelTimeSlider = Slider(title="Travel time", value=0, start=0, end=100, step=5)
timeStepsMaxSlider = Slider(title="Time steps max", value=200, start=10, end=400, step=10)



def calcDepletion( resourcesInitial,
                   areaPatch,
                   agentCount,
                   consumptionRateMax,
                   areaForaging,
                   travelTime,
                   timeStepsMax ):

    print( "****************************************************" )

    # Define some initial variables
    foragingHistory = list()
    resourcesRemaining = resourcesInitial
    resourcesForagedTotal = 0
    foragingSlopeMax = { 'slope': 0, 'time': 0, 'resources': 0 }

    # Iterate through each timestep
    for time in range(timeStepsMax + 1):

        # Calculate the current resource density
        resourceDensity = resourcesRemaining / areaPatch

        # Calculate the effective foraging area for an agent
        areaForagingEffective = min( areaForaging,
                                         areaPatch / agentCount )

        # Save the current data
        foragingData = { 'resourceDensity': resourceDensity,
                         'areaForagingEffective': areaForagingEffective,
                         'resourcesForagedPerAgentMax': 0,
                         'resourcesForagedPerAgentPreferred': 0,
                         'resourcesForagedPerAgent': 0,
                         'resourcesForaged': 0,
                         'resourcesRemaining': resourcesRemaining,
                         'resourcesForagedTotal': 0,
                         'percentageOfResourcesForaged': 0,
                         'foragingSlope': 0,
                         'time': time };

        # Have we arrived?
        if( travelTime <= time ):
            # Yes, they can start foraging

            # Calculate the max possible resources foraged per agent
            resourcesForagedPerAgentMax = resourceDensity / agentCount
            
            # Calculate the amount of resources foraged per agent
            resourcesForagedPerAgentPreferred = min( consumptionRateMax,
                    areaForagingEffective * resourceDensity )
            resourcesForagedPerAgent = min( resourcesForagedPerAgentMax,
                                            resourcesForagedPerAgentPreferred )

            print( resourcesForagedPerAgentMax, consumptionRateMax, (areaForagingEffective * resourceDensity), resourcesForagedPerAgentPreferred, resourcesForagedPerAgent )

            # Calculate the total amount of resources foraged
            resourcesForaged = resourcesForagedPerAgent * agentCount

            # Update the remaining resources
            resourcesRemaining -= resourcesForaged

            # Update the total amount of resources foraged
            resourcesForagedTotal += resourcesForaged

            # Calculate the total percentage of resources foraged
            percentageOfResourcesForaged = resourcesForagedTotal / resourcesInitial

            # Calculate the current slope
            foragingSlope = 0
            if( time > 0 ):
                foragingSlope = percentageOfResourcesForaged / time

            # Update the data
            foragingData = { 'resourceDensity': resourceDensity,
                             'areaForagingEffective': areaForagingEffective,
                             'resourcesForagedPerAgentMax': resourcesForagedPerAgentMax,
                             'resourcesForagedPerAgentPreferred': resourcesForagedPerAgentPreferred,
                             'resourcesForagedPerAgent': resourcesForagedPerAgent,
                             'resourcesForaged': resourcesForaged,
                             'resourcesRemaining': resourcesRemaining,
                             'resourcesForagedTotal': resourcesForagedTotal,
                             'percentageOfResourcesForaged': percentageOfResourcesForaged,
                             'foragingSlope': foragingSlope,
                             'time': time };

            # Is it the current max?
            if( foragingSlope > foragingSlopeMax['slope'] ):
                # Yup
                foragingSlopeMax['slope'] = foragingSlope
                foragingSlopeMax['time'] = time
                foragingSlopeMax['resources'] = percentageOfResourcesForaged

        foragingHistory.append( foragingData )

    # Return the data
    return {'history': foragingHistory, 'slope': foragingSlopeMax}

def buildPlotData( rawData ):
    times=[]
    resourcePercentages=[]
    for foragingData in rawData['history']:
        times.append( foragingData['time'] )
        resourcePercentages.append( foragingData['percentageOfResourcesForaged'] )
#        print( foragingData['time'], foragingData['percentageOfResourcesForaged'] )

    return {'x': times, 'y': resourcePercentages }

rawData = calcDepletion( 100, 30, 5, 25, 1, 20, 200 )
plotSource = ColumnDataSource( data=buildPlotData( rawData ) )
slopeSource = ColumnDataSource( data={'x': [0, 1/(rawData['slope']['slope'])], 'y': [0,1] } )


plot = figure(plot_height=600, plot_width=700, x_range=[0,200], y_range=[0,1] )
plot.line( 'x', 'y', source=plotSource )
plot.line( 'x', 'y', source=slopeSource, line_color="#FF6666" )


def update():
    # Get the slider values
    resources = resourcesSlider.value
    areaPatch = areaPatchSlider.value
    agentCount = agentCountSlider.value
    consumptionRateMax = consumptionRateMaxSlider.value
    areaForaging = areaForagingSlider.value
    travelTime = travelTimeSlider.value
    timeStepsMax = timeStepsMaxSlider.value

    rawData = calcDepletion( resources,
                             areaPatch,
                             agentCount,
                             consumptionRateMax,
                             areaForaging,
                             travelTime,
                             timeStepsMax )

    #plot.x_range = [0, timeStepsMax]
    plotSource.data = buildPlotData( rawData )
    slopeSource.data = {'x': [0, 1/(rawData['slope']['slope'])], 'y': [0,1] }


controls = [ resourcesSlider, areaPatchSlider, agentCountSlider,
             consumptionRateMaxSlider, areaForagingSlider,
             travelTimeSlider, timeStepsMaxSlider ]
for control in controls:
    control.on_change('value', lambda attr, old, new: update())


##rawData = calcDepletion( 100, 30, 5, 25, 1, 20, 200 )
##plotData = buildPlotData( rawData )

inputs = widgetbox(*controls)
plotLayout = layout([
    [desc],
    [inputs, plot],
])

##output_file( "patch_depletion.html" )
##plot = figure(plot_height=600, plot_width=700, x_range=[0,200], y_range=[0,1] )
##plot.line( plotData['times'], plotData['resourcePercentages'] )
##plot.line( [0, 1/(rawData['slope']['slope'])], [0,1], line_color="#FF6666" )
##
##show(plot)

update()

curdoc().add_root(plotLayout)
curdoc().title = "Patch Depletion"

