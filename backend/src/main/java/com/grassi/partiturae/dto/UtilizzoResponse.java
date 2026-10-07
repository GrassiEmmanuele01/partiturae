package com.grassi.partiturae.dto;

import java.util.List;

public record UtilizzoResponse(int count, List<UtilizzoElemento> elementi) {
}