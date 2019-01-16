unset label
set datafile separator ','
set nokey
set pointsize

set format x "10^{%T}"
set format y "10^{%T}"

set xlabel "Degree k"
set ylabel "Average Clustering coefficient"
set logscale xy

P(k)= c*k**-gamma
fit [10:10000] P(x) ARG1 using 1:2 via c,gamma

set label gprintf("k^{-%.02f}",gamma) at 30000,1E-4

plot [k = 1E0 : 1E5] ARG1 using 1:2 title "Average Clustering coefficient" pt 8 ps 0.5, \
    P(k) dashtype 3