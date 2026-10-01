package com.rpg.domain.hero.exceptions;
import com.rpg.domain.hero.HeroName;
import java.util.*;


public class DuplicateHeroNameException extends RuntimeException {
    public DuplicateHeroNameException(HeroName name) {
        super("Hero name already exists: " + name.getName() + ". Please choose another name.");
    }
}
