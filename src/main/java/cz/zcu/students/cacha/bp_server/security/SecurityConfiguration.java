package cz.zcu.students.cacha.bp_server.security;

import cz.zcu.students.cacha.bp_server.services.AuthUserService;
import cz.zcu.students.cacha.bp_server.shared.RolesConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/users/register", "/users/login", "/institutions_images/**", "/exhibits_images/**", "/info_labels_images/**").permitAll()
                .antMatchers(HttpMethod.POST, "/exhibits/{institutionId}").permitAll()
                .antMatchers(HttpMethod.GET, "/institutions", "/institutions/ordered", "/exhibits/all/{institutionId}").permitAll()
                .antMatchers("/users/**").authenticated()
                .antMatchers(HttpMethod.GET, "/institutions/myInstitution").authenticated()
                .antMatchers(HttpMethod.POST, "/institutions/myInstitution").authenticated()
                .antMatchers("/translations/rate/{exhibitId}/{languageId}", "/translations/like/{translationId}").hasAnyAuthority(RolesConstants.ROLE_TRANSLATOR, RolesConstants.ROLE_INSTITUTION_OWNER)
                .antMatchers("/translations/official/{translationId}").hasAnyAuthority(RolesConstants.ROLE_INSTITUTION_OWNER)
                .antMatchers("/exhibits/translate/{institutionId}", "/translations/**").hasAuthority(RolesConstants.ROLE_TRANSLATOR)
                .antMatchers("/institutions/**", "/exhibits/**").hasAuthority(RolesConstants.ROLE_INSTITUTION_OWNER)
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authUserService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
