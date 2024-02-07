<?php  
    include "Conexion.php";
    session_start();

    if ( !empty($_GET["id_cuestionario"]) || !empty($_POST["id_cuestionario"]) ){

        
        if (!empty($_GET["id_cuestionario"]) ) {
            $id_cuestionario = $_GET["id_cuestionario"];
        }else {
            $id_cuestionario = $_POST["id_cuestionario"];
        }

        if (!isset($_SESSION['preguntas_realizadas'][$id_cuestionario] )) {
            $_SESSION['preguntas_realizadas'][$id_cuestionario] = [];
        }

        if (count($_SESSION['preguntas_realizadas'][$id_cuestionario]) < 10) {
            $id_pregunta = rand(1, 10);
            
            while (in_array($id_pregunta, $_SESSION['preguntas_realizadas'][$id_cuestionario])) {
                $id_pregunta = rand(1, 10);
            }
            
            $_SESSION['preguntas_realizadas'][$id_cuestionario][] = $id_pregunta;
            
            try{
    
                // OBTENER PREGUNTA
                $consulta = $base_de_datos->prepare("SELECT * FROM preguntas WHERE id = :id_pre");
                $consulta->bindParam(":id_pre",$id_pregunta);
                $consulta->execute();
                $preguntas = $consulta->fetchAll(PDO::FETCH_ASSOC);
    
                //OBTENER OPCIONES DE LA PREGUNTA
                $consulta_opciones = $base_de_datos->prepare("SELECT * FROM opciones WHERE id_pregunta = :id_pre");
                $consulta_opciones->bindParam(":id_pre",$id_pregunta);
                $consulta_opciones->execute();
                $opciones = $consulta_opciones->fetchAll(PDO::FETCH_ASSOC);

                if ( $preguntas) {
                    $respuesta = [
                        "status" => true,
                        "pregunta" => $preguntas,
                        "opciones" => $opciones
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
                "message" => "Ya se han realizado 10 preguntas."
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