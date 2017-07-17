set xlabel "Time"
set ylabel "Percentage foraged"
set format y "%04.2f"
set key right bottom
set yrange [0:1]
set xrange [0:200]

plot "forage-highresources-higharea-single-agent.dat" using 1:8 with lines title "High resources, big area, 1 agent"
replot "forage-highresources-medarea-single-agent.dat" using 1:8 with lines title "High resources, med area, 1 agent"
replot "forage-highresources-lowarea-single-agent.dat" using 1:8 with lines title "High resources, small area, 1 agent"


plot "forage-lowresources-higharea-single-agent.dat" using 1:8 with lines title "Low resources, big area, 1 agent"
replot "forage-lowresources-lowarea-single-agent.dat" using 1:8 with lines title "Low resources, small area, 1 agent"

replot "forage-lowresources-higharea-two-agents.dat" using 1:8 with lines title "Low resources, big area, 2 agents"
replot "forage-lowresources-higharea-five-agents.dat" using 1:8 with lines title "Low resources, big area, 5 agents"
replot "forage-lowresources-higharea-ten-agents.dat" using 1:8 with lines title "Low resources, big area, 10 agents"

