<?php

$rows = [];
$min = [INF, INF];
$max = [-INF, -INF];

if (($handle = fopen("G:\projets\HiggsTwitter\SocialNetwork\KCoreComponents\DegreeKShell\part-00000", "r")) !== FALSE) {
    while (($data = fgetcsv($handle, 2000, ",")) !== FALSE) {
        $min[0] = min($data[0], $min[0]);
        $min[1] = min($data[1], $min[1]);

        $max[0] = max($data[0], $max[0]);
        $max[1] = max($data[1], $max[1]);

        $rows[$data[0]][$data[1]] = $data[2];
    }
    fclose($handle);
}
$handle = fopen("test.csv", "w+");
for ($degree = $min[0]; $degree < $max[0] + 1; $degree++) {
    for ($kShell = $min[1]; $kShell < $max[1] + 1; $kShell++) {
        $count = 0;
        if (array_key_exists($degree, $rows) && array_key_exists($kShell, $rows[$degree])) {
            $count = $rows[$degree][$kShell];
        }

        fwrite($handle, "$degree,$kShell,$count" . "\n");
    }
    fwrite($handle, "\n");
}
fclose($handle);