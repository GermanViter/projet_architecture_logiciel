package character;

public class InvalidWeaponException extends RuntimeException {
    public InvalidWeaponException(String name, int damage) {

        super("the weapon \"" + name + "\" has invalid damage : " + damage);
    }
}
