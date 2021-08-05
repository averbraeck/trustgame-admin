package org.transsonic.trustgame.admin.image;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class ImageUtil {

    public static void makeResponse(HttpServletResponse response, byte[] image) throws IOException {
        response.reset();

        if (checkMimeType(image, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A))
            response.setContentType("image/png");
        else if (checkMimeType(image, 0xFF, 0xD8, 0xFF)) // all jpeg types
            response.setContentType("image/jpeg");
        else if (checkMimeType(image, 0x47, 0x49, 0x46, 0x38)) // GIF87a and GIF89a
            response.setContentType("image/gif");
        else if (checkMimeType(image, 0x42, 0x4D))
            response.setContentType("image/bmp");
        else if (checkMimeType(image, 0x49, 0x49, 0x2A, 0x00) || checkMimeType(image, 0x4D, 0x4D, 0x00, 0x2A))
            response.setContentType("image/tiff"); // captures little endian and big endian
        else
            response.setContentType("image/*");

        response.setContentLengthLong(image.length);
        response.getOutputStream().write(image);
    }

    public static boolean checkMimeType(byte[] image, int... signature) {
        for (int i = 0; i < signature.length; i++) {
            if (i > image.length)
                return false;
            if (image[i] != signature[i])
                return false;
        }
        return true;
    }
}
