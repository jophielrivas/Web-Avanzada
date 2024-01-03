package eitc.pucmm.mailmicroservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@RestController
public class MailApiController {

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;


    public void main(){
        enviarCorreo("", "", "");
    }

    //Funciona
    @PostMapping("/notificacion/registro")
    public boolean notificarRegistro(@RequestBody String body){
        JsonObject json = new Gson().fromJson(body, JsonObject.class);
        String correo = json.get("correo").getAsString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> model;
        try {
            model = mapper.readValue(body, Map.class);
            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(model);
            String html = thymeleafTemplateEngine.process("confirmacionRegistro.html", thymeleafContext);
            System.out.println(html);
            enviarCorreo(correo, "Confirmacion de registro - PUCMM Eventos", html);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
        
    }

    @PostMapping("/notificacion/compra")
    public boolean notificarCompra(@RequestBody String body) throws IOException{
        JsonObject json = new Gson().fromJson(body, JsonObject.class);
        
        notificarEmpleados(json.get("servicios").getAsString());
        String correo = json.get("correo").getAsString();
        String mail = definirHtml(json.get("servicios").getAsString(),"facturaCliente.html");
        return enviarCorreo(correo , "Notificacion de compra", mail);
    }

    public void notificarEmpleados(String body) throws IOException {
        String mail =  definirHtml(body,"notificacionEmpleados.html");

        URL url = new URL("http://USER-MICROSERVICE:8080/usuarios/tipo/EMPLEADO");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        InputStream response = con.getInputStream();
        try (Scanner scanner = new Scanner(response)) {
            String aux = scanner.useDelimiter("\\A").next();
            if(!aux.equalsIgnoreCase("[]")){
            aux = aux.substring(1,aux.length()-1);
            List<String> myList = new ArrayList<String>(Arrays.asList(aux.split(",")));
            for (String string : myList) {

                System.out.println(string);
                    enviarCorreo(string.substring(1,string.length()-1), "Un cliente ha adquirido nuestros servicios", mail);
            }
        }
        }
    }

    private String definirHtml(String body, String template) {
        int[] carrito = {0,0,0,0};

        String aux = body.substring(1,body.length()-1);
        aux = aux.replaceAll("\\s+","");
        System.out.println(aux);
        List<String> myList = new ArrayList<String>(Arrays.asList(aux.split(",")));
        for (int i = 0; i < 4; i++) {
            carrito[i] = Integer.parseInt(myList.get(i));
        }

        String[] productos = {"Pre-Boda","Boda","Cumpleaños","Vide de Evento"};
        int[] precio = {1000,5000,3000,4000};
        int total = 0;
        Evento evento;
        List<Evento> eventos = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            if(carrito[i] != 0 ){
                evento = new Evento(productos[i], precio[i], carrito[i]);
                eventos.add(evento);
                total += precio[i]*carrito[i];
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("servicios", eventos);
        model.put("total", total);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(model);
        String html = thymeleafTemplateEngine.process(template, thymeleafContext);
        return html;

    }
    

    public boolean enviarCorreo(String destinatario,String subject,String html){
        try {
            Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.sendgrid.net", 587, "apikey", "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")
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withSessionTimeout(10 * 1000)
                .clearEmailAddressCriteria() // turns off email validation
                .withDebugLogging(true)
                .buildMailer();
            
            Email email = EmailBuilder.startingBlank()
                    .from("noreply@fguzman.codes")
                    .to(destinatario)
                    .withReplyTo("Soporte", "soporte@fguzman.codes")
                    .withSubject(subject)
                    .withHTMLText(html)
                    .withPlainText("No visualiza la información en formato html")

                    .withReturnReceiptTo()
                    .withBounceTo("bounce@fguzman.codes")
                    .buildEmail();

            //Enviando el mensaje:
            mailer.sendMail(email);
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
