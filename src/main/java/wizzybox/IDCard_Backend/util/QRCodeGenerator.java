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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {
//    public static String generateQRCode(String data, String fullFilePath) throws WriterException, IOException {
//        File file = new File(fullFilePath);
//        File dir = file.getParentFile();
//        if (dir != null && !dir.exists()) {
//            dir.mkdirs();
//        }
//
//        int width = 300;
//        int height = 300;
//
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        Map<EncodeHintType, Object> hints = new HashMap<>();
//        hints.put(EncodeHintType.MARGIN, 1);
//
//        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);
//        Path path = FileSystems.getDefault().getPath(fullFilePath);
//        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
//
//        return fullFilePath;
//    }
@Autowired
private static Cloudinary cloudinary;

    public static String generateQRCode(String data) throws WriterException, IOException {
        int width = 300;
        int height = 300;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

        // Convert QR code to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        byte[] qrCodeBytes = outputStream.toByteArray();

        // Upload to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(qrCodeBytes, ObjectUtils.asMap("resource_type", "image"));
        return uploadResult.get("secure_url").toString(); // Return Cloudinary URL
    }
}
