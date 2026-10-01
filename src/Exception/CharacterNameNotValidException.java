package character;

class CharacterNameNotValidException extends RuntimeException {
    public CharacterNameNotValidException(String nom) {
        super("the name\"" + nom + "\" does not respect the rule of the character's name");
    }
}
