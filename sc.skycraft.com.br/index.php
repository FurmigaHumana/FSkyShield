<?php

require_once(dirname(__FILE__) . '/path.php');
ob_start();

if (session_id() == '') {
    session_name(md5(filter_input(INPUT_SERVER, 'HTTP_HOST')));
    session_start();
}

header('Cache-control: public, max-age=' . 60);
header('Expires: ' . gmdate(DATE_RFC1123, time() + 60));

$url = 'page_home.php';

require_once(PATH . 'settings.php');
require_once(PATH . 'min/html.php');

$gzip = settings::$compress_gzip;

if ($gzip) {
    
    $encoding = filter_input(INPUT_SERVER, 'HTTP_ACCEPT_ENCODING');
    
    if (!$encoding) {
        $gzip = false;
    } else {
        $gzip = substr_count($encoding, 'gzip');
    }
}

if ($gzip) {
    header('Content-Encoding: gzip');
}

register_shutdown_function('complete');

include_once($url);

function complete() {
    
    GLOBAL $gzip;

    $buffer = ob_get_contents();
    ob_clean();

    if (settings::$compress_html) {
        $compressor = new html($buffer);
        $buffer = $compressor->process();
    }

    if ($gzip) {
        $buffer = ob_gzhandler($buffer, 1);
    }

    echo $buffer;

    ob_end_flush();
}