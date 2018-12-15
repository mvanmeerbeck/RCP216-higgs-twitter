unset label
set datafile separator ','
set nokey
set pointsize

set format x "10^{%T}"
set format y "10^{%T}"

set xlabel "Degree k"
set ylabel "Probability density P(k)"
set xrange [1E0 : 1E5]
set yrange [1E-6 : 0.05]
set logscale xy

P(k)= c*k**-gamma
fit [200:400] P(x) ARG1 using 1:2 via c,gamma

set label gprintf("k^{-%.02f}",gamma) at 70,1E-2

plot [k = 1E0 : 1E5] ARG1 using 1:2 title "Degree" pt 8 ps 0.5, P(k) dashtype 3