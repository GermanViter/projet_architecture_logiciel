package com.rpg.domain.lobby;

import com.rpg.domain.hero.Hero;
import com.rpg.domain.hero.HeroName;
import com.rpg.domain.player.Pseudonym;

import java.util.Objects;
import java.util.Optional;

public final class LobbyMember {
    private final Pseudonym pseudonym;
    private final Hero hero;
    private final boolean isReady;

    public LobbyMember(Pseudonym pseudonym) {
        this.pseudonym = Objects.requireNonNull(pseudonym);
        this.hero = null;
        this.isReady = false;
    }

    private LobbyMember(Pseudonym pseudonym, Hero hero, boolean isReady) {
        this.pseudonym = pseudonym;
        this.hero = hero;
        this.isReady = isReady;
    }

    public Pseudonym pseudonym() {
        return pseudonym;
    }

    public Optional<HeroName> selectedHeroName() {
        return Optional.ofNullable(hero).map(Hero::name);
    }

    public boolean hasSelected(HeroName heroName) {
        return selectedHeroName().filter(heroName::equals).isPresent();
    }

    public boolean isReady() {
        return isReady;
    }

    public LobbyMember withHero(Hero newHero) {
        return new LobbyMember(this.pseudonym, newHero, this.isReady);
    }

    public LobbyMember withReady(boolean ready) {
        return new LobbyMember(this.pseudonym, this.hero, ready);
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
