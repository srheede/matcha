<?php

$DB_DSN = 'mysql:host=localhost;dbname=Matcha';
$DB_DSN_LITE = 'mysql:host=localhost';
$DB_USER = 'root';
$DB_PASSWORD = 'root';

$pdo = new PDO($DB_DSN, $DB_USER, $DB_PASSWORD);
$pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_OBJ);

if (isset($_REQUEST['firebaseID'], $_REQUEST['data'])){
    $firebaseID = $_REQUEST['firebaseID'];
    $data = $_REQUEST['data'];
    try
    {
        $exec = $pdo->prepare("INSERT INTO Users (firebaseID,data) VALUES ('$firebaseID','$data')");
        $exec->execute();

        $sql="SELECT * FROM Users";
        $result = $pdo->query($sql);

        $response = array();
        $posts = array();

        foreach ($result as $user){
            $posts[] = array('firebaseID' => $user->firebaseID, 'data' => $user->data);
        }

        $response['Users'] = $posts;

        $fname = "Users.json";
        $fhandle = fopen($fname,"r");
        $content = fread($fhandle,filesize($fname));

        $fhandle = fopen($fname,"w");
        fwrite($fhandle, json_encode($posts, JSON_PRETTY_PRINT));
        fflush($fhandle);
        fclose($fhandle);

    }
    catch (PDOException $e)
    {
        die($e->getMessage());
    }
}

?>