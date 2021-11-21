<?php

$pw = filter_input(INPUT_GET, 'pw');

if ($pw == '@@REMOVED') {
	
	file_put_contents('shield.txt', time());

} else {

	$data = file_get_contents('shield.txt');
	$diff = (time() - $data);

	if ($diff > 120) {
		
		echo 'Offline';
		
	} else {
		
		if (!isset($_GET['full'])) {
			
			echo 'Online';
			
		} else {
			
			require_once('counters.php');

			echo 'Sistema Online' . "\n";
			echo $connected  . " jogadores online";
			
			//echo 'Manutenção' . "\n";
			//echo 'Estamos em manutenção, voltaremos em breve.';
		}
	}
}