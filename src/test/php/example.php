<?php

$api_key = 'user';
$api_secret = 'secret';
$nonce = '4314efa9-04c2-4109-a6a6-385797fa47a3';

$string_to_sign = 'POST' . "\n" .
    'https' . "\n" .
    'localhost:8080' . "\n" .
    '/api/echo' . "\n" .
    'application/json' . "\n" .
    $api_key . "\n" .
    '4314efa9-04c2-4109-a6a6-385797fa47a3' . "\n" .
    'Thu, 29 Oct 2015 05:27:23 GMT' . "\n" .
    '{"data":{"name":"hoho"}}' . "\n";

$digest = hash_hmac('sha512', $string_to_sign, $api_secret, true);
$header = 'Authorization: HmacSHA512 ' . $api_key . ':' . $nonce . ':' . base64_encode($digest);
echo $header;