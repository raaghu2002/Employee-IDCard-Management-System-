package wizzybox.IDCard_Backend.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {


    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "employeephoto");
        config.put("api_key", "951773785157282");
        config.put("api_secret", "MyPj0irxpBoFpH-omw34iiDXtA8");

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", config.get("cloud_name"),
                "api_key", config.get("api_key"),
                "api_secret", config.get("api_secret")
        ));
    }
}
