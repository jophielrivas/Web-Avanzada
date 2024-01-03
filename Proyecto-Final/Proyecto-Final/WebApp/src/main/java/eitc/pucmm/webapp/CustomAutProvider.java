package eitc.pucmm.webapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.*;

@Component
public class CustomAutProvider implements AuthenticationProvider{
    
    //Si no funciona, probar haciendo la peticion fuera y pasandolo a autheticate
    @Override
    public Authentication authenticate(Authentication auth){

        Usuario usuario = new Usuario();
        System.out.println(auth.getName());
        System.out.println(auth.getCredentials().toString());
        usuario.setCorreo(auth.getName());
        usuario.setPassword(auth.getCredentials().toString());

        URL url;
        try {
            url = new URL("http://user-microservice:8080/usuarios/aut");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = new Gson().toJson(usuario).getBytes("utf-8");
                os.write(input, 0, input.length);			
            } 

            try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                    System.out.println(response.toString());
                }

                usuario = new Gson().fromJson(response.toString(), Usuario.class);
                final List<GrantedAuthority> grantedAuths = new ArrayList<>();
                grantedAuths.add(new SimpleGrantedAuthority(usuario.getPermiso()));
                return new UsernamePasswordAuthenticationToken(usuario.getCorreo(), usuario.getPassword(), grantedAuths);
            

          }   
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    
        return null;
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }

    private class Usuario{
        String correo;
        String password;
        String permiso;

        public String getPermiso(){
            return this.permiso;
        }
        
        public String getCorreo(){
            return this.correo;
        }
        
        public String getPassword(){
            return this.password;
        }
        public void setCorreo(String correo){
            this.correo = correo;
        }
        
        public void setPassword(String password){
            this.password = password;
        }
    }
}

