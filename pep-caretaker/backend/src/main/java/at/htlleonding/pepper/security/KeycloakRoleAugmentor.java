package at.htlleonding.pepper.security;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;

@ApplicationScoped
public class KeycloakRoleAugmentor implements SecurityIdentityAugmentor {

    @ConfigProperty(name = "pepper.oidc.frontend-client-id", defaultValue = "angular-frontend")
    String frontendClientId;

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        if (identity == null || identity.isAnonymous()) {
            return Uni.createFrom().item(identity);
        }

        Principal principal = identity.getPrincipal();
        if (!(principal instanceof JsonWebToken jwt)) {
            return Uni.createFrom().item(identity);
        }

        QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder(identity);
        addRoles(builder, child(jwt.getClaim("realm_access"), "roles"));
        addRoles(builder, child(child(jwt.getClaim("resource_access"), frontendClientId), "roles"));
        return Uni.createFrom().item(builder.build());
    }

    private Object child(Object source, String key) {
        if (source instanceof Map<?, ?> map) {
            return map.get(key);
        }
        if (source instanceof JsonObject jsonObject) {
            return jsonObject.get(key);
        }
        return null;
    }

    private void addRoles(QuarkusSecurityIdentity.Builder builder, Object roles) {
        if (roles instanceof Collection<?> collection) {
            collection.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .forEach(builder::addRole);
            return;
        }
        if (roles instanceof JsonArray jsonArray) {
            jsonArray.stream()
                    .filter(value -> value.getValueType() == jakarta.json.JsonValue.ValueType.STRING)
                    .map(value -> ((jakarta.json.JsonString) value).getString())
                    .forEach(builder::addRole);
            return;
        }
        if (roles instanceof String[] array) {
            for (String role : array) {
                builder.addRole(role);
            }
        }
    }
}
