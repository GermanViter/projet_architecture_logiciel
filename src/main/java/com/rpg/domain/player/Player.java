package com.rpg.domain.player;

public final class Player {
    private final Pseudonym pseudonym;
    private final Email email;
    private final Password password;

    public Player(Pseudonym pseudonym, Email email, Password password) {
        this.pseudonym = pseudonym;
        this.email = email;
        this.password = password;
    }

    public Pseudonym pseudonym() {
        return pseudonym;
    }

    public Email email() {
        return email;
    }

    public Password password() {
        return password;
    }
}
