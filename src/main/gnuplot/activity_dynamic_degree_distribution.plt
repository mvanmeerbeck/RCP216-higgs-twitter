unset label
set datafile separator ','
set termoption font "Open sans,18"

set term gif animate
set output "output.gif"
set nokey

set format x "10^{%T}"
set format y "10^{%T}"

stats ARG1 u 2:3 nooutput

set xlabel "Degree k"
set ylabel "Probability density P(k)"
set xrange [1E0 : 1E5]
set yrange [1E-5 : 1E0]
set logscale xy

do for [i=1:int(STATS_blocks)]{
    plot ARG1 index (i-1) using 2:3 pt 8 ps 0.5 lc rgb "#4daf4a"
}
