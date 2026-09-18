package at.htlleonding.pepper.service;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Binary object storage. All image binaries live in a single MinIO bucket
 * (pep-caretaker-images); Postgres only keeps the object key as metadata.
 */
@ApplicationScoped
public class MinioService {

    @Inject
    MinioClient minioClient;

    @ConfigProperty(name = "pepper.minio.bucket")
    String bucket;

    @ConfigProperty(name = "pepper.minio.enabled", defaultValue = "true")
    boolean enabled;

    private final Map<String, StoredObject> inMemoryObjects = new ConcurrentHashMap<>();
    private volatile boolean bucketChecked;

    void onStart(@Observes StartupEvent event) {
        ensureBucket();
    }

    public String bucket() {
        return bucket;
    }

    public synchronized void ensureBucket() {
        if (!enabled || bucketChecked) {
            return;
        }
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                Log.infof("Created MinIO bucket '%s'", bucket);
            } else {
                Log.infof("MinIO bucket '%s' already exists", bucket);
            }
            bucketChecked = true;
        } catch (Exception e) {
            throw new IllegalStateException("Could not ensure MinIO bucket '" + bucket + "'", e);
        }
    }

    /**
     * Uploads the bytes under a generated key ("{keyPrefix}/{uuid}{ext}") and returns that key.
     */
    public String put(String keyPrefix, byte[] bytes, String contentType) {
        String objectKey = keyPrefix + "/" + UUID.randomUUID() + extFromMime(contentType);
        if (!enabled) {
            inMemoryObjects.put(objectKey, new StoredObject(bytes, contentType));
            return objectKey;
        }
        ensureBucket();
        try (var in = new ByteArrayInputStream(bytes)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .contentType(contentType)
                    .stream(in, bytes.length, -1)
                    .build());
        } catch (Exception e) {
            throw new IllegalStateException("Could not store object '" + objectKey + "' in MinIO", e);
        }
        return objectKey;
    }

    public byte[] get(String objectKey) {
        if (!enabled) {
            StoredObject storedObject = inMemoryObjects.get(objectKey);
            if (storedObject == null) {
                throw new IllegalStateException("Object '" + objectKey + "' not found in test object storage");
            }
            return storedObject.bytes();
        }
        ensureBucket();
        try (var stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectKey)
                .build())) {
            return stream.readAllBytes();
        } catch (Exception e) {
            throw new IllegalStateException("Could not read object '" + objectKey + "' from MinIO", e);
        }
    }

    public void delete(String objectKey) {
        if (objectKey == null) {
            return;
        }
        if (!enabled) {
            inMemoryObjects.remove(objectKey);
            return;
        }
        ensureBucket();
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            // a dangling object must not block deleting the metadata row
            Log.warnf(e, "Could not delete object '%s' from MinIO", objectKey);
        }
    }

    private record StoredObject(byte[] bytes, String contentType) {
    }

    public static String detectMime(byte[] b) {
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 0x50 && b[2] == 0x4E && b[3] == 0x47) return "image/png";
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) return "image/jpeg";
        if (b.length >= 6 && b[0] == 0x47 && b[1] == 0x49 && b[2] == 0x46 && b[3] == 0x38) return "image/gif";
        if (b.length >= 12 && b[0] == 0x52 && b[1] == 0x49 && b[2] == 0x46 && b[3] == 0x46 &&
                b[8] == 0x57 && b[9] == 0x45 && b[10] == 0x42 && b[11] == 0x50) return "image/webp";
        return "application/octet-stream";
    }

    public static String extFromMime(String mime) {
        return switch (mime) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }
}
