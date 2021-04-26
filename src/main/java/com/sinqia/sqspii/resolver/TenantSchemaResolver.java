package com.sinqia.sqspii.resolver;

import static com.sinqia.sqspii.util.MultiTenantConstants.DEFAULT_TENANT_ID;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

import com.sinqia.sqspii.context.TenantContext;

@Component
public class TenantSchemaResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String t = TenantContext.getCurrentTenant();
        if (t != null) {
            return t;
        } else {
            return DEFAULT_TENANT_ID;
        }
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}