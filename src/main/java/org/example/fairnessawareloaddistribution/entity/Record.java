package org.example.fairnessawareloaddistribution.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author ankushs
 */
@Getter
@Setter
@Document(collection = "records")
@ToString
public class Record {

    @JsonAlias({"_id", "id"})
    private String id;
    private String customerKey;
    private RecordStatus status;

    // any other attributes
}
