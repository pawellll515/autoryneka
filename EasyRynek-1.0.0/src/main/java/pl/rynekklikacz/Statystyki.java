package pl.rynekklikacz;

/** Liczniki sesji wyświetlane obok przycisków Start / Stop / Skanuj. */
public final class Statystyki {
   private static volatile int zakupy;
   private static volatile int sztuki;
   private static volatile int zmianyStron;

   private Statystyki() {
   }

   public static void kupiono(int ilosc) {
      zakupy++;
      sztuki += Math.max(1, ilosc);
   }

   public static void zmianaStrony() {
      zmianyStron++;
   }

   public static int zakupy() {
      return zakupy;
   }

   public static int sztuki() {
      return sztuki;
   }

   public static int zmianyStron() {
      return zmianyStron;
   }
}
