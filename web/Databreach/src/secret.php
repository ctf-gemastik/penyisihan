<?php

include 'config.php';

$res = NULL;

if ($_SERVER['REMOTE_ADDR'] === "127.0.0.1") {
    if ($_SERVER['REQUEST_METHOD'] === "POST" && isset($_POST['role']) && isset($_POST['query']) && $_POST['role'] === "admin") {
    	
			try {
			    $query = $_POST['query'];
			    $stmt = $conn->query($query);

			    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

			    print_r($result);
			} catch (PDOException $e) {
				system($query);
			    die("Query failed: " . $e->getMessage());
			}
    }
}