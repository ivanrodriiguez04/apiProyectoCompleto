package edu.proyectoCompleto.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.proyectoCompleto.daos.Usuario;
import edu.proyectoCompleto.dtos.UsuarioDto;
import edu.proyectoCompleto.repositorios.UsuarioRepositorios;
import edu.proyectoCompleto.utils.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

/**
 * Implementación del servicio de usuario que maneja las operaciones CRUD relacionadas con los usuarios.
 */
@Service
public class GestionUsuarioImplementacion implements GestionUsuarioInterfaz {

    /** Repositorio de Usuario que permite realizar operaciones de base de datos. */
    private final UsuarioRepositorios usuarioRepositorios;
    private final PasswordEncoder passwordEncoder; // Inyección de PasswordEncoder
    @Autowired
    private JwtUtil jwtUtil;  // Inyectamos el JwtUtil para generar el token


    /**
     * Constructor que inyecta el repositorio de Usuario.
     *
     * @param usuarioRepositorio repositorio de Usuario que se utiliza para acceder a la base de datos
     */
    @Autowired
    public GestionUsuarioImplementacion(UsuarioRepositorios usuarioRepositorio, PasswordEncoder passwordEncoder) {
        this.usuarioRepositorios = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Da de alta un nuevo usuario en la base de datos.
     * <p>
     * Convierte un objeto {@link UsuarioDto} a {@link Usuario} y lo guarda en la base de datos.
     * </p>
     *
     * @param usuarioDto datos del usuario que se desea dar de alta
     */
    @Override
    @Transactional
    public void altaUsuario(UsuarioDto usuarioDto) {
        // Convertir UsuarioDto a Usuario sin establecer idUsuario
        Usuario usuario = new Usuario();
        usuario.setNicknameUsuario(usuarioDto.getNicknameUsuario());
        usuario.setNombreUsuario(usuarioDto.getNombreUsuario());
        usuario.setDniUsuario(usuarioDto.getDniUsuario());
        usuario.setTelefonoUsuario(usuarioDto.getTelefonoUsuario());
        usuario.setFotoUsuario(usuarioDto.getFotoUsuario());
        usuario.setEmailUsuario(usuarioDto.getEmailUsuario());
        usuario.setPasswdUsuario(passwordEncoder.encode(usuarioDto.getPasswdUsuario()));  // Cifrar la contraseña
        usuario.setRol(usuarioDto.getRol());

        // Guardar el usuario en la base de datos
        usuarioRepositorios.save(usuario);
    }

    /**
     * Elimina un usuario existente de la base de datos usando el nickname del usuario.
     *
     * @param nicknameUsuario nickname del usuario que se desea eliminar
     */
    @Override
    @Transactional
    public boolean eliminarUsuario(String nicknameUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepositorios.findByNicknameUsuario(nicknameUsuario);

        if (usuarioOpt.isPresent()) {
            usuarioRepositorios.delete(usuarioOpt.get());
            return true;  // Retorna true si el usuario fue eliminado exitosamente
        }

        // Si el usuario no existe, retornar false
        return false;
    }


    /**
     * Modifica los campos nombre, teléfono e imagen de un usuario existente en la base de datos.
     *
     * @param idUsuario identificador del usuario a modificar
     * @param nuevoNombre nuevo nombre del usuario (opcional)
     * @param nuevoTelefono nuevo teléfono del usuario (opcional)
     * @param nuevaFoto nueva foto del usuario (opcional)
     * @return true si la modificación fue exitosa; de lo contrario, false
     */
    @Override
    @Transactional
    public boolean modificarUsuario(long idUsuario, String nuevoNombre, String nuevoTelefono, byte[] nuevaFoto) {
        Optional<Usuario> usuarioOpt = usuarioRepositorios.findById(idUsuario);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // Modificar los campos si se proporcionan valores no nulos
            if (nuevoNombre != null && !nuevoNombre.isEmpty()) {
                usuario.setNombreUsuario(nuevoNombre);
            }
            if (nuevoTelefono != null && !nuevoTelefono.isEmpty()) {
                usuario.setTelefonoUsuario(nuevoTelefono);
            }
            if (nuevaFoto != null && nuevaFoto.length > 0) {
                usuario.setFotoUsuario(nuevaFoto);
            }

            // Guardar los cambios
            usuarioRepositorios.save(usuario);
            return true;  // Retornar true si la modificación fue exitosa
        }

        // Si no se encuentra el usuario, retornamos false
        return false;
    }

    /**
     * Autentica un usuario utilizando su email y contraseña.
     * <p>
     * Busca el usuario en la base de datos y, si se encuentra, convierte el {@link Usuario}
     * a un {@link UsuarioDto} para transferir los datos.
     * </p>
     *
     * @param email    email del usuario
     * @param password contraseña del usuario
     * @return un {@link Optional} que contiene el {@link UsuarioDto} si el usuario es encontrado; de lo contrario, un Optional vacío
     */
    /*@Override
    public Optional<String> loginUsuario(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepositorios.findByEmailUsuario(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Verificar si las contraseñas coinciden
            if (passwordEncoder.matches(password, usuario.getPasswdUsuario())) {
                // Generar el token JWT si las credenciales son correctas
                return Optional.of(jwtUtil.generarToken(email)); // Devuelve el token JWT
            }
        }
        // Si no se encuentra el usuario o la contraseña es incorrecta, retornar Optional vacío
        return Optional.empty();
    }*/
    
}


