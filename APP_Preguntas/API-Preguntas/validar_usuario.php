<?php  
    include "Conexion.php";
    if ( !empty($_POST["correo"]) && !empty($_POST["password"]) || !empty($_GET["correo"]) || !empty($_GET["password"]) ){
        
        $correo = (!empty($_POST["correo"])) ? $_POST["correo"] : $_GET["correo"];
        $password = (!empty($_POST["password"])) ? md5($_POST["password"]) : md5($_GET["password"]);

        
        

        try{
            $consulta = $base_de_datos->prepare("SELECT id_usuario,nombres FROM usuarios WHERE correo = :cor AND password = :pass");
            $consulta->bindParam(":cor",$correo);
            $consulta->bindParam(":pass",$password);
            $consulta->execute();

            $datosUsuario = $consulta->fetchAll(PDO::FETCH_ASSOC);

            if ( $datosUsuario) {
                $respuesta = [
                    "status" => true,
                    "message" => "Usuario encontrado",
                    "usuario" => $datosUsuario[0]
                ];
                echo json_encode($respuesta);
            }else {
                $respuesta = [
                    "status" => false,
                    "message" => "ERROR#PROCESS"
                ];
                echo json_encode($respuesta);
            }

        } catch (Exception $e) {
            $respuesta = [
                "status" => false,
                "message" => "ERROR#SQL#$e"
            ];
            echo json_encode($respuesta);
        }
    }else {
        $respuesta = [
            "status" => false,
            "message" => "ERROR#DATOS"
        ];
        echo json_encode($respuesta);
    }


?>