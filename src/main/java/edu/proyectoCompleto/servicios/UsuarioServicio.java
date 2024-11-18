package edu.proyectoCompleto.servicios;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.proyectoCompleto.daos.Usuario;
import edu.proyectoCompleto.repositorios.UsuarioRepositorios;

@Service
public class UsuarioServicio implements UserDetailsService {

    private final UsuarioRepositorios usuarioRepository;

    public UsuarioServicio(UsuarioRepositorios usuarioRepositorios) {
        this.usuarioRepository = usuarioRepositorios;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar al usuario por correo electrónico
        Usuario usuario = usuarioRepository.findByEmailUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el correo: " + username));

        // Verificar que el usuario tiene un rol asignado
        String rol = usuario.getRol();
        if (rol == null) {
            throw new UsernameNotFoundException("El usuario no tiene un rol asignado.");
        }

        // Usar el rol con el prefijo 'ROLE_'
        return User.builder()
                .username(usuario.getEmailUsuario())  // Usamos el correo del usuario
                .password(usuario.getPasswdUsuario())  // Contraseña cifrada
                .authorities("ROLE_" + rol)  // Asignar roles
                .build();
    }
}
