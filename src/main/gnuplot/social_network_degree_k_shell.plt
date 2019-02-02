unset label
set datafile separator ','
set termoption font "Open sans,18"

set xlabel "Degree"
set ylabel "k-shell"

set format x "10^{%T}"
set format y "10^{%T}"

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

# line styles
set style line 1 lt 1 lc rgb '#FCFBFD' # very light purple
set style line 2 lt 1 lc rgb '#EFEDF5' #
set style line 3 lt 1 lc rgb '#DADAEB' #
set style line 4 lt 1 lc rgb '#BCBDDC' # light purple
set style line 5 lt 1 lc rgb '#9E9AC8' #
set style line 6 lt 1 lc rgb '#807DBA' # medium purple
set style line 7 lt 1 lc rgb '#6A51A3' #
set style line 8 lt 1 lc rgb '#4A1486' # dark purple

# palette
set palette defined ( 0 '#FCFBFD',\
    	    	      1 '#EFEDF5',\
		      2 '#DADAEB',\
		      3 '#BCBDDC',\
		      4 '#9E9AC8',\
		      5 '#807DBA',\
		      6 '#6A51A3',\
		      7 '#4A1486' )

set xrange [1E0 : 51386]
set yrange [1E0 : 201]
set logscale yx
set cblabel "Count"
set cbrange [ 0.00000 : 5.00000 ] noreverse nowriteback
set rrange [ * : * ] noreverse writeback

plot ARG1 using 1:2:3 with image title "Degree / k-shell"