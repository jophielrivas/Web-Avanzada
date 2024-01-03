package eitc.pucmm.webapp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import eitc.pucmm.webapp.entidades.Compra;
import eitc.pucmm.webapp.entidades.Evento;
import eitc.pucmm.webapp.entidades.Usuario;

@Controller
public class ApiController {

    //Carrito
    int[] carrito = {0,0,0,0};
    String[] productos = {"Pre-Boda","Boda","Cumpleaños","Vide de Evento"};
    int[] precio = {1000,5000,3000,4000};

    @RequestMapping("/")
    public String inicio(Model model) throws IOException{
        Usuario user = buscarUsuarioActivo();
        model.addAttribute("user", user);
        return "market";
    }

    @RequestMapping("/login")
    public String login(Model model){
        return "login";
    }

    @RequestMapping(value = "/registro")
    public String registro(){
        return "registro";
    }

    @RequestMapping(value = "/registro", method = RequestMethod.POST)
    public String registrarUsuario(WebRequest request,Model model) throws IOException{
        String correo = request.getParameter("correo");
        String nombre = request.getParameter("nombre");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");
        if( correo.equalsIgnoreCase("") || nombre.equalsIgnoreCase("") || telefono.equalsIgnoreCase("") || direccion.equalsIgnoreCase("")){

            model.addAttribute("error","Algunos campos se encuentran vacios!");
            return "registro";
        }else{
            if(validar(correo,"correo")){
                if( validar(telefono,"telefono")){
                Usuario aux = new Usuario(correo,nombre,telefono,direccion);
                String msResponse = makeRequest("POST",aux,"http://USER-MICROSERVICE:8080/usuarios");
                if(msResponse != null){
                    return "login";
                }
            }
            }
        }   
        model.addAttribute("error", "Error! Por favor intentelo mas tarde");

        return "registro";
    }

    @RequestMapping("/market")
    public String market(Model model) throws IOException{
        Usuario user = buscarUsuarioActivo();
        model.addAttribute("user", user);
        return "market";
    }


    @RequestMapping(value = "/addServicio", method = RequestMethod.POST )
    public String addToCarrito(WebRequest request, Model model) throws IOException{
        Usuario user;
        user = buscarUsuarioActivo();
        model.addAttribute("user", user);
        carrito[Integer.valueOf(request.getParameter("producto"))]++;
        return "market";
    }

    @RequestMapping("/delServicio")
    public String delFromCarrito(WebRequest request){
        if(carrito[Integer.valueOf(request.getParameter("producto"))] > 0){
            carrito[Integer.valueOf(request.getParameter("producto"))]--;
        }
        return "redirect:/carrito";
    }

