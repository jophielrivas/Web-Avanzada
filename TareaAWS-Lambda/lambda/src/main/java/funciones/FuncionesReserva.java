package funciones;

import Services.ReservaDynamoServices;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import encapsulaciones.Reserva;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class FuncionesReserva implements RequestStreamHandler {
    private ReservaDynamoServices dynamoServices = new ReservaDynamoServices();
    private Gson gson = new Gson();

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        JSONParser jsonParser = new JSONParser();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
        String requestBody = null;
        JSONObject responseJson = new JSONObject();
        String response = "";

        try {
            JSONObject event = (JSONObject) jsonParser.parse(bufferedReader);
            context.getLogger().log(""+event.toJSONString());

            if(event.get("requestContext")==null){
                throw new IllegalArgumentException(("No se respesta el API de entrada"));
            }
            String httpMethod = ((JSONObject)((JSONObject)event.get("requestContext")).get("http")).get("method").toString();
            ReservaDynamoServices.listaReservasResponse listaReservasResponse;
            switch (httpMethod){
                case "GET":
                    listaReservasResponse = dynamoServices.listarReservarActivas(null, context,0);
                    response = gson.toJson(listaReservasResponse);
                    break;
                case "POST":
                    listaReservasResponse = dynamoServices.listarReservarActivas(null, context,1);
                    response = gson.toJson(listaReservasResponse);
                    break;
                case "PUT":
                    Reserva reserva = getReservaBodyJson(event);
                    response = gson.toJson(dynamoServices.insertarReserva(reserva, context));
                    break;
            }
            if(event.get("body")!= null){
                requestBody = event.get("body").toString();
            }
            JSONObject responseBody = new JSONObject();
            responseBody.put("data",jsonParser.parse(response));
            responseBody.put("entrada",requestBody);

            JSONObject headerJson = new JSONObject();
            headerJson.put("Content-Type", "application/json");

            responseJson.put("statusCode",200);
            responseJson.put("headers",headerJson);
            responseJson.put("body", responseBody.toString());

        } catch (ParseException e) {
            responseJson.put("statusCode", 400);
            responseJson.put("exception", e);
        }
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(responseJson.toString());
        writer.close();
    }

    private Reserva getReservaBodyJson(JSONObject event) {
        if(event.get("body")==null){
            throw new IllegalArgumentException("No envio el cuerpo en la trama.");
        }
        Reserva reserva = gson.fromJson(event.get("body").toString(), Reserva.class);
        return reserva;
    }
}
