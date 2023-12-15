package com.app.messenger.repository.model;

import com.app.messenger.service.ImageTypeConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_images")
public class UserImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Convert(converter = ImageTypeConverter.class)
    private ImageType type;

    @Column(nullable = false, columnDefinition = "bytea")
    private byte[] data;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
