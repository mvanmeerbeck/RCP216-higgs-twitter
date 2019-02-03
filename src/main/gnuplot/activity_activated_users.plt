unset label
set key bmargin center horizontal Right noreverse enhanced autotitles nobox
set datafile separator ','
set termoption font "Open sans,18"

set xlabel "Time"
set ylabel "Activated users"
set logscale y

lambda = 1E-8
lambda2 = 1E-8
lambda3 = 1E-8
lambda4 = 1E-8

t1 = 1341100800
t2 = 1341151200
t3 = 1341230400
t4 = 1341370800
t5 = 1341702000

set xdata

set xrange [t1 : t2]
stats ARG1 using 1:2 prefix "A1"

set xrange [t2 : t3]
stats ARG1 using 1:2 prefix "A2"

set xrange [t3 : t4]
stats ARG1 using 1:2 prefix "A3"

set xrange [t4 : t5]
stats ARG1 using 1:2 prefix "A4"

set xdata time
set timefmt "%s"
set format x "%d/%m/%Y"
set format y "10^{%T}"
set style rect fc lt -1 fs solid 0.15 noborder
set xrange [t1 : t5]

set object rect from t1, 0 to t2, 1
# set object rect from t2, 0 to t3, 1
set object rect from t3, 0 to t4, 1
# set object rect from t4, 0 to t5, 1

set label "Period I" at (t1 + (t2 - t1) / 2), 0.5 center
set label "Period II" at (t2 + (t3 - t2) / 2), 0.5 center
set label "Period III" at (t3 + (t4 - t3) / 2), 0.5 center
set label "Period IV" at (t4 + (t5 - t4) / 2), 0.5 center

A1(t) = 1 - (1 - 0) * exp(- lambda * ((t - t1)))
fit [t1:t2] A1(x) ARG1 using 1:2 via lambda

A2(t) = 1 - (1 - A1(t2)) * exp(- lambda2 * ((t - t2)))
fit [t2:t3] A2(x) ARG1 using 1:2 via lambda2

A3(t) = 1 - (1 - A2(t3)) * exp(- lambda3 * ((t - t3)))
fit [t3:t4] A3(x) ARG1 using 1:2 via lambda3

A4(t) = 1 - (1 - A3(t4)) * exp(- lambda4 * ((t - t4)))
fit [t4:t5] A4(x) ARG1 using 1:2 via lambda4

set label gprintf("%.02f", lambda * 279379 * 60) at (t1 + (t2 - t1) / 2), 0.25 center
set label gprintf("%.02f users/mn", lambda2 * 279379 * 60) at (t2 + (t3 - t2) / 2), 0.25 center
set label gprintf("%.02f users/mn", lambda3 * 279379 * 60) at (t3 + (t4 - t3) / 2), 0.25 center
set label gprintf("%.02f users/mn", lambda4 * 279379 * 60) at (t4 + (t5 - t4) / 2), 0.25 center

plot \
    ARG1 using 1:2 title "Activated users" with lines lw 10 lc rgb "#4daf4a", \
    [t = 1341100800:1341151200] A1(t) lt rgb "#e41a1c" dashtype 3 lw 6 title "Fit Period I", \
    [t = 1341151200:1341230400] A2(t) lt rgb "#377eb8" dashtype 3 lw 6 title "Fit Period II", \
    [t = 1341230400:1341370800] A3(t) lt rgb "#984ea3" dashtype 3 lw 6 title "Fit Period III", \
    [t = 1341370800:1341702000] A4(t) lt rgb "#ff7f00" dashtype 3 lw 6 title "Fit Period IV"
