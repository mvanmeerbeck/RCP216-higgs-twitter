unset label
set nokey
set datafile separator ','
set style data linespoints

set xlabel "Seconds"
set ylabel "Number of actions"
set logscale y

set xdata time
set timefmt "%s"
set format x "%d/%m/%Y"

plot \
    ARG1 using 1:2 title "Number of actions"