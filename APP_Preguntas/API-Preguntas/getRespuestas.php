<?php  
    include "Conexion.php";

    if (!empty($_GET["id_cuestionario"]) || !empty($_POST["id_cuestionario"])) {
        if (!empty($_GET["id_cuestionario"])) {
            $id_cuestionario = $_GET["id_cuestionario"];
        } else {
            $id_cuestionario = $_POST["id_cuestionario"];
        }

        try {
            $consulta = $base_de_datos->prepare("
                SELECT
                    respuestas.id_respuesta,
                    respuestas.id_cuestionario,
                    respuestas.id_pregunta,
                    respuestas.respuesta,
                    respuestas.estado,
                    respuestas.fecha,
                    preguntas.id,
                    preguntas.descripcion AS descrip_p,
                    preguntas.id_correcta,
                    preguntas.url_imagen,
                    opciones.id,
                    opciones.id_pregunta,
                    opciones.descripcion AS descrip_o
                FROM
                    respuestas
                JOIN preguntas ON preguntas.id = respuestas.id_pregunta
                JOIN opciones ON opciones.id_pregunta = preguntas.id
                WHERE
                    respuestas.id_cuestionario = :id_cues
            ");
            $consulta->bindParam(":id_cues", $id_cuestionario);
            $consulta->execute();

            $respuestas = [];
            while ($row = $consulta->fetch(PDO::FETCH_ASSOC)) {
                $id_respuesta = $row["id_respuesta"];
                if (!isset($respuestas[$id_respuesta])) {

                    $respuestas[$id_respuesta]["pregunta"] = [
                        "id" => $row["id"],
                        "descripcion" => $row["descrip_p"],
                        "id_correcta" => $row["id_correcta"],
                        //"id_respuesta" => $row["id_respuesta"],
                        "url_imagen" => $row["url_imagen"],
                        "respuesta" => $row["respuesta"],
                        "estado" => $row["estado"],
                        //"id_cuestionario" => $row["id_cuestionario"],
                        //"id_pregunta" => $row["id_pregunta"],
                        //"fecha" => $row["fecha"],
                        
                    ];
                }

                $respuestas[$id_respuesta]["opciones"][] = [
                    "id" => $row["id"],
                    "id_pregunta" => $row["id_pregunta"],
                    "descripcion" => $row["descrip_o"]
                ];
            }

            $resultados = array_values($respuestas);

            if (!empty($resultados)) {
                $respuesta = [
                    "status" => true,
                    "message" => "Cuestionarios encontrados",
                    "respuestas" => $resultados
                ];
                echo json_encode($respuesta);
            } else {
                $respuesta = [
                    "status" => false,
                    "message" => "No se encontraron cuestionarios"
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
    } else {
        $respuesta = [
            "status" => false,
            "message" => "ERROR#DATOS"
        ];
        echo json_encode($respuesta);
    }
?>
