package mil.pusdalops.domain.kejadian;

import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;

public class AttributeEncryptor implements AttributeConverter<String, String> {

    private static final String AES = "AES";
    private static final String SECRET = "pusdalops-k2-426";

    private final Key key;
    private final Cipher cipher;    
    
    public AttributeEncryptor() throws Exception {
    	super();

    	key = new SecretKeySpec(SECRET.getBytes(), AES);
    	cipher = Cipher.getInstance(AES);
    }
    
    
	@Override
	public String convertToDatabaseColumn(String attribute) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			throw new IllegalStateException(e);
		}
	}


	@Override
	public String convertToEntityAttribute(String dbData) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
		} catch (IllegalArgumentException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			return dbData;
		}
	}

}
