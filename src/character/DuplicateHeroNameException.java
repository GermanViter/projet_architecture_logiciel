package character;

public class DuplicateHeroNameException extends RuntimeException {
    public DuplicateHeroNameException(HeroName name) {
        super("Hero name already exists: " + name.getName() + "please take another name.");
    }
}
