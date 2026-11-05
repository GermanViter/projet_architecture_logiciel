package com.rpg.api.player;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePlayerRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    public String pseudonym;

    @NotBlank
    @Email
    public String email;

    @NotBlank
    @Size(min = 8)
    public String password;
}
