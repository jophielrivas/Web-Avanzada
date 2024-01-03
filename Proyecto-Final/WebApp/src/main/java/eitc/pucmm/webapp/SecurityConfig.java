package eitc.pucmm.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    @Autowired
    CustomAutProvider customAutProvider;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAutProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()            
            .antMatchers("/registro").permitAll()
            .antMatchers("/addServicio","/carrito","/realizarCompra").hasAnyAuthority("CLIENTE","EMPLEADO")//cualquier llamada debe ser validada
            .antMatchers("/estadistica","/historial","/market","/").hasAnyAuthority("ADMIN","CLIENTE","EMPLEADO")
            .antMatchers("/usuarios/**").hasAnyAuthority("ADMIN")
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .failureUrl("/login")
            .permitAll()
            .and()
            .logout()
            .permitAll();

            http.csrf().disable();
            http.headers().frameOptions().disable();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
