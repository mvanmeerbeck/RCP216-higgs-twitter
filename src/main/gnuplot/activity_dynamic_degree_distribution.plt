unset label
set datafile separator ','

set term gif animate
set output "output.gif"

set format x "10^{%T}"
set format y "10^{%T}"

stats ARG1 u 2:3 nooutput

set xlabel "Degree k"
set ylabel "Probability density P(k)"
set xrange [1E0 : 1E5]
set yrange [1E-5 : 1E0]
set logscale xy

print int(STATS_blocks)

#plot for [i=1:int(STATS_blocks)] ARG1 index (i-1) using 2:3

do for [i=1:int(STATS_blocks)]{
    plot ARG1 index (i-1) using 2:3
}
