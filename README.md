# spring-hmac-rest
Spring HMAC authentication filter for RESTfull webservice example.

Client sends frollowing http request to the server:

    POST /api/echo HTTP/1.1
    Accept: application/json, application/*+json
    Content-Type: application/json
    Date: Thu, 29 Oct 2015 05:27:23 GMT
    Authorization: HmacSHA512 user:4314efa9-04c2-4109-a6a6-385797fa47a3:p0Mi/le2ph0XTwmnRZ8+IVf1D3kAbos14eJLeuL/Y8zpbV7tp1+4lmqgqtU9Z6XlBa3YylMD+Mdu+4RNcc6Y5w==
    User-Agent: RestAPI client v.1.0
    Content-Length: 24
    Host: localhost:8080
    
    {"data":{"name":"hoho"}}

The `Authorization` header format is:

    Authorization: HmacSHA512 <user>:<nonce>:<digest>
    
where `digest` is Base64 formatted HMAC SHA512 digest of the following string: 
 
    METHOD\n
    SCHEME\n
    HOST:PORT\n
    RESOURCE\n
    CONTENT_TYPE\n
    USER\
    NONCE\n
    DATE\n
    PAYLOAD\n

i.e. string to sign is:
            
    POST\n
    https\n
    localhost:8080\n
    /api/echo\n
    application/json\n
    user\n
    4314efa9-04c2-4109-a6a6-385797fa47a3\n
    Thu, 29 Oct 2015 05:27:23 GMT\n
    {"data":{"name":"hoho"}}\n

where user="user" and secret="secret".

Java client code example:

~~~java
ApiClient client = new ApiClient("localhost", 8080);
client.setCredentials("user", "secret");
client.init();

Foo request = new Foo("hoho");
FooResponseWrapper response = client.echo(request, FooResponseWrapper.class);
~~~

In PHP there is a function [`hash_hmac`][hash_hmac] for generating keyed hash value using the HMAC method. Here is the example:

~~~php
<?php

$string_to_sign = 'POST\n' .
'https\n' .
'localhost:8080\n' .
'/api/echo\n' .
'application/json\n' .
'user\n' . 
'4314efa9-04c2-4109-a6a6-385797fa47a3\n' .
'Thu, 29 Oct 2015 05:27:23 GMT\n' .
'{"data":{"name":"hoho"}}\n';

$secret = 'secret';

echo 'SHA-512 HMAC Digest: ', hash_hmac('sha512', $string_to_sign, $secret);
~~~

## Links

*  [Implementing HMAC authentication for REST API with Spring Security](http://www.massimilianosciacco.com/implementing-hmac-authentication-rest-api-spring-security)

* [JSON API Format](http://jsonapi.org/format)
* [The RESTful CookBook: HMAC](http://restcookbook.com/Basics/loggingin/)

[hash_hmac]:  http://php.net/manual/en/function.hash-hmac.php
[hash_algos]: http://php.net/manual/en/function.hash-algos.php
