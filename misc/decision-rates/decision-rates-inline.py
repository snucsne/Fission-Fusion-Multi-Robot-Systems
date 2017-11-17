import numpy as np

from bokeh.layouts import row, widgetbox, column
from bokeh.models import CustomJS, Slider, Div, LabelSet
from bokeh.plotting import figure, output_file, save, ColumnDataSource
from bokeh.models.tickers import FixedTicker

# Define some constants
groupSize = 10
nearestNeighborSize = 10
xAxisTicks = 100

# Create empty arrays
departedInds = np.zeros(xAxisTicks+1)
initiationRates = np.zeros(xAxisTicks+1)
followingRates = np.zeros(xAxisTicks+1)
cancellingRates = np.zeros(xAxisTicks+1)

# Create the data sources
initiationSource = ColumnDataSource( data = dict(x=departedInds,
        y=initiationRates) )
followingSource = ColumnDataSource( data = dict(x=departedInds,
        y=followingRates) )
cancellingSource = ColumnDataSource( data = dict(x=departedInds,
        y=cancellingRates) )


# Create a JavaScript callback to recalculate data when sliders are changed
callback = CustomJS( args=dict( initiationSource=initiationSource, followingSource=followingSource, cancellingSource=cancellingSource ), code="""
    //console.log( "Updating plot" );

    // Get the source data
    var initiationData = initiationSource.data;
    var followingData = followingSource.data;
    var cancellingData = cancellingSource.data;

    // Get slider values
    //var kValue = k.value;
    relPosValue = relPos.value;
    patchRelDirGroupValue = patchRelDirGroup.value;
    patchRelDirHeadingValue = patchRelDirHeading.value;
    patchValueValue = patchValue.value;
    groupDistDiffValue = groupDistDiff.value;

    // Define some constants
    var groupSize = 10;
    var nearestNeighborSize = 10;
    var xAxisTicks = 100;

    // Pull out the important data for calculations
    departedInds = initiationData['x']
    initiationRates = initiationData['y']
    followingRates = followingData['y']
    cancellingRates = cancellingData['y']

    // Calculate the different k values
    initKValue = 1 - relPosValue * patchRelDirGroupValue * patchValueValue;
    followKValue = groupDistDiffValue * patchRelDirHeadingValue * patchValueValue;
    cancelKValue = initKValue;

    //console.log( "k=[" + kValue + "]" );
    //console.log( "initK=[ " + initKValue + "]  followK=[" + followKValue + "]" )

    for( i = 0; i < departedInds.length; i++ )
    {
        departedInds[i] = groupSize * i / xAxisTicks;
        initiationRates[i] = 1 / (initKValue * 1290);
        cancellingRates[i] = cancelKValue * 0.009/(1+Math.pow(departedInds[i]/2, 2.3));
        if( i > 0 )
        {
            followingRates[i] = followKValue / (162.3 + 75.4*(nearestNeighborSize - departedInds[i]) / departedInds[i] )
        }
        //console.log( "i=[" + i + "]  departed=[" + departedInds[i] + "]  initiation=[" + initiationRates[i] + "] following=[" + followingRates[i] + "]" );
    }
    initiationSource.change.emit()
    followingSource.change.emit()
    cancellingSource.change.emit()
""")

# Create the sliders
# kSlider = Slider(title="k Slider",
#         value=1,
#         start=0.1,
#         end=1.9,
#         step=0.05,
#         callback=callback)
# callback.args["k"] = kSlider
relPosSlider = Slider(title="Relative position in group",
        value=0.5,
        start=0.05,
        end=0.95,
        step=0.05,
        callback=callback)
callback.args["relPos"] = relPosSlider
patchRelDirGroupSlider = Slider(title="Relative direction of patch to group",
        value=0.5,
        start=0.05,
        end=0.95,
        step=0.05,
        callback=callback)
callback.args["patchRelDirGroup"] = patchRelDirGroupSlider
patchRelDirHeadingSlider = Slider(title="Relative direction of patch to heading",
        value=0.5,
        start=0.05,
        end=0.95,
        step=0.05,
        callback=callback)
callback.args["patchRelDirHeading"] = patchRelDirHeadingSlider
patchValueSlider = Slider(title="Value of patch",
        value=0.5,
        start=0.05,
        end=0.95,
        step=0.05,
        callback=callback)
callback.args["patchValue"] = patchValueSlider
groupDistDiffSlider = Slider(title="Distance difference of groups",
        value=0.5,
        start=-0.95,
        end=0.95,
        step=0.05,
        callback=callback)
callback.args["groupDistDiff"] = groupDistDiffSlider



# Create the data for the initial plot
#k = kSlider.value
relPos = relPosSlider.value
patchRelDirGroup = patchRelDirGroupSlider.value
patchRelDirHeading = patchRelDirHeadingSlider.value
patchValue = patchValueSlider.value
groupDistDiff = groupDistDiffSlider.value

initK = 1 - relPos * patchRelDirGroup * patchValue
followK = groupDistDiff * patchRelDirHeading * patchValue
cancelK = initK

for i in range(xAxisTicks+1):
    departedInds[i] = 10 * i / xAxisTicks
    initiationRates[i] = 1 / (initK * 1290)
    cancellingRates[i] = cancelK * 0.009/(1+(departedInds[i]/2)**2.3)
    if( i > 0 ):
        followingRates[i] = followK/(162.3+75.4*(nearestNeighborSize-departedInds[i])/departedInds[i])


# Plot the rates
plot = figure( x_range=[0,10], y_range=[0,0.015], plot_width=800, plot_height=600 )
plot.line( 'x', 'y', source=initiationSource, legend="Initiation", color="#006600" )
plot.line( 'x', 'y', source=followingSource, legend="Following", color="#6666FF" )
plot.line( 'x', 'y', source=cancellingSource, legend="Cancelling", color="#FF6666" )
plot.xaxis.axis_label = "Departed individuals"
plot.xaxis.ticker = FixedTicker(ticks=np.arange(0,groupSize+1,1.0))
plot.xgrid.ticker = FixedTicker(ticks=np.arange(0,groupSize+1,1.0))


desc = Div( text="Decision rates model", width=800 )

# Create the layout
layout = column( desc, row(
    plot,
    widgetbox( relPosSlider,
        patchRelDirGroupSlider,
        patchRelDirHeadingSlider,
        patchValueSlider,
        groupDistDiffSlider ),
))

# Specify the output file
output_file( "decision-rates-model.html", title="Decision rates model" )

# Show the plot and sliders
save( layout )


