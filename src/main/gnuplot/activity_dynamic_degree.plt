unset label
set datafile separator ','
set style data linespoints

set xlabel "t"
set ylabel "Degrees"
set logscale y

set xdata time
set timefmt "%s"
set format x "%d/%m/%Y"

plot \
    ARG1 using 1:2 title "In degrees", \
    ARG1 using 1:3 title "Out degrees"