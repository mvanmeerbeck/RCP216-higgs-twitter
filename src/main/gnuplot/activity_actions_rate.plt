unset label
set nokey
set datafile separator ','
set style data line
set termoption font "Open sans,18"

set format y "10^{%T}"

set xlabel "Day"
set ylabel "Number of actions"
set logscale y

set xdata time
set timefmt "%s"
set format x "%d/%m/%Y"

plot \
    ARG1 using 1:2 title "Number of actions" lt rgb "#984ea3" lw 3