<?php

$type = filter_input(INPUT_GET, 'type', FILTER_SANITIZE_NUMBER_INT);


$fullurl = '@@REMOVED';
$liteurl = '@@REMOVED';

$url;

if ($type == 1) {
    $url = shortenHere($fullurl);
} else if ($type == 2) {
    $url = $fullurl;
} else if ($type == 3) {
    $url = shortenHere($liteurl);
} else if ($type == 4) {
    $url = $liteurl;
}

header('Location: ' . $url);

function shortenHere($input) {
    
    require_once('path.php');
    require_once(PATH . 'shorten_function.php');
    
    return shorten($input);
}