<?php

$type = filter_input(INPUT_GET, "type");
$file = filter_input(INPUT_GET, "file");

require_once('path.php');

if ($type == 'js') {
    $dir = PATH . 'cache/min/js/';
} else {
    $dir = PATH . 'cache/min/css/';
}

require_once($dir . 'A0_' . $file . '_.php');