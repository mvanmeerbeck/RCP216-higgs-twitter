unset label
set datafile separator ','
set nokey
set pointsize

set xlabel "k-shell"
set ylabel "Probability density P(k-shell)"

set logscale xy

plot ARG1 using 1:2 title "k-shell distribution" pt 8 ps 0.5