package wizzybox.IDCard_Backend.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class QRCodeGenerator {

    private final Cloudinary cloudinary;

    public QRCodeGenerator(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String generateQRCode(String data) {
        int width = 300;
        int height = 300;

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

            // Convert QR code to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] qrCodeBytes = outputStream.toByteArray();

            // Validate QR Code byte array
            if (qrCodeBytes.length == 0) {
                System.out.println("Error: QR Code byte array is empty!");
                return null;
            }

            // Upload to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(qrCodeBytes, ObjectUtils.asMap("resource_type", "image"));
            System.out.println("Cloudinary Upload Response: " + uploadResult);

            // Return Cloudinary URL
            return uploadResult.get("secure_url").toString();

        } catch (WriterException e) {
            System.out.println("QR Code Generation Failed: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error Writing QR Code Image: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Cloudinary Upload Failed: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // Return null if an error occurs
    }
}
