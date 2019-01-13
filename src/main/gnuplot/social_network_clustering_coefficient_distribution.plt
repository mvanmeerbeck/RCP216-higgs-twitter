unset label
set datafile separator ','
set nokey
set pointsize

set format x "10^{%T}"
set format y "10^{%T}"

set xlabel "Degree k"
set ylabel "Average Clustering coefficient"
set logscale xy

plot ARG1 using 1:2 title "Average Clustering coefficient" pt 8 ps 0.5