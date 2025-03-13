package wizzybox.IDCard_Backend.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageProcessor {
    public static void processImage(File inputFile, File outputFile) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputFile);
        BufferedImage resizedImage = resizeImage(originalImage, 300, 300); // Resize to 300x300 pixels
        ImageIO.write(resizedImage, "png", outputFile); // Save as PNG
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return resizedImage;
    }
}
