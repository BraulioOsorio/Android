<?php  
    include "Conexion.php";
    if ( !empty($_GET["id_usuario"]) || !empty($_POST["id_usuario"]) ){
        

        if (!empty($_GET["id_usuario"]) ) {
            $id_usuario = $_GET["id_usuario"];
        }else {
            $id_usuario = $_POST["id_usuario"];
        }

        try{
            $consulta = $base_de_datos->prepare("SELECT * FROM cuestionarios WHERE id_usuario = :id_user  ORDER BY fecha_inicio DESC");
            $consulta->bindParam(":id_user",$id_usuario);
            $consulta->execute();

            $cuestions = $consulta->fetchAll(PDO::FETCH_ASSOC);

            if ( $cuestions) {
                $respuesta = [
                    "status" => true,
                    "message" => "Cuestionarios encontrados",
                    "resumen" => $cuestions
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