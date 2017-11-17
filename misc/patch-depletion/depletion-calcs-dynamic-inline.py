import numpy as np

from bokeh.layouts import row, widgetbox, column
from bokeh.models import CustomJS, Slider, Div, LabelSet
from bokeh.plotting import figure, output_file, show, ColumnDataSource

# Define some constants
timeSteps = 400
resourcesMax = 200

# Create default time & resource values
xTime = np.arange(0,timeSteps+1,1)
yResources = np.zeros(timeSteps+1)

# Create default slope points
xSlope = np.array( [0,timeSteps] )
ySlope = np.array( [0,0] )

# Create the data sources
resourceSource = ColumnDataSource( data = dict(x=xTime,
        y=yResources ) )
slopeSource = ColumnDataSource( data = dict(x=xSlope,
        y=ySlope) )
labelSource = ColumnDataSource( data = dict(x=[0], y=[0], name=['']))


# Create a JavaScript callback to recalculate data when sliders are changed
callback = CustomJS( args=dict( resourcesSource=resourceSource, slopeSource=slopeSource, labelSource=labelSource ), code="""
    var data = resourcesSource.data;
    var slopeData = slopeSource.data;
    var labelData = labelSource.data;
    var resourcesValue = resources.value;
    var areaPatchValue = areaPatch.value;
    var agentCountValue = agentCount.value;
    var consumptionRateMaxValue = consumptionRateMax.value;
    var areaForagingValue = areaForaging.value;
    var travelTimeValue = travelTime.value;
    var resourcesRemaining = resourcesValue;
    var resourcesForagedTotal = 0;
    var slopeMax = 0;
    timeData = data['x']
    resourcesData = data['y']
    xSlopeData = slopeData['x']
    ySlopeData = slopeData['y']
    for( i = 0; i < timeData.length; i++ )
    {
        if( travelTimeValue > timeData[i] )
        {
            resourcesData[i] = 0;
        }
        else
        {
            var resourceDensity = resourcesRemaining / areaPatchValue;
            var areaForagingEffective = Math.min( areaForagingValue, areaPatchValue / agentCountValue );
            var resourcesForagedPerAgent = Math.min( consumptionRateMaxValue, areaForagingEffective * resourceDensity );
            var resourcesForaged = resourcesForagedPerAgent * agentCountValue;
            resourcesRemaining -= resourcesForaged;
            resourcesForagedTotal += resourcesForaged;
            resourcesData[i] = resourcesForagedTotal;

            if( timeData[i] > 0)
            {
                if( slopeMax < (resourcesForagedTotal/ timeData[i]) )
                {
                    xSlopeData[1] = resources.end / (resourcesForagedTotal/timeData[i]);
                    ySlopeData[1] = resources.end;//resourcesData[i];
                    labelData['x'][0] = timeData[i];
                    labelData['y'][0] = resourcesForagedTotal;
                    labelData['name'][0] = "Slope ["+(resourcesForagedTotal/ timeData[i])+"] at time [" + timeData[i] + "]";
                    slopeMax = resourcesForagedTotal/ timeData[i];
                }
            }

        }
    }
    slopeSource.change.emit();
    resourcesSource.change.emit();
""")


# Create the sliders
resourcesSlider = Slider(title="Initial resources",
        value=100,
        start=10,
        end=resourcesMax,
        step=10,
        callback=callback)
callback.args["resources"] = resourcesSlider
areaPatchSlider = Slider(title="Patch area",
        value=50,
        start=10,
        end=200,
        step=5,
        callback=callback)
callback.args["areaPatch"] = areaPatchSlider
agentCountSlider = Slider(title="Agent count",
        value=1,
        start=1,
        end=20,
        step=1,
        callback=callback)
callback.args["agentCount"] = agentCountSlider
consumptionRateMaxSlider = Slider(title="Consumption rate maximum",
        value=3,
        start=1,
        end=6,
        step=1,
        callback=callback)
callback.args["consumptionRateMax"] = consumptionRateMaxSlider
areaForagingSlider = Slider(title="Agent foraging area",
        value=1,
        start=0.5,
        end=5,
        step=0.25,
        callback=callback)
callback.args["areaForaging"] = areaForagingSlider
travelTimeSlider = Slider(title="Travel time",
        value=0,
        start=0,
        end=100,
        step=1,
        callback=callback)
callback.args["travelTime"] = travelTimeSlider

# Create the data for the initial plot
resourcesRemaining = resourcesSlider.value
resourcesForagedTotal = 0
slopeMax = 0
for i in range(timeSteps + 1):
    if travelTimeSlider.value > i:
        yResources[i] = 0
    else:
        resourceDensity = resourcesRemaining / areaPatchSlider.value
        areaForagingEffective = min( areaForagingSlider.value, areaPatchSlider.value / agentCountSlider.value )
        resourcesForagedPerAgent = min( consumptionRateMaxSlider.value, areaForagingEffective * resourceDensity )
        resourcesForaged = resourcesForagedPerAgent * agentCountSlider.value
        resourcesRemaining -= resourcesForaged
        resourcesForagedTotal += resourcesForaged
        yResources[i] = resourcesForagedTotal
        if( i > 0 ):
            slope = resourcesForagedTotal / i
            if( slopeMax < slope ):
                slopeMax = slope
                xSlope[1] = resourcesMax / (resourcesForagedTotal / i)
                ySlope[1] = resourcesMax
                labelSource.data['x'][0] = i
                labelSource.data['y'][0] = resourcesForagedTotal
                labelSource.data['name'][0] = "Slope ["+str(slope)+"] at time [" + str(i) + "]"



# Plot the resources and the slope line
plot = figure( x_range=[0,timeSteps], y_range=[0,resourcesMax], plot_width=800, plot_height=600 )
plot.line( 'x', 'y', source=resourceSource )
plot.line( 'x', 'y', source=slopeSource, line_color="#FF6666" )

labels = LabelSet( x='x', y='y', text='name', x_offset=10, y_offset=-10, source=labelSource )
plot.add_layout( labels )
plot.xaxis.axis_label = "Time"
plot.yaxis.axis_label = "Resources gathered"
desc = Div( text="Patch depletion Model", width=800 )

# Create the layout
layout = column( desc, row(
    plot,
    widgetbox( resourcesSlider,
        areaPatchSlider,
        agentCountSlider,
        consumptionRateMaxSlider,
        areaForagingSlider,
        travelTimeSlider),
))

# Specify the output file
output_file( "patch-depletion-model.html", title="Patch depletion model" )

# Show the plot and sliders
show( layout )