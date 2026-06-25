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
                 * Página de inicio de sesión,
                 * errores y recursos estáticos.
                 */
                .requestMatchers(
                    "/login",
                    "/error",
                    "/favicon.ico",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/images/**"
                )
                .permitAll()

                /*
                 * Cualquier usuario autenticado
                 * puede entrar a su panel.
                 */
                .requestMatchers("/panel")
                .authenticated()

                /*
                 * Solo el administrador puede
                 * eliminar una alerta.
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
                 * Cualquier otra ruta administrativa
                 * requiere rol administrador.
                 */
                .requestMatchers("/admin/**")
                .hasRole("ADMINISTRADOR")

                /*
                 * El resto de Santa Cultura Viva
                 * permanece accesible públicamente.
                 */
                .anyRequest()
                .permitAll()
        )
        .formLogin(
            formulario -> formulario
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/panel", true)
                .failureUrl("/login?error")
                .permitAll()
        )
        .logout(
            cierre -> cierre
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );

    return http.build();
}

}
