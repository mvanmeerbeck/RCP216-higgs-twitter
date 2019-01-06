unset label
set nokey
set datafile separator ','
set style data linespoints

set xlabel "Time"
set ylabel "Activated users"
set logscale y

set xdata time
set timefmt "%s"
set format x "%d/%m/%Y"

plot \
    ARG1 using ($1):($2 / GPVAL_DATA_Y_MAX) title "Activated users"