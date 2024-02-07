<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");


//mb_convert_encoding($array, "UTF-8", "iso-8859-1");

$servidor = 'localhost';
$usuario = 'root';
$contrasena = '';
$nombre_db = 'preguntas_v2_db';

try {
    $base_de_datos = new PDO("mysql:host=$servidor;dbname=$nombre_db",$usuario,$contrasena);
} catch (Exception $e) {
    echo "Ha surgido un error y no se puede conectar a base de datos";
}