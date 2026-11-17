package com.rpg.domain.lobby;

import com.rpg.domain.hero.Hero;
import com.rpg.domain.player.Pseudonym;

import java.util.Objects;

public class LobbyMember {
    private final Pseudonym pseudonym;
    private Hero hero;
    private boolean isReady;

    public LobbyMember(Pseudonym pseudonym) {
        this.pseudonym = pseudonym;
        this.hero = null;
        this.isReady = false;
    }

    public Pseudonym pseudonym() {
        return pseudonym;
    }

    public Hero hero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbyMember that = (LobbyMember) o;
        return Objects.equals(pseudonym, that.pseudonym);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pseudonym);
    }

    @Override
    public String toString() {
        return "LobbyMember{" +
                "pseudonym=" + pseudonym +
                ", hero=" + hero +
                ", isReady=" + isReady +
                '}';
    }
}
