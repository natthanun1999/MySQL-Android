<?php
    $servername = "localhost";
    $username = "root";
    $password = "123456789";
    $dbname = "mobile";

    $conn = new mysqli($servername , $username , $password , $dbname);

    if($conn->connect_error){
        die("Connection failed: " .$conn->connect_error);
    }

    $sql = "SELECT * FROM `member`";

    $result = $conn->query($sql);

    if($result !== false) {
        while($row = mysqli_fetch_assoc($result)) {
            $output[] = $row;
        }
        mysqli_free_result($result);
    }
    else {
        echo "Error cannot query: " .$conn->error;
    }
	
	echo json_encode($output);
	
    $conn->close();
?>