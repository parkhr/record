package com.example.demo.entity;

import com.example.demo.enums.Location;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordLocation {

    @Id
    private long id;

    @Column("recordId")
    private long recordId;

    @Column("locationId")
    private long locationId;

    @Column("locationType")
    private Location locationType;

    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("updatedAt")
    private LocalDateTime updatedAt;

    @Column("deletedAt")
    private LocalDateTime deletedAt;

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
