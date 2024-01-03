package eitc.pucmm.usuariosmicroservice.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import eitc.pucmm.usuariosmicroservice.entidades.EnumPermiso;
import eitc.pucmm.usuariosmicroservice.entidades.Usuario;
import eitc.pucmm.usuariosmicroservice.repository.UserRepository;

@RestController
public class UserApiController {
    
    @Autowired
    UserRepository userRepository;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    //Funciona
    @GetMapping("/usuarios/lista/{permiso}")
    List<Usuario> getUsuarios(@PathVariable String permiso){
        if(permiso.equalsIgnoreCase("todos")){
            return userRepository.findAll();
        }
        return userRepository.findByPermiso(EnumPermiso.valueOf(permiso));
    }

    @GetMapping("/prueba")
    String prueba(){
        return "Hola";
    }

    //Funciona
    @PostMapping("/usuarios")
    Usuario registrUsuario(@RequestBody String usuario) throws IOException{
        Usuario aux =  new Gson().fromJson(usuario, Usuario.class);
        if(userRepository.findByCorreo(aux.getCorreo())!=null){
            return null;
        }
        
        aux.setPermiso(EnumPermiso.CLIENTE);
        aux.setPassword(aux.getNombre()+aux.getTelefono().substring(8, 11));

        URL url = new URL("http://MAIL-MICROSERVICE:8080/notificacion/registro");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = new Gson().toJson(aux).getBytes("utf-8");
            os.write(input, 0, input.length);			
        }
        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(con.getInputStream(), "utf-8"))) {
              StringBuilder response = new StringBuilder();
              String responseLine = null;
              while ((responseLine = br.readLine()) != null) {
                  response.append(responseLine.trim());
              }
              System.out.println(response.toString());
          }   
          aux.setPassword(encryp(aux.getPassword()));
          userRepository.save(aux);
          return aux;
    }

    @RequestMapping(value = "/modificar/usuarios", method = RequestMethod.POST)
    public boolean uptdateUsuario(@RequestBody String request){
        Usuario aux = new Gson().fromJson(request, Usuario.class);
        Usuario actual = userRepository.findByCorreo(aux.getCorreo());
        aux.setPassword(actual.getPassword()); 
        userRepository.save(aux);
        return true;
    }

    //Funciona
    @DeleteMapping("usuarios/{id}")
    public boolean deleteUsuario(@PathVariable String id){
        try {
            long aux = Long.parseLong(id);
            userRepository.deleteById(aux);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    //No funciona
    @GetMapping("/usuarios/{id}")
    public Usuario getUsuarioById(@PathVariable String id){
        long aux = Long.parseLong(id);
        return userRepository.findById(aux).orElse(null);
    }

    @GetMapping("/usuarios/correo/{correo}")
    public Usuario getUsuarioByCorreo(@PathVariable String correo){
        return userRepository.findByCorreo(correo);
    }

    //Funciona mas o menos
    @PostMapping("/usuarios/aut")
    public Usuario autentificacion(@RequestBody String loginData){
        JsonObject json = new Gson().fromJson(loginData, JsonObject.class);
        Usuario user = userRepository.findByCorreo(json.get("correo").getAsString());
        if (user != null){
            System.out.println(user.getCorreo());
            if(encoder.matches(json.get("password").getAsString(), user.getPassword())){
                return user;
            }
        }
        return null;
    }

    //Funciona
    @GetMapping("/usuarios/tipo/{permiso}")
    public List<String> getUsuariosByPermiso(@PathVariable String permiso){
        
        EnumPermiso enumPermiso = EnumPermiso.valueOf(permiso);
        return userRepository.findCorreoByPermiso(enumPermiso);
    }
    

    private String encryp(String string) {
        return encoder.encode(string);
    }

    @Bean
    public void createAdminUser(){
        if(userRepository.findByCorreo("admin@admin")!=null){
            return;
        }else{
            Usuario user = new Usuario();
            user.setId((long) 0);
            user.setCorreo("admin@admin");
            user.setDireccion("");
            user.setNombre("admin");
            user.setPassword(encryp("admin"));
            user.setPermiso(EnumPermiso.ADMIN);
            user.setTelefono("999-999-9999");

            userRepository.save(user);
        }
    }
}
