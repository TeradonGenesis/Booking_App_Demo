<?php


////Axios attempt
//header("Access-Control-Allow-Origin: *");
//header("Access-Control-Allow-Methods: GET, POST");
//
//if(isset($_POST) && !empty($_POST)){
//    $data = json_decode(file_get_contents("php://input"), true);
//    $id = $data['id'];
//    $conn=mysqli_connect("localhost","root","","jwt_test");
//    $stmt = $conn->prepare("DELETE FROM user WHERE u_id=?");
//    $stmt->bind_param('s',$id);
//    $stmt->execute();
//    $conn->close();
//};


//Axios attempt
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST");

if(isset($_POST) && !empty($_POST)){
    $data = json_decode(file_get_contents("php://input"), true);
    $id = $data['id'];
    $conn=mysqli_connect("localhost","root","","api_keys");
    $stmt = $conn->prepare("DELETE FROM user WHERE user_id=?");
    $stmt->bind_param('s',$id);
    $stmt->execute();
    $conn->close();
};

?>
