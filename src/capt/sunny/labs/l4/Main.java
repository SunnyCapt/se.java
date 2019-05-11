package capt.sunny.labs.l4;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


public class Main implements Runnable {
    private static void pprint(String text) {
        System.out.println("[PERFORMANCE_INFO]: " + text);
    }

    public static void main(String[] args) {
        new Main().run();
    }

    @Override
    public void run() {
        //рождается
        //.......
        //доживает до 666 лет

        String heroArt =
                "\n                                                 KARLSON                                                                                                   \n\n" +
                        "                                                  /((((///(#/                                                                                                  \n" +
                        "                                             (#,*//////////((//(,                                                                                              \n" +
                        "                                          ,#%%&(/////***/////////(#*                                                                                           \n" +
                        "                                         ..,#////////*****/////////((*                                                                                         \n" +
                        "                                          ,(%(//////////*///////////(/*#             .*                                                                        \n" +
                        "                                          ,#/(//////(/////////////////((/,         #%%%#((,                                                                    \n" +
                        "                                          .#/((///(/,/((///////////////(((/       #%##(((#(#*                                                                  \n" +
                        "                                           *(((,/(((((/(/*/,*/((((((((//(/(//..   /####(((##(#,                                                                \n" +
                        "                                             *(, .(/(/.,,..,,,,,,,,,*//((((*,,,**  /%%#######(#,                                                               \n" +
                        "                                                  *,,.../(*,..,,,,,,,..,,,,,,,,(*   %%########(%,                                                              \n" +
                        "                                                   ,*,,.  *...,,,,,,,,,,..,,,.  .##%%(%#%%####%##     .%##%%##%&.                                              \n" +
                        "                                                .*......*%&,..,,,,,,,,,,.,,*,  (########%#&%###(%.  *#%%###((###%,                                             \n" +
                        "                                                (/,......,.,,*/.,,..,..,,,,   *########%#%%#(%%##* *%%%###((((#%%,                                             \n" +
                        "                                                 .(,***,(*.....,/(%/,.,,*#.  .%#######%##%%%##%%%#*%####(((###(%.                                              \n" +
                        "                                                      /*,,..,/,..**(*,*%/    (######%%######%##(%####%%##(/(#(.                                                \n" +
                        "                                                       ,*,,,,,,,,**/%###    ,%#####%#######%#**,*%####(#/,                                                     \n" +
                        "                                                     .(#%%%%%#%%##%##%%     ,####%#%%%%%%%*,,,,,,/%%###*                                                       \n" +
                        "                                         .***     ,,*%#%%%%%##########,    .*#####%%%%%/,,,,,,,,,,/##%%#%#*                                                    \n" +
                        "                                        ,%**,,,,,,**.,/%%%%#########(/     ,(##%%%%%%%#(,,,,,,*,,,/#####%%#%/                                                  \n" +
                        "                                          ,*,.*/*,.,**#%%%##########&      /#%%%%%%%%(,,,,,,,,,,**#((####%%%,                                                \n" +
                        "                                         ,(,**,,(/.*#*#%%%#########%/./#*. /###%%*   ****,,,,,,,*,##((#####%*                                                \n" +
                        "                                        (**,****,,,//,(%%%%%%%##%#(**###%%*/%              ,(#(((,.,.((((((##%#                                                \n" +
                        "                                        .*/*   ,*****, (%%%%%%/.    *//((/*                      ....(########/                                                \n" +
                        "                                                       .%#%(..       ./,*                         .....##(%###.                                                \n" +
                        "                                             ,((((#(    **....                                    .,/.                                                         \n" +
                        "                                          .%##((##**(*    ,.. .                                  .../                                                          \n" +
                        "                                          ,%##((/((//(    .,/....                               ..,/.                                                          \n" +
                        "                                           /%#((//##//((*.    **.......                       ....,.                                                           \n" +
                        "                                            ,%#((((##/(#(..      //,...... ...  . .,.       ...,,(,                                                            \n" +
                        "                                               #((//////#%%#*.   ,,,,/(((((((/**/          ....,/                                                              \n" +
                        "                                               /##((((/(#(./,.#/,      ,(((,.    .        ..../                                                                \n" +
                        "                                               .(##(((((,    .      .##(////(/,*(           ./                                                                 \n" +
                        "                                                  *//,              %((/****/(#((/.       ./,                                                                  \n" +
                        "                                                                    .###/////#/((/(%%#(,,,*                                                                    \n" +
                        "                                                                      /##%((/*//***///((                                                                       \n" +
                        "                                                                          /##%((////((                                                                         ";
        //и после всего этого:
        System.out.println(heroArt);
        Clothes pants = new Clothes("штаны", "синий", 36);
        pprint("Создали синие штаны с площадью материала 36 м^2");
        Clothes shirt = new Clothes("рубашка", "клетчатый", 27);
        pprint("Создали клетчатую рубашку с площадью материала 27 м^2");
        Clothes socks = new Clothes("носки", "полосатый", 12);
        pprint("Создали полосатые носки с площадью материала 12 м^2");
        Clothes sheets = new Clothes("простыни", "белый", 50);
        pprint("Создали белые простыни с площадью материала 50 м^2");
        Device propeller = new Device("пропеллер", DeviceType.MOTOR);

        LusterInt luster = new Luster("обыкновенная", 12, 40, new Point());
        pprint("Создали люстру с радиусом 12 и высотой  40 расположенный в точке (0,0,0)");


        HumanInt hero = new Human(666, "Карлсон", 127,
                new Device[]{propeller},
                new Clothes[]{sheets, pants, shirt, socks}, new double[]{0, 20, 40});
        Updateable changeHeroLocation = hero::updateLocation;
        //Updateable changeHeroLocation = (_p) -> hero.updateLocation(_p);
        pprint("В точке (0,20,40) создали персонажа \"Карлсон\" обладающего созданным моторным девайсом \"Пропеллер\" и одетого во всю созданную прежде одежду.");


        hero.changeDeviceState(propeller);
        pprint("Включили пропелер Карлсона");
        if (hero.showWear("простыни").canRazvevatsya()) {
            hero.showWear("простыни").makeLoose();
            pprint("Из-за работы пропеллера простыни накинутые на нашего героя стали развеваться");
        }
        hero.scream();
        pprint("Карлсон стал вопить как неадекватный, пытаясь тем самым казаться призраком");
        System.out.println("\tПризрак взлетел и начал кружиться вокруг люстры");

//летит по спирали к люстре
        final double firstRadius = sqrt(pow(hero.getLocation()[0], 2) + pow(hero.getLocation()[2], 2)); //начальное расстояние hero от центра люстры

        for (double t = 0; sqrt(pow(hero.getLocation()[0], 2) + pow(hero.getLocation()[2], 2)) > (luster.getRadius() + 1); t += 0.1) {
            double[] newPoint = Point.Func.spiral(firstRadius, t);
            try {
                changeHeroLocation.update(new double[]{newPoint[0], hero.getLocation()[1], newPoint[1]});
            } catch (ParametersException e) {
                e.printStackTrace();
            }
            if (((int) t) % 41 == 0) {
                System.out.println("\tОн летит по спирали....");
            }
        }

        luster.addToHooks(sheets);
        pprint("Простыни зацепиллись за люстру");
        hero.removeWear(sheets);
        pprint("И сползли с Карлсона");
        System.out.println("\tПризрак раскрыл себя: им оказался Карлсон!!!");

//летит по кругу
        final double lastRadius = sqrt(pow(hero.getLocation()[0], 2) + pow(hero.getLocation()[2], 2)); //конечное расстояние hero отт центра люстры
        System.out.println("\tОднако он не сразу заметил, в какое положение попал");
        pprint("Траектория изменилась на окружность с радусом чуть большим радиуса люстры");
        boolean flage = true;
        int laps = 0;
        for (double t = 0; ; t += 0.1) {
            if (t >= 2 * Math.PI) {
                t -= 2 * Math.PI; //период функций cos и sin 2П
            }
            double[] newPoint = Point.Func.circle(lastRadius, t);
            try {
                changeHeroLocation.update(new double[]{newPoint[0], hero.getLocation()[1], newPoint[1]});
            } catch (ParametersException e) {
                e.printStackTrace();
            }
            if (((int) t) == 5 && flage) {
                System.out.println(String.format("\tНо в итоге Карлсон увидел, что простыни спали с него и %s развеваются " +
                        "на люстре", sheets.isStagger() ? "" : "не"));
                flage = false;
            }
            laps++;
            if (laps == 100) {
                break;
            }
        }

//конец
    }


}
