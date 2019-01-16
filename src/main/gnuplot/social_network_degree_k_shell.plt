unset label
set datafile separator ','

set xlabel "Degree"
set ylabel "k-shell"


unset key
set style increment default
set view map scale 1
set style data lines
set xtics border in scale 0,0 mirror norotate  autojustify
set ytics border in scale 0,0 mirror norotate  autojustify
set ztics border in scale 0,0 nomirror norotate  autojustify
unset cbtics
set rtics axis in scale 0,0 nomirror norotate  autojustify
set title "Degree / k-shell"
set logscale yx
set cblabel "Count"
set cbrange [ 0.00000 : 5.00000 ] noreverse nowriteback
set rrange [ * : * ] noreverse writeback
set palette rgbformulae -7, 2, -7

plot ARG1 using 1:2:3 with image title "Degree / k-shell"