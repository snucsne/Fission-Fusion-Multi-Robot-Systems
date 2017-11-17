# Decision rates

import numpy as np

from bokeh.layouts import row, widgetbox, column, layout
from bokeh.models import CustomJS, Slider, Div, LabelSet
from bokeh.plotting import figure, output_file, show, ColumnDataSource
from bokeh.models.tickers import FixedTicker
from bokeh.io import curdoc

groupSize = 10
nearestNeighborSize = 10
xAxisTicks = 100

# 
departedInds = np.zeros(xAxisTicks+1)
initiationRates = np.zeros(xAxisTicks+1)
followingRates = np.zeros(xAxisTicks+1)
cancellingRates = np.zeros(xAxisTicks+1)

initiationSource = ColumnDataSource( data = dict(x=departedInds,
        y=initiationRates) )
followingSource = ColumnDataSource( data = dict(x=departedInds,
        y=followingRates) )
cancellingSource = ColumnDataSource( data = dict(x=departedInds,
        y=cancellingRates) )

def calculateRates(k):
    departedInds = followingSource.data['x']
    followingRates = followingSource.data['y']
    cancellingRates = cancellingSource.data['y']
    for i in range(xAxisTicks+1):
        departedInds[i] = 10 * i / xAxisTicks
        initiationRates[i] = 1 / (k * 1290)
        cancellingRates[i] = k * 0.009/(1+(departedInds[i]/2)**2.3)
        if( i > 0 ):
            followingRates[i] = k/(162.3+75.4*(nearestNeighborSize-departedInds[i])/departedInds[i])
    initiationSource.data = {'x': departedInds, 'y': initiationRates}
    followingSource.data = {'x': departedInds, 'y': followingRates}
    cancellingSource.data = {'x': departedInds, 'y': cancellingRates}


kSlider = Slider( title="k value", value = 1, start=0.1, end=1.9, step=0.05 )

#calculateRates(kSlider.value)
calculateRates(1)



def update():
    kValue = kSlider.value
    calculateRates(kValue)
#    calculateRates(1)

controls = [ kSlider ]
for control in controls:
    control.on_change('value', lambda attr, old, new: update())

plot = figure( x_range=[0,10], y_range=[0,0.015], plot_width=800, plot_height=600 )
plot.line( 'x', 'y', source=initiationSource, legend="Initiation", color="#006600" )
plot.line( 'x', 'y', source=followingSource, legend="Following", color="#6666FF" )
plot.line( 'x', 'y', source=cancellingSource, legend="Cancelling", color="#FF6666" )
plot.xaxis.axis_label = "Departed individuals"
plot.xaxis.ticker = FixedTicker(ticks=np.arange(0,groupSize+1,1.0))
plot.xgrid.ticker = FixedTicker(ticks=np.arange(0,groupSize+1,1.0))

output_file( "decision-rates.html", title="Decision rates" )

inputs = widgetbox(*controls)
plotLayout = layout([plot,inputs])

#update()
curdoc().add_root(plotLayout)
curdoc().title = "Decision rates"

