unset label
set datafile separator ','
set border 3 front lt black linewidth 1.000 dashtype solid

set style increment default
set style data linespoints
set termoption font "Open sans,18"
set key below

set xlabel "Iteration"
set ylabel "Average Shortest Paths"

plot sum = 0, \
    ARG1 using 1:2 title "Average Shortest Paths" with points pt 7 lc rgb "#4daf4a", \
    '' using 0:(sum = sum + $2, sum/($0+1)) title "Cumulative mean" pt 1 lw 3 lc rgb "#984ea3"