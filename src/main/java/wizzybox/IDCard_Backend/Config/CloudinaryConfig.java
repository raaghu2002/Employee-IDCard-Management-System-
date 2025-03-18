package wizzybox.IDCard_Backend.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    private final Cloudinary cloudinary;

    public CloudinaryConfig(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    @Bean
    public Cloudinary cloudinary() {
        return cloudinary;
    }

    // Upload method inside the same class
    public Map uploadFile(byte[] fileBytes) throws IOException {
        return cloudinary.uploader().upload(fileBytes, ObjectUtils.emptyMap());
    }
}
