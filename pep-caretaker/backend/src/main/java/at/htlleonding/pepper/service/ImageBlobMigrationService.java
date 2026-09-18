package at.htlleonding.pepper.service;

import io.agroal.api.AgroalDataSource;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@ApplicationScoped
public class ImageBlobMigrationService {

    @Inject
    AgroalDataSource dataSource;

    @Inject
    MinioService minioService;

    void migrateExistingLobs(@Observes StartupEvent event) {
        try (Connection connection = dataSource.getConnection()) {
            if (!hasTable(connection, "pe_image") || !hasColumn(connection, "pe_image", "i_image")) {
                return;
            }

            // both are StartupEvent observers without a defined order, so don't rely on MinioService.onStart
            minioService.ensureBucket();

            connection.setAutoCommit(false);
            ensureNewColumns(connection);

            int migrated = migrateRows(connection);
            connection.commit();

            if (migrated > 0) {
                Log.infof("Migrated %d image LOB(s) from PostgreSQL to MinIO bucket '%s'", migrated, minioService.bucket());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not migrate existing PostgreSQL image LOBs to MinIO", e);
        }
    }

    private int migrateRows(Connection connection) throws SQLException {
        int migrated = 0;
        try (PreparedStatement select = connection.prepareStatement("""
                select i_id, i_image
                from pe_image
                where i_image is not null
                  and i_object_key is null
                """);
             ResultSet resultSet = select.executeQuery();
             PreparedStatement update = connection.prepareStatement("""
                     update pe_image
                     set i_object_key = ?, i_content_type = ?
                     where i_id = ?
                     """)) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("i_id");
                byte[] bytes = readBlob(resultSet);
                if (bytes == null || bytes.length == 0) {
                    continue;
                }

                String contentType = MinioService.detectMime(bytes);
                String objectKey = minioService.put("migration/" + id, bytes, contentType);
                update.setString(1, objectKey);
                update.setString(2, contentType);
                update.setLong(3, id);
                update.executeUpdate();
                migrated++;
            }
        }
        return migrated;
    }

    private byte[] readBlob(ResultSet resultSet) throws SQLException {
        Blob blob = resultSet.getBlob("i_image");
        if (blob != null) {
            return blob.getBytes(1, Math.toIntExact(blob.length()));
        }
        return resultSet.getBytes("i_image");
    }

    private void ensureNewColumns(Connection connection) throws SQLException {
        if (!hasColumn(connection, "pe_image", "i_object_key")) {
            try (PreparedStatement statement = connection.prepareStatement("alter table pe_image add column i_object_key varchar(255)")) {
                statement.executeUpdate();
            }
        }
        if (!hasColumn(connection, "pe_image", "i_content_type")) {
            try (PreparedStatement statement = connection.prepareStatement("alter table pe_image add column i_content_type varchar(255)")) {
                statement.executeUpdate();
            }
        }
    }

    private boolean hasTable(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    private boolean hasColumn(Connection connection, String tableName, String columnName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getColumns(null, null, tableName, columnName)) {
            return resultSet.next();
        }
    }
}
