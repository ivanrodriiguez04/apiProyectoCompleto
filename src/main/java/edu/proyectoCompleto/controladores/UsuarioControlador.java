package edu.proyectoCompleto.controladores;

import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import edu.proyectoCompleto.dtos.UsuarioDto;
import edu.proyectoCompleto.servicios.GestionUsuarioInterfaz;
import edu.proyectoCompleto.utils.JwtUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

/**
 * Controlador REST para manejar las operaciones relacionadas con los usuarios.
 * <p>
 * Proporciona endpoints para crear, eliminar, autenticar y modificar usuarios.
 * La ruta base para todos los endpoints es {@code /api/usuarios}.
 * </p>
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {
	private final GestionUsuarioInterfaz usuarioInterfaz;
	private final AuthenticationManager authenticationManager; // Para autenticar a los usuarios

	@Autowired
	private JwtUtil jwtUtil; // Inyectamos el JwtUtil para generar el token JWT

	/**
     * Constructor que inyecta la dependencia de {@link GestionUsuarioInterfaz} y AuthenticationManager.
     *
     * @param usuarioInterfaz instancia del servicio de usuarios
     * @param authenticationManager instancia del AuthenticationManager
     */
    @Autowired
    public UsuarioControlador(GestionUsuarioInterfaz usuarioInterfaz, AuthenticationManager authenticationManager) {
        this.usuarioInterfaz = usuarioInterfaz;
        this.authenticationManager = authenticationManager;
    }

	/**
	 * Endpoint para crear un nuevo usuario.
	 *
	 * @param usuarioDto datos del usuario a crear
	 * @return ResponseEntity con el mensaje de éxito o error
	 */
	@PostMapping
	public ResponseEntity<String> crearUsuario(@RequestBody UsuarioDto usuarioDto) {
		try {
			usuarioInterfaz.altaUsuario(usuarioDto);
			return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado con éxito.");
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al crear el usuario: " + e.getMessage());
		}
	}

	/**
	 * Endpoint para eliminar un usuario por su nombre.
	 *
	 * @param nombreUsuario nombre del usuario a eliminar
	 * @return ResponseEntity con el mensaje de éxito o error
	 */
	@DeleteMapping("/{nicknameUsuario}")
	public ResponseEntity<String> eliminarUsuario(@PathVariable String nicknameUsuario) {
		try {
			usuarioInterfaz.eliminarUsuario(nicknameUsuario);
			return ResponseEntity.ok("Usuario eliminado con éxito.");
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado: " + nicknameUsuario);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al eliminar el usuario: " + e.getMessage());
		}
	}

	/**
	 * Endpoint para modificar los campos nombre, teléfono e imagen de un usuario.
	 *
	 * @param idUsuario     identificador del usuario a modificar
	 * @param nuevoNombre   nuevo nombre del usuario
	 * @param nuevoTelefono nuevo teléfono del usuario
	 * @param nuevaFoto     nueva foto del usuario (como archivo en multipart)
	 * @return ResponseEntity con el mensaje de éxito o error
	 */
	@PutMapping("/{idUsuario}")
	public ResponseEntity<String> modificarUsuario(@PathVariable long idUsuario,
			@RequestParam(required = false) String nuevoNombre, @RequestParam(required = false) String nuevoTelefono,
			@RequestParam(required = false) MultipartFile nuevaFoto) {

		try {
			byte[] fotoBytes = null;

			if (nuevaFoto != null && !nuevaFoto.isEmpty()) {
				fotoBytes = nuevaFoto.getBytes(); // Convertir la foto a bytes
			}

			boolean exito = usuarioInterfaz.modificarUsuario(idUsuario, nuevoNombre, nuevoTelefono, fotoBytes);

			if (exito) {
				return ResponseEntity.ok("Usuario modificado con éxito.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
			}
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la foto.");
		}
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@Valid @RequestParam @Email String email,
			@Valid @RequestParam @NotBlank @Size(min = 6) String password) {
		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(email, password));

			// Generamos el token JWT
			String token = jwtUtil.generarToken(email);

			return ResponseEntity.ok("Token JWT: " + token);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
		}
	}
}
