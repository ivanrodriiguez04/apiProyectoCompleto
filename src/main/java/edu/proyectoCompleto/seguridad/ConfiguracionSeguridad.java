package edu.proyectoCompleto.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import edu.proyectoCompleto.servicios.UsuarioServicio;

@Configuration
public class ConfiguracionSeguridad {
    private final UsuarioServicio usuarioServicio;

    // Constructor para inyectar el servicio de usuario
    public ConfiguracionSeguridad(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Desactivar CSRF si es necesario (por ejemplo, para APIs REST o si trabajas con formularios)
        http.csrf().disable()
            .authorizeRequests()
                .anyRequest().permitAll() // Permitir acceso sin autenticación a todas las rutas
            .and()
            .formLogin().disable()  // Desactivar el formulario de inicio de sesión
            .httpBasic().disable(); // Desactivar la autenticación básica

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(usuarioServicio);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
