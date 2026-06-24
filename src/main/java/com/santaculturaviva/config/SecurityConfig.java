package com.santaculturaviva.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

@Bean
public SecurityFilterChain securityFilterChain(
    HttpSecurity http
) throws Exception {

    http
        .authorizeHttpRequests(
            autorizacion -> autorizacion

                /*
                 * Solamente el administrador
                 * puede eliminar alertas.
                 */
                .requestMatchers(
                    HttpMethod.POST,
                    "/admin/alertas/*/eliminar"
                )
                .hasRole("ADMINISTRADOR")

                /*
                 * Administrador, editor y revisor
                 * pueden acceder al módulo de alertas.
                 */
                .requestMatchers(
                    "/admin/alertas/**"
                )
                .hasAnyRole(
                    "ADMINISTRADOR",
                    "EDITOR",
                    "REVISOR"
                )

                /*
                 * Gestión exclusiva del administrador.
                 */
                .requestMatchers(
                    "/admin/usuarios/**",
                    "/admin/roles/**",
                    "/admin/mensajes/**",
                    "/admin/suscriptores/**"
                )
                .hasRole("ADMINISTRADOR")

                /*
                 * Las demás rutas administrativas
                 * requieren rol administrador.
                 */
                .requestMatchers(
                    "/admin/**"
                )
                .hasRole("ADMINISTRADOR")

                /*
                 * El resto del sitio permanece público.
                 */
                .anyRequest()
                .permitAll()
        )
        .formLogin(
            formulario -> formulario
                .defaultSuccessUrl("/", false)
                .permitAll()
        )
        .logout(
            cierre -> cierre
                .logoutSuccessUrl("/")
                .permitAll()
        );

    return http.build();
}
}
