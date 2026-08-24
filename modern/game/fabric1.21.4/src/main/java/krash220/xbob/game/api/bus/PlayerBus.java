package krash220.xbob.game.api.bus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlayerBus {

  private static List<Consumer<Float>> attack = new ArrayList<>();

  public static void registerAttack(Consumer<Float> attack) {
    PlayerBus.attack.add(attack);
  }

  public static void onAttack(float damage) {
    for (Consumer<Float> handler : attack) {
      handler.accept(damage);
    }
  }

  public static void onBreakBlock() {
    for (Consumer<Float> handler : attack) {
      handler.accept(0.6f);
    }
  }
}
