package character;
import java.lang.Character;

public class CheckName{
    private final String name;

    public CheckName(String name) {
        if (!isValid(name)) {
            throw new CharacterNameNotValidException(name);
        }
        this.name = name;
    }

    private boolean isValid(String name) {
        return lengthValid(name)
                && !name.startsWith(" ")
                && !name.endsWith(" ")
                && containLetterAndSpaceOnly(name);
    }

    private boolean lengthValid(String name) {
        return name.length() >= 2 && name.length() <= 30;
    }

    private boolean containLetterAndSpaceOnly(String name) {
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isLetter(c) && c != ' ') {
                return false;
            }
        }
        return true;
    }

    public String getName() {
        return name;
    }
}

class CharacterNameNotValidException extends RuntimeException {
    public CharacterNameNotValidException(String nom) {
        super("Le nom \"" + nom + "\" ne respecte pas les règles du personnage");
    }
}