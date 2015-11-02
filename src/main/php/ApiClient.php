<?php
/**
 * Created by IntelliJ IDEA.
 * User: maestro
 * Date: 02.11.15
 * Time: 4:11
 */

namespace com\github\kpavlov\hmac;

class ApiClient
{

    const SCHEMA = 'http';
    const API_URI = '/api';
    private $host;
    private $port;
    private $api_url;
    private $api_key;
    private $api_secret;
    private $debug = false;

    /**
     * @param boolean $debug
     */
    public function setDebug($debug)
    {
        $this->debug = $debug;
    }

    /**
     * ApiClient constructor.
     * @param $host string hostname
     * @param $port int port
     */
    public function __construct($host, $port)
    {
        $this->host = $host;
        $this->port = $port;
        $this->api_url = self::SCHEMA . '://' . $host . ':' . $port . self::API_URI;
    }

    /**
     * @param mixed $api_key
     */
    public function setApiKey($api_key)
    {
        $this->api_key = $api_key;
    }

    /**
     * @param mixed $api_secret
     */
    public function setApiSecret($api_secret)
    {
        $this->api_secret = $api_secret;
    }

    /**
     * Sends echo request.
     *
     * @param $data
     * @return mixed
     * @throws \Exception
     */
    public function echoRequest($data)
    {
        $post_data = array('data' => $data);

        return $this->sendRequest('/echo', $post_data);
    }

    private function sendRequest($request_uri, $data)
    {
        $ch = curl_init($this->api_url . $request_uri);
        try {
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            if ($this->debug) {
                curl_setopt($ch, CURLOPT_VERBOSE, true);
            }
            curl_setopt($ch, CURLOPT_POST, true);
            $payload = json_encode($data);
            $date = gmdate(DATE_RFC2822);

            curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);

            curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                    'Accept: application/json',
                    'Content-Type: application/json',
                    'Date: ' . $date,
                    'Content-Length: ' . strlen($payload),
                    'Authorization: ' . $this->createAuthHeader(
                        self::API_URI . $request_uri,
                        $date,
                        $payload
                    ))

            );

            $res = curl_exec($ch);
            $error_code = curl_errno($ch);
            $error = curl_error($ch);
            if ($error_code !== 0) {
                throw new \Exception('Error #' . $error_code . ': ' . $error);
            }

            $info = curl_getinfo($ch);
            $http_code = $info['http_code'];
            if ($http_code != 200) {
                if ($this->debug) {
                    syslog(LOG_DEBUG, 'Response: ' . print_r($info) . $http_code);
                }
                throw new \Exception('Server Status: ' . $http_code);
            }

            return json_decode($res);
        } finally {
            curl_close($ch);
        }
    }

    private function createAuthHeader($uri, $date, $payload)
    {
        $nonce = uniqid();
        $string_to_sign =
            "POST\n" .
            self::SCHEMA . "\n" .
            $this->host . ':' . $this->port . "\n" .
            $uri . "\n" .
            "application/json\n" .
            $this->api_key . "\n" .
            $nonce . "\n" .
            $date . "\n" .
            $payload . "\n";

        if ($this->debug) {

            syslog(LOG_DEBUG, "Signing:\n" . $string_to_sign);
        }

        $digest = hash_hmac('sha512', $string_to_sign, $this->api_secret, true);
        return 'HmacSHA512 ' . $this->api_key . ':' . $nonce . ':' . base64_encode($digest);
    }

}