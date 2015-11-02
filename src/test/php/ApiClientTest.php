<?php

namespace com\github\kpavlov\hmac;

require_once '../../main/php/ApiClient.php';

$api_client = new ApiClient('localhost', 8080);
$api_client->setApiKey('user');
$api_client->setApiSecret('secret');
$api_client->setDebug(true);

$foo = (object)array('name' => 'hoho');

echo 'Request', print_r($foo);

$result = $api_client->echoRequest($foo);

echo print_r($result);