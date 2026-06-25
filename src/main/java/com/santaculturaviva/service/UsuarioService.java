package com.santaculturaviva.service;

import com.santaculturaviva.dto.UsuarioFormulario;
import com.santaculturaviva.model.Rol;
import com.santaculturaviva.model.Usuario;
import com.santaculturaviva.repository.RolRepository;
import com.santaculturaviva.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
        UsuarioRepository usuarioRepository,
        RolRepository rolRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository
            .findAllByOrderByFechaCreacionDesc();
    }

    @Transactional(readOnly = true)
    public List<Rol> listarRolesActivos() {
        return rolRepository
            .findByActivoTrueOrderByEtiquetaAsc();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "No se encontró el usuario solicitado."
                )
            );
    }

    @Transactional(readOnly = true)
    public UsuarioFormulario obtenerFormulario(
        Long id
    ) {
        Usuario usuario = buscarPorId(id);

        UsuarioFormulario formulario =
            new UsuarioFormulario();

        formulario.setNombreSocial(
            usuario.getNombreSocial()
        );

        formulario.setPronombre(
            usuario.getPronombre()
        );

        formulario.setCorreo(
            usuario.getCorreo()
        );

        formulario.setActivo(
            usuario.isActivo()
        );

        formulario.setBloqueado(
            usuario.isBloqueado()
        );

        formulario.setCorreoVerificado(
            usuario.isCorreoVerificado()
        );

        Set<Long> rolesIds = usuario
            .getRoles()
            .stream()
            .map(Rol::getId)
            .collect(
                Collectors.toCollection(
                    LinkedHashSet::new
                )
            );

        formulario.setRolesIds(rolesIds);

        return formulario;
    }

    @Transactional(readOnly = true)
    public boolean correoEnUso(
        String correo,
        Long usuarioId
    ) {
        String correoNormalizado =
            normalizarCorreo(correo);

        if (usuarioId == null) {
            return usuarioRepository
                .existsByCorreoIgnoreCase(
                    correoNormalizado
                );
        }

        return usuarioRepository
            .existsByCorreoIgnoreCaseAndIdNot(
                correoNormalizado,
                usuarioId
            );
    }

    @Transactional
    public Usuario crearUsuario(
        UsuarioFormulario formulario
    ) {
        Usuario usuario = new Usuario();

        aplicarDatosBasicos(
            usuario,
            formulario
        );

        usuario.setPasswordHash(
            passwordEncoder.encode(
                formulario.getPassword()
            )
        );

        usuario.setRoles(
            obtenerRoles(
                formulario.getRolesIds()
            )
        );

        usuario.setIntentosFallidos(0);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizarUsuario(
        Long id,
        UsuarioFormulario formulario
    ) {
        Usuario usuario = buscarPorId(id);

        aplicarDatosBasicos(
            usuario,
            formulario
        );

        if (
            formulario.getPassword() != null
            && !formulario.getPassword().isBlank()
        ) {
            usuario.setPasswordHash(
                passwordEncoder.encode(
                    formulario.getPassword()
                )
            );
        }

        usuario.setRoles(
            obtenerRoles(
                formulario.getRolesIds()
            )
        );

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarEstado(
        Long id,
        String correoUsuarioActual
    ) {
        Usuario usuario = buscarPorId(id);

        boolean esUsuarioActual =
            usuario.getCorreo().equalsIgnoreCase(
                correoUsuarioActual
            );

        if (
            esUsuarioActual
            && usuario.isActivo()
        ) {
            throw new IllegalStateException(
                "No puedes desactivar tu propia cuenta."
            );
        }

        usuario.setActivo(
            !usuario.isActivo()
        );

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarBloqueo(
        Long id,
        String correoUsuarioActual
    ) {
        Usuario usuario = buscarPorId(id);

        boolean esUsuarioActual =
            usuario.getCorreo().equalsIgnoreCase(
                correoUsuarioActual
            );

        if (
            esUsuarioActual
            && !usuario.isBloqueado()
        ) {
            throw new IllegalStateException(
                "No puedes bloquear tu propia cuenta."
            );
        }

        usuario.setBloqueado(
            !usuario.isBloqueado()
        );

        if (!usuario.isBloqueado()) {
            usuario.setIntentosFallidos(0);
        }

        usuarioRepository.save(usuario);
    }

    private void aplicarDatosBasicos(
        Usuario usuario,
        UsuarioFormulario formulario
    ) {
        usuario.setNombreSocial(
            formulario.getNombreSocial().trim()
        );

        usuario.setPronombre(
            limpiarTexto(
                formulario.getPronombre()
            )
        );

        usuario.setCorreo(
            normalizarCorreo(
                formulario.getCorreo()
            )
        );

        usuario.setActivo(
            formulario.isActivo()
        );

        usuario.setBloqueado(
            formulario.isBloqueado()
        );

        usuario.setCorreoVerificado(
            formulario.isCorreoVerificado()
        );
    }

    private Set<Rol> obtenerRoles(
        Set<Long> rolesIds
    ) {
        if (
            rolesIds == null
            || rolesIds.isEmpty()
        ) {
            throw new IllegalArgumentException(
                "El usuario debe tener al menos un rol."
            );
        }

        List<Rol> rolesEncontrados =
            rolRepository.findAllById(rolesIds);

        Set<Rol> rolesActivos =
            rolesEncontrados
                .stream()
                .filter(Rol::isActivo)
                .collect(
                    Collectors.toCollection(
                        LinkedHashSet::new
                    )
                );

        if (
            rolesActivos.size()
            != rolesIds.size()
        ) {
            throw new IllegalArgumentException(
                "Uno o más roles seleccionados no existen o están inactivos."
            );
        }

        return rolesActivos;
    }

    private String normalizarCorreo(
        String correo
    ) {
        if (correo == null) {
            return "";
        }

        return correo
            .trim()
            .toLowerCase(Locale.ROOT);
    }

    private String limpiarTexto(
        String texto
    ) {
        if (
            texto == null
            || texto.isBlank()
        ) {
            return null;
        }

        return texto.trim();
    }
}