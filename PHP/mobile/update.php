<?php
    $servername = "localhost";
    $username = "root";
    $password = "123456789";
    $dbname = "mobile";

    $conn = new mysqli($servername , $username , $password , $dbname);

    if($conn->connect_error) {
        die("Connection failed: " .$conn->connect_error);
    }
	
	if (isset($_POST['id']) && isset($_POST['username']) && isset($_POST['password'])) {
		$id = $_POST['id'];
		$u = $_POST['username'];
		$p = $_POST['password'];
		
		$sql = "UPDATE `member` SET username = \"$u\", pass = \"$p\" WHERE Id = \"$id\"";

		$result = $conn->query($sql);
		
		if ($result) {
			echo "Update Success!";
		} else {
			echo "Something wrong!";
		}
	}
	
    $conn->close();
?>