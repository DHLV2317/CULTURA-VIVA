package com.santaculturaviva.service;

import com.santaculturaviva.model.Rol;
import com.santaculturaviva.model.Usuario;
import com.santaculturaviva.repository.UsuarioRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class UsuarioSeguridadService
implements UserDetailsService {

private final UsuarioRepository usuarioRepository;

public UsuarioSeguridadService(
    UsuarioRepository usuarioRepository
) {
    this.usuarioRepository = usuarioRepository;
}

@Override
@Transactional(readOnly = true)
public UserDetails loadUserByUsername(
    String correoIngresado
) throws UsernameNotFoundException {

    String correo = normalizarCorreo(
        correoIngresado
    );

    Usuario usuario = usuarioRepository
        .findByCorreoIgnoreCase(correo)
        .orElseThrow(
            () -> new UsernameNotFoundException(
                "No existe una cuenta asociada al correo ingresado."
            )
        );

    List<GrantedAuthority> autoridades =
        usuario.getRoles()
            .stream()
            .filter(Rol::isActivo)
            .map(Rol::getNombre)
            .map(SimpleGrantedAuthority::new)
            .map(
                autoridad ->
                    (GrantedAuthority) autoridad
            )
            .toList();

    return User
        .withUsername(usuario.getCorreo())
        .password(usuario.getPasswordHash())
        .authorities(autoridades)
        .disabled(!usuario.isActivo())
        .accountLocked(usuario.isBloqueado())
        .accountExpired(false)
        .credentialsExpired(false)
        .build();
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

}
