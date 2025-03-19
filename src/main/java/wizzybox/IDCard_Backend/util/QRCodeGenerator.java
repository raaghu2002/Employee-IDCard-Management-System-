package wizzybox.IDCard_Backend.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
public class QRCodeGenerator {

    private final Cloudinary cloudinary;

    @Autowired
    public QRCodeGenerator(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String generateQRCode(String data, String fullFilePath) throws WriterException, IOException {
        File file = new File(fullFilePath);
        File dir = file.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }

        int width = 300;
        int height = 300;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);
        Path path = FileSystems.getDefault().getPath(fullFilePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        // ✅ Upload QR Code to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        String qrCodeUrl = (String) uploadResult.get("secure_url");

        // ✅ Delete the local file after upload
        file.delete();

        return qrCodeUrl; // ✅ Return the Cloudinary URL
    }
}
