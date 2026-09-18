package at.htlleonding.pepper.util;

public final class Base64Util {

    private Base64Util() {
    }

    /**
     * Strips a data-URI prefix ("data:image/png;base64,") if present and
     * returns the plain Base64 payload.
     */
    public static String extractBase64String(String image) {
        if (image == null || !image.contains(",")) {
            return image;
        }
        return image.substring(image.indexOf(",") + 1);
    }
}
