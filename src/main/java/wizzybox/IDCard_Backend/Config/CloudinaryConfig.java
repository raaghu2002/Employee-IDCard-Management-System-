package wizzybox.IDCard_Backend.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "employeephoto",
                "api_key", "951773785157282",
                "api_secret", "MyPj0irxpBoFpH-omw34iiDXtA8"
        ));
    }
}
