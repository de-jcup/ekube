package de.jcup.ekube.core.bouncycastle;

import static org.junit.Assert.*;

import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.junit.Test;

public class JcaPEMKeyConverterAvailableTest {

    
    @Test
    public void JcaPEMKeyConverter_can_be_created() {
        /* just create a new instance */
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        
        assertNotNull(converter);
    }
}
