package com.cz.config.server.meta;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * code desc
 *
 * @author Zjianru
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CONFIGS")
public class Configs implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app")
    private String app;

    @Column(name = "env")
    private String env;

    @Column(name = "ns")
    private String ns;

    @Column(name = "properties")
    private String properties;

    @Column(name = "placeholder")
    private String placeholder;
}