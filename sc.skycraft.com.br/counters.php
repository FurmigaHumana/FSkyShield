<?php

require_once('@@REMOVEDsettings.php');

$stmt = $dbs2->prepare('@@REMOVED');
$stmt->execute();
$stmt->bind_result($online, $setups, $scans);
$stmt->fetch();
$stmt->close();

$stmt = $dbs2->prepare('@@REMOVED');
$stmt->execute();
$stmt->bind_result($maxbuild);
$stmt->fetch();
$stmt->close();

$connected = $online;