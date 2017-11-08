from bokeh.plotting import show, output_file
from bokeh.layouts import column
from bokeh.models import CustomJS, Slider

s1 = Slider(start=1, end=10, value=1, step=1)
s2 = Slider(start=0, end=1, value=0, step=1)

s1.callback = CustomJS(args=dict(s1=s1, s2=s2), code="""
    s2.end = s1.value;
""")

output_file("foo.html")

show(column(s1,s2))

