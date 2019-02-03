unset label
set datafile separator ','
set style data line
set termoption font "Open sans,18"
set key below

set format y "10^{%T}"

set xlabel "Day"
set ylabel "Number"
set logscale y

set xdata time
set timefmt "%s"
set format x "%d/%m/%Y"

plot \
    ARG1 using 1:2 title "Number of vertices" lt rgb "#984ea3" lw 3, \
    ARG1 using 1:3 title "Number of edges" lt rgb "#4daf4a" lw 3