unset label
set nokey
set datafile separator ','

set xlabel "Time"
set ylabel "Activated users"
set logscale y

set xdata time
set timefmt "%s"
set format x "%d/%m/%Y"

lambda = 1E-8
lambda2 = 1E-8
lambda3 = 1E-8
lambda4 = 1E-8

A1(t) = 1 - (1 - 0) * exp(- lambda * ((t - 1341100800)))
fit [1341100800:1341151200] A1(x) ARG1 using 1:2 via lambda

A2(t) = 1 - (1 - 0.0022657393719642495) * exp(- lambda2 * ((t - 1341151200)))
fit [1341151200:1341230000] A2(x) ARG1 using 1:2 via lambda2

A3(t) = 1 - (1 - 0.018218978520218056) * exp(- lambda3 * ((t - 1341230400)))
fit [1341230400:1341370800] A3(x) ARG1 using 1:2 via lambda3

A4(t) = 1 - (1 - 0.12244298963057353) * exp(- lambda4 * ((t - 1341370800)))
fit [1341370800:1341702000] A4(x) ARG1 using 1:2 via lambda4

plot \
    ARG1 using 1:2 title "Activated users", \
    [t = 1341100800:1341151200] A1(t) dashtype 3 lw 3, \
    [t = 1341151200:1341230400] A2(t) dashtype 3 lw 3, \
    [t = 1341230400:1341370800] A3(t) dashtype 3 lw 3, \
    [t = 1341370800:1341702000] A4(t) dashtype 3 lw 3
