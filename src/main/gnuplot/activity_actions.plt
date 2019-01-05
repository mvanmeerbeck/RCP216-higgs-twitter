unset label
set nokey
set datafile separator ','

set xlabel "Seconds"
set ylabel "Number of actions"
set yrange [0 : 1E+2]
set logscale y

plot \
    ARG1 using 1:2 title "Number of actions" pt 7