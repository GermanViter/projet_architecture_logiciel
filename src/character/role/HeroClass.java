
package character.role;
 
public enum HeroClass {
    WARRIOR(12, 0, CharacteristicType.STRENGTH),
    RANGER(10, 0, CharacteristicType.DEXTERITY),
    MAGE(6, 10, CharacteristicType.INTELLIGENCE),
    CLERIC(10, 8, CharacteristicType.WISDOM);
 
    private final int baseHealthPoints;
    private final int baseManaPoints;
    private final CharacteristicType dominantAptitude;
 
    HeroClass(int baseHealthPoints, int baseManaPoints, CharacteristicType dominantAptitude) {
        this.baseHealthPoints = baseHealthPoints;
        this.baseManaPoints = baseManaPoints;
        this.dominantAptitude = dominantAptitude;
    }
 
    public int baseHealthPoints() {
        return baseHealthPoints;
    }

    public int baseManaPoints() {
        return baseManaPoints;
    }
 
    public CharacteristicType dominantAptitude() {
        return dominantAptitude;
    }
}
 