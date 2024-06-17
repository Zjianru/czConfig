package com.cz.config.server.meta;

import lombok.Builder;
import lombok.Data;

/**
* code desc
*
* @author Zjianru
*/
@Data
@Builder
public class Locks {
    private Integer id;

    private String app;
}