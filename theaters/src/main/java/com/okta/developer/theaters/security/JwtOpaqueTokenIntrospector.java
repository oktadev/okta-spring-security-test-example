package com.okta.developer.theaters.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.NimbusReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtOpaqueTokenIntrospector implements ReactiveOpaqueTokenIntrospector {

    @Autowired
    private OAuth2ResourceServerProperties oAuth2;
    private ReactiveOpaqueTokenIntrospector delegate;

    @PostConstruct
    private void setUp() {
        delegate =
            new NimbusReactiveOpaqueTokenIntrospector(oAuth2.getOpaquetoken().getIntrospectionUri(),
                oAuth2.getOpaquetoken().getClientId(),
                oAuth2.getOpaquetoken().getClientSecret());
    }

    public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {
        return this.delegate.introspect(token)
            .flatMap(principal -> enhance(principal));
    }

    private Mono<OAuth2AuthenticatedPrincipal> enhance(OAuth2AuthenticatedPrincipal principal) {
        Collection<GrantedAuthority> authorities = extractAuthorities(principal);
        OAuth2AuthenticatedPrincipal enhanced = new DefaultOAuth2AuthenticatedPrincipal(principal.getAttributes(), authorities);
        return Mono.just(enhanced);
    }

    private Collection<GrantedAuthority> extractAuthorities(OAuth2AuthenticatedPrincipal principal) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(principal.getAuthorities());

        List<String> groups = principal.getAttribute("groups");
        if (groups != null) {
            groups.stream()
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);
        }

        return authorities;
    }
}
