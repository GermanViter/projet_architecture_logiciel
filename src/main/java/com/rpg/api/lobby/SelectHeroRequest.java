package com.rpg.api.lobby;

import jakarta.validation.constraints.NotBlank;

public class SelectHeroRequest {
    @NotBlank
    public String heroName;
}
