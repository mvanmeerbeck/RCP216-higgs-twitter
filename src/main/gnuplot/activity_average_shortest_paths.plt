unset label
set datafile separator ','
set border 3 front lt black linewidth 1.000 dashtype solid
set key enhanced autotitle box lt black linewidth 1.000 dashtype solid
set key invert samplen 4 spacing 1 width 0 height 0
set style increment default
set style data linespoints

set xlabel "Iteration"
set ylabel "Average Shortest Paths"

plot sum = 0, \
    ARG1 using 1:2 title "Average Shortest Paths" with points pt 7, \
    '' using 0:(sum = sum + $2, sum/($0+1)) title "Cumulative mean" pt 1 lw 1 lc rgb "dark-red"