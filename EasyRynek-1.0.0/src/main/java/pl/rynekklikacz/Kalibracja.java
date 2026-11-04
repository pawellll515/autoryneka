package pl.rynekklikacz;

import net.minecraft.class_310;

/** Zapamiętuje sloty i przyciski myszy używane do zmiany stron rynku. */
public final class Kalibracja {
   private static volatile int etap;
   private static volatile String status = "";
   private static int nextSlot = -1;
   private static int nextButton;

   private Kalibracja() {}

   public static void start() {
      if (!Klikacz.rynek(class_310.method_1551().field_1755)) {
         status = "§cOtwórz rynek, aby rozpocząć kalibrację";
         etap = 0;
         return;
      }
      nextSlot = -1;
      etap = 1;
      status = "§eKliknij przycisk NASTĘPNA STRONA";
   }

   public static boolean przechwyt(int slot, int button) {
      if (etap == 0 || !Klikacz.rynek(class_310.method_1551().field_1755)) return false;
      if (etap == 1) {
         nextSlot = slot;
         nextButton = button;
         etap = 2;
         status = "§eTeraz kliknij POPRZEDNIA STRONA";
         return true;
      }
      if (etap == 2) {
         if (slot == nextSlot && button == nextButton) {
            status = "§cWybierz inny przycisk dla poprzedniej strony";
            return true;
         }
         Ustawienia.zapiszPrzyciskiStron(nextSlot, nextButton, slot, button);
         etap = 0;
         status = "§aKalibracja zakończona — przyciski zapisane";
         return true;
      }
      return false;
   }

   public static boolean aktywna() { return etap != 0; }
   public static String status() { return status; }
   public static void anuluj() { etap = 0; status = "§7Kalibracja anulowana"; }
}
