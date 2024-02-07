<?php  
    include "Conexion.php";
    if ( !empty($_POST["id_usuario"]) || !empty($_GET["id_usuario"]) ){
        

        if (!empty($_GET["id_usuario"]) ) {
            $id_usuario = $_GET["id_usuario"];
        }else {
            $id_usuario = $_POST["id_usuario"];
        }

        try{
            $consulta = $base_de_datos->prepare("INSERT INTO cuestionarios (id_usuario) VALUES (:id_user)");
            $consulta->bindParam(":id_user",$id_usuario);
            $consulta->execute();

            // Obtener el ID del cuestionario recién insertado
            $id_cuestionario = $base_de_datos->lastInsertId();

            if ( $id_cuestionario ) {
                $respuesta = [
                    "status" => true,
                    "message" => "OK#CUESTIONARIO#INSERT",
                    "id_cuestionario" => $id_cuestionario
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