<?php  
    include "Conexion.php";
    if ( !empty($_POST["id_cuestionario"]) && !empty($_POST["id_pregunta"]) && !empty($_POST["respuesta"]) && !empty($_POST["estado"])  ){
        
        $id_cuestionario = $_POST["id_cuestionario"];
        $id_pregunta = $_POST["id_pregunta"];
        $respuesta = $_POST["respuesta"];
        $estado = $_POST["estado"];
       
        try{
            $consulta = $base_de_datos->prepare("INSERT INTO respuestas (id_cuestionario,id_pregunta,respuesta,estado) VALUES ( :id_cues, :id_pre,:res,:esta)");
            $consulta->bindParam(":id_cues",$id_cuestionario);
            $consulta->bindParam(":id_pre",$id_pregunta);
            $consulta->bindParam(":res",$respuesta);
            $consulta->bindParam(":esta",$estado);
            $consulta->execute();

            // Obtener el ID del cuestionario recién insertado
            $id_respuesta = $base_de_datos->lastInsertId();

            if ( $id_respuesta ) {
                $respuesta = [
                    "status" => true,
                    "message" => "OK#RESPONSE#INSERT",
                    "id_respuesta" => $id_respuesta
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