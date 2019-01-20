unset label
set datafile separator ','
set style data linespoints

set xlabel "Day"
set ylabel "Number"
set logscale y

set xdata time
set timefmt "%s"
set format x "%d/%m/%Y"

plot \
    ARG1 using 1:2 title "Number of vertices", \
    ARG1 using 1:3 title "Number of edges"