<?php
    $servername = "localhost";
    $username = "root";
    $password = "123456789";
    $dbname = "mobile";

    $conn = new mysqli($servername , $username , $password , $dbname);

    if($conn->connect_error) {
        die("Connection failed: " .$conn->connect_error);
    }
	
	if (isset($_POST['username']) && isset($_POST['password'])) {
		$u = $_POST['username'];
		$p = $_POST['password'];
		
		$sql = "INSERT INTO `member` (`username`, `pass`) VALUES (\"$u\", \"$p\")";

		$result = $conn->query($sql);
		
		if ($result) {
			echo "Insert Success!";
		} else {
			echo "Something wrong!";
		}
	}
	
    $conn->close();
?>