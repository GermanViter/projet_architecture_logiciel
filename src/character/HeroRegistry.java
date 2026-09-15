package character;
import java.util.ArrayList;


public class HeroRegistry {


    private ArrayList<Hero> heroes = new ArrayList<>();

    public void addHero(Hero hero) {
        if(nameAlreadyExists(hero.name())){
            throw  new DuplicateHeroNameException(hero.name());

        }

        heroes.add(hero);
    }
    private boolean nameAlreadyExists(HeroName name) {
        for (Hero existingHero : heroes) {
            if (existingHero.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
    public void showAllHeroes() {
        if (heroes.isEmpty()) {
            System.out.println("no heroes found");
            return;
        }
        System.out.println("=== List of heroes ===");
        for (Hero a : heroes) {
            System.out.println(a.toString());


        }
    }

}

