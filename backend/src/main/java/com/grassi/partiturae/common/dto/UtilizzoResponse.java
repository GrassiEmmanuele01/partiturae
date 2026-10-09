package com.grassi.partiturae.common.dto;

import java.util.List;

public record UtilizzoResponse(int count, List<UtilizzoElemento> elementi) {
}