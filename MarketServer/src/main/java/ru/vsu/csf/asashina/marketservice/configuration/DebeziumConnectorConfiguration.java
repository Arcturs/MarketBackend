package ru.vsu.csf.asashina.marketservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class DebeziumConnectorConfiguration {

    private static final String CONNECTOR_NAME = "market-connector";
    private static final String CONNECTOR_CLASS = "io.debezium.connector.postgresql.PostgresConnector";
    private static final String CONNECTOR_OFFSET_STORAGE = "org.apache.kafka.connect.storage.FileOffsetBackingStore";
    private static final String CONNECTOR_OFFSET_FLUSH_INTERVAL_MS = "60000";
    private static final String CONNECTOR_DATABASE_SERVER_ID = "36755";
    private static final String CONNECTOR_DATABASE_HISTORY = "io.debezium.relational.history.FileDatabaseHistory";
    private static final String CONNECTOR_TABLE_INCLUDE_LIST = "public.product";
    private static final String CONNECTOR_TOPIC_PREFIX = "product";
    private static final String CONNECTOR_PLUGIN_NAME = "pgoutput";

    private static final String OFFSET_STORAGE_FILE_NAME = "/tmp/offsets_";
    private static final String DB_HISTORY_FILE_NAME = "/tmp/dbhistory_";
    private static final String DAT_SUFFIX = ".dat";

    @Value("${spring.datasource.host}")
    private String dbHostname;

    @Value("${spring.datasource.port}")
    private String dbPort;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.dbname}")
    private String dbName;

    @Bean
    public io.debezium.config.Configuration marketConnector() throws IOException {
        File offsetStorageTempFile = File.createTempFile(OFFSET_STORAGE_FILE_NAME, DAT_SUFFIX);
        File dbHistoryTempFile = File.createTempFile(DB_HISTORY_FILE_NAME, DAT_SUFFIX);
        return io.debezium.config.Configuration.create()
                .with("name", CONNECTOR_NAME)
                .with("connector.class", CONNECTOR_CLASS)
                .with("offset.storage", CONNECTOR_OFFSET_STORAGE)
                .with("offset.storage.file.filename", offsetStorageTempFile.getAbsolutePath())
                .with("offset.flush.interval.ms", CONNECTOR_OFFSET_FLUSH_INTERVAL_MS)
                .with("database.hostname", dbHostname)
                .with("database.port", dbPort)
                .with("database.user", dbUsername)
                .with("database.password", dbPassword)
                .with("database.dbname", dbName)
                .with("database.server.id", CONNECTOR_DATABASE_SERVER_ID)
                .with("database.server.name", dbHostname + "-" + dbName)
                .with("database.history", CONNECTOR_DATABASE_HISTORY)
                .with("database.history.file.filename", dbHistoryTempFile.getAbsolutePath())
                .with("table.include.list", CONNECTOR_TABLE_INCLUDE_LIST)
                .with("topic.prefix", CONNECTOR_TOPIC_PREFIX)
                .with("plugin.name", CONNECTOR_PLUGIN_NAME)
                .build();
    }
}
