package Services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import encapsulaciones.Reserva;

import com.amazonaws.services.lambda.runtime.Context;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReservaDynamoServices {
    public listaReservasResponse listarReservarActivas(FiltroListaReserva filtro, Context context, int condicion){
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
        List<Reserva> reservas = new ArrayList<>();
        Date aux = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String fechaActual = format.format(aux);
        System.out.println(fechaActual);
        ScanRequest scanRequest = null;

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":fechaActual", new AttributeValue().withS(fechaActual));

        if(condicion == 0){
            scanRequest = new ScanRequest().withTableName("Reservas")
                    .withFilterExpression("fecha >= :fechaActual")
                    .withExpressionAttributeValues(expressionAttributeValues);
        }else {

            scanRequest = new ScanRequest().withTableName("Reservas")
                    .withFilterExpression("fecha < :fechaActual")
                    .withExpressionAttributeValues(expressionAttributeValues);

        }
        ScanResult result = null;

        do{
            if(result != null){
                scanRequest.setExclusiveStartKey(result.getLastEvaluatedKey());
            }
            result = dynamoDB.scan(scanRequest);
            List<Map<String, AttributeValue>> rows = result.getItems();

            for (Map<String, AttributeValue> mapReservas : rows) {
                System.out.println(""+mapReservas);

                AttributeValue matriculaAtributo = mapReservas.get("matricula");
                AttributeValue nombreAtributo = mapReservas.get("nombre");
                AttributeValue carreraAtributo = mapReservas.get("carrera");
                AttributeValue laboratorioAtributo = mapReservas.get("laboratorio");
                AttributeValue fechaAtributo = mapReservas.get("fecha");

                Reserva tmp = new Reserva();
                tmp.setMatricula(matriculaAtributo.getS());
                tmp.setNombre(nombreAtributo.getS());
                tmp.setCarrera(carreraAtributo.getS());
                tmp.setLaboratorio(laboratorioAtributo.getS());
                tmp.setFecha(fechaAtributo.getS());

                reservas.add(tmp);
            }

        } while (result.getLastEvaluatedKey() != null);

        return new listaReservasResponse(false, "", reservas);
    }
    public reservaResponse insertarReserva(Reserva reserva, Context context){
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
        if(reserva.getMatricula().isEmpty() || reserva.getNombre().isEmpty()){
            throw new RuntimeException("Datos no validos");
        }
        try {
            DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);
            mapper.save(reserva);
        }catch (Exception e){
            return new reservaResponse(true, e.getMessage(),null);
        }
        return new reservaResponse(false, "",reserva);

    }
    public class reservaResponse {
        boolean error;
        String errorMessage;
        Reserva reserva;
        public reservaResponse(boolean error, String errorMessage, Reserva reserva) {
            this.error = error;
            this.errorMessage = errorMessage;
            this.reserva = reserva;
        }

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Reserva getReserva() {
            return reserva;
        }

        public void setReserva(Reserva reserva) {
            this.reserva = reserva;
        }
    }
    public class listaReservasResponse {
        boolean error;
        String errorMessage;
        List<Reserva> reservas;
        public listaReservasResponse(boolean error, String errorMessage, List<Reserva> reservas) {
            this.error = error;
            this.errorMessage = errorMessage;
            this.reservas = reservas;
        }

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public List<Reserva> getReservas() {
            return reservas;
        }

        public void setReservas(List<Reserva> reservas) {
            this.reservas = reservas;
        }
    }
    public class FiltroListaReserva {
        String filtro;

        public String getFiltro() {
            return filtro;
        }

        public void setFiltro(String filtro) {
            this.filtro = filtro;
        }
    }
}



