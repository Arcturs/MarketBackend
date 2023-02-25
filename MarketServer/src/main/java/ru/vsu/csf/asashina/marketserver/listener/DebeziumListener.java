package ru.vsu.csf.asashina.marketserver.listener;

import io.debezium.config.Configuration;
import io.debezium.data.Envelope.Operation;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import ru.vsu.csf.asashina.marketserver.handler.ProductChangeEventHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;

import static io.debezium.data.Envelope.FieldName.*;
import static io.debezium.data.Envelope.Operation.DELETE;
import static java.util.stream.Collectors.toMap;

@Component
@Slf4j
public class DebeziumListener {

    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private final Map<String, ProductChangeEventHandler> eventHandlerMap;

    public DebeziumListener(Configuration customerConnectorConfiguration,
                            Map<String, ProductChangeEventHandler> eventHandlerMap) {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(customerConnectorConfiguration.asProperties())
                .notifying(this::handleChangeEvent)
                .build();
        this.eventHandlerMap = eventHandlerMap;
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();
        Struct sourceRecordChangeValue = (Struct) sourceRecord.value();

        if (sourceRecordChangeValue == null) {
            return;
        }

        Operation operation = Operation.forCode((String) sourceRecordChangeValue.get(OPERATION));
        String record = operation == DELETE ? BEFORE : AFTER;

        Struct struct = (Struct) sourceRecordChangeValue.get(record);
        Map<String, Object> payload = mapStructToMap(struct);
        String operationValue = operation.toString();
        if (!eventHandlerMap.containsKey(operationValue)) {
            return;
        }
        eventHandlerMap.get(operationValue).logChangeEvent(payload);
    }

    private Map<String, Object> mapStructToMap(Struct struct) {
        return struct.schema().fields().stream()
                .map(Field::name)
                .filter(fieldName -> struct.get(fieldName) != null)
                .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                .collect(toMap(Pair::getFirst, Pair::getSecond));
    }

    @PostConstruct
    private void start() {
        Executors.newSingleThreadExecutor().execute(debeziumEngine);
    }

    @PreDestroy
    private void stop() throws IOException {
        if (debeziumEngine != null) {
            debeziumEngine.close();
        }
    }
}