    @RequestMapping("/carrito")
    public String cargarCarrito(Model model) throws IOException{
        Usuario user = buscarUsuarioActivo();
        model.addAttribute("user", user);
        
        Evento aux;
        int total = 0;
        List<Evento> eventosAux = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            if(carrito[i] != 0 ){
                aux = new Evento(productos[i], precio[i], carrito[i]);
                eventosAux.add(aux);
                total += precio[i]*carrito[i];
                aux.setNumAux(i);
            }    
        }
        model.addAttribute("servicios", eventosAux);
        model.addAttribute("total", total);
        return "cart";
        }

        

    //compras busca por id, no por correo
    //Convertir json a arreglo de usuarios

    @RequestMapping("/historial")
    public String getHistorial(Model model) throws IOException{
        Usuario user = buscarUsuarioActivo();
        model.addAttribute("user", user);
        
        String response = makeRequest("GET", "", "http://EVENTS-MICROSERVICE:8080/compras/"+user.getId());
        JsonArray object = new Gson().fromJson(response, JsonArray.class);
        List<Compra> eventos = new ArrayList<>();
        if(object != null){
            for(int i = 0; i < object.size();i++){
                Compra compra = new Gson().fromJson(object.get(i), Compra.class);
                compra.setFecha(compra.getFecha().substring(0, 10));
                eventos.add(compra);
            }
        }

        eventos = buscarEventos(eventos);

        model.addAttribute("compras", eventos);

        return "historial";
    }


    @RequestMapping("/procesarCompra")
    public String procesarCompra(Model model) throws IOException{
        Usuario user = buscarUsuarioActivo();
        model.addAttribute("user", user);
        
        Evento aux;
        int total = 0;
        List<Evento> eventosAux = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            if(carrito[i] != 0 ){
                aux = new Evento(productos[i], precio[i], carrito[i]);
                eventosAux.add(aux);
                total += precio[i]*carrito[i];
                aux.setNumAux(i);
            }
        }
        String response = makeRequest("POST", carrito, "http://EVENTS-MICROSERVICE:8080/compra/"+user.getId());

        JsonObject json = new JsonObject();
        json.addProperty("correo", user.getCorreo());
        json.addProperty("servicios",Arrays.toString(carrito));
        makeRequest("POST",json, "http://MAIL-MICROSERVICE:8080/notificacion/compra");

        carrito = new int[] {0, 0, 0,0};
        model.addAttribute("servicios", eventosAux);
        model.addAttribute("total", total);
        return "resumen";
        
    }

    //Convertir json a arreglo de usuarios
    @RequestMapping("/usuarios/{tipo}")
    public String verUsuarios(Model model,@PathVariable String tipo) throws IOException{
        Usuario user = buscarUsuarioActivo();
        model.addAttribute("user", user);
        System.out.println("===============TIPO================");
        System.out.println(tipo);
        String response = makeRequest("GET", tipo, "http://USER-MICROSERVICE:8080/usuarios/lista/"+tipo);
        JsonArray object = new Gson().fromJson(response, JsonArray.class);
        List<Usuario> usuarios = new ArrayList<>();
        if(object != null){
            for(int i = 0; i < object.size();i++){
                usuarios.add(new Gson().fromJson(object.get(i), Usuario.class));
            }
        }

        model.addAttribute("usuarios", usuarios);

        return "listaUsuarios";
    }

    @RequestMapping("/usuarios/ver/{id}")
    private String verUsuario(@PathVariable String id, Model model) throws IOException{
        Usuario user = buscarUsuarioActivo();
        model.addAttribute("user", user);

        String response = makeRequest("GET", id, "http://USER-MICROSERVICE:8080/usuarios/"+id);
        Usuario aux = new Gson().fromJson(response, Usuario.class);
        model.addAttribute("usuario", aux);
        //agregar historial de usuario
        return "usuario";
    }

    @RequestMapping(value = "/modificar", method = RequestMethod.POST)
    public String moddificarUsuario(WebRequest request, Model model) throws IOException{
    
        String correo = request.getParameter("correo");
        String nombre = request.getParameter("nombre");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");
        String id = request.getParameter("id");
        String permiso = request.getParameter("permiso");
        
        Usuario aux = new Usuario(correo,nombre,telefono,direccion);
        aux.setPermiso(permiso);
        aux.setId(Integer.parseInt(id));
        String response = makeRequest("POST",aux, "http://USER-MICROSERVICE:8080/modificar/usuarios");

        return "redirect:/usuarios/todos";
    }

    @RequestMapping("/estadisticas/{filtro}")
    public String estadisticas(@PathVariable String filtro, Model model) throws IOException{
        Usuario user = buscarUsuarioActivo();
        model.addAttribute("user", user);
        
        String response = makeRequest("GET", "", "http://EVENTS-MICROSERVICE:8080/compras/general/"+filtro);
        JsonArray object = new Gson().fromJson(response, JsonArray.class);
        List<Compra> eventos = new ArrayList<>();
        if(object != null){
            for(int i = 0; i < object.size();i++){
                Compra compra = new Gson().fromJson(object.get(i), Compra.class);
                compra.setFecha(compra.getFecha().substring(0, 10));
                eventos.add(compra);
            }
        }

        eventos = buscarEventos(eventos);
        String datos = generarGrafico(eventos);
        model.addAttribute("estats", datos);
        model.addAttribute("compras", eventos);
        return "estadisticas";
    }

    private String generarGrafico(List<Compra> compras) {

        Map<String, Integer> mapa = new HashMap<>();
        for (Compra aux : compras) {
            for (Evento evento : aux.getEventos()) {
                
                if(mapa.containsKey(evento.getNombre())){
                    int valAux = mapa.get(evento.getNombre());
                    mapa.put(evento.getNombre(), evento.getCantidad() + valAux);
                }else {
                    mapa.put(evento.getNombre(), evento.getCantidad());
                }
                System.out.println(evento.getNombre()+mapa.get(evento.getNombre()));
            }
        }
        System.out.println(mapa.toString());
        return mapa.toString();
    }

    private boolean validar(String str,String tipo) {
        Pattern pattern;
        if(tipo.equalsIgnoreCase("correo")){
            pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        }else{
            pattern = Pattern.compile("^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$");
        }

        Matcher mat = pattern.matcher(str);
        return mat.matches();
    }

    private String makeRequest(String metodo, Object requestBody,String URL) throws IOException {
        
        URL url = new URL(URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(metodo);
        if(metodo.equalsIgnoreCase("POST")){
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = new Gson().toJson(requestBody).getBytes("utf-8");
                os.write(input, 0, input.length);			
            }
            try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
                  StringBuilder response = new StringBuilder();
                  String responseLine = null;
                  while ((responseLine = br.readLine()) != null) {
                      response.append(responseLine.trim());
                  }
                  return response.toString();
              }   
        }else{
            con.setRequestMethod(metodo);
            InputStream response = con.getInputStream();
            try (Scanner scanner = new Scanner(response)) {
                return scanner.useDelimiter("\\A").next();
            }
            
        }
       
    }

    private Usuario buscarUsuarioActivo() throws IOException {
        
        String correo = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString());
        String response = makeRequest("GET","", "http://USER-MICROSERVICE:8080/usuarios/correo/"+correo);

        return new Gson().fromJson(response, Usuario.class);
        
    }

    private List<Compra> buscarEventos(List<Compra> compras) throws IOException {

        for (Compra compra : compras) {
            String response = makeRequest("GET", "", "http://EVENTS-MICROSERVICE:8080/compra/eventos/"+compra.getIdCompra());
            JsonArray object = new Gson().fromJson(response, JsonArray.class);
            List<Evento> eventos = new ArrayList<>();

            if(object != null){
                for(int i = 0; i < object.size();i++){
                    Evento evento = new Gson().fromJson(object.get(i), Evento.class);
                    eventos.add(evento);
                }
                compra.setEventos(eventos);
            }
        }

        return compras;
    }
}
