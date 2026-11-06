package pl.rynekklikacz;

import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_124;
import net.minecraft.class_1703;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_7923;

/**
 * Kalibracja przycisków zmiany stron. Użytkownik włącza "Ustaw zmienianie stron",
 * otwiera menu (np. /ah) i klika najpierw "następna strona", potem "poprzednia strona".
 * Zapamiętujemy slot + nazwę + id przedmiotu oraz znormalizowany tytuł menu (działa na każdym serwerze).
 */
public final class Kalibracja {
   private static final Gson GSON = new Gson();
   private static final String[] DOMYSLNE = {"rynek", "auction", "aukcj", "market", "bazar"};

   public static class Przycisk {
      public int slot;
      public String nazwa = "";
      public String item = "";
   }

   public static class Dane {
      public String tytul = "";
      public Przycisk nast;
      public Przycisk poprz;
   }

   private static Dane dane;
   private static boolean aktywna;
   private static int krok;
   private static long okDo;
   private static String ostrzezenie = "";
   private static String cacheTytul;
   private static boolean cacheWynik;

   private Kalibracja() {
   }

   private static Path plik() {
      return FabricLoader.getInstance().getGameDir().resolve("rynekklikacz").resolve("kalibracja.json");
   }

   private static Dane dane() {
      if (dane == null) {
         dane = new Dane();

         try {
            if (Files.exists(plik())) {
               Dane d = (Dane)GSON.fromJson(Files.readString(plik(), StandardCharsets.UTF_8), Dane.class);
               if (d != null) {
                  dane = d;
               }
            }
         } catch (Exception var1) {
         }
      }

      return dane;
   }

   private static void zapisz() {
      try {
         Files.createDirectories(plik().getParent());
         Files.writeString(plik(), GSON.toJson(dane()), StandardCharsets.UTF_8);
      } catch (Exception var1) {
      }
   }

   public static boolean gotowa() {
      Dane d = dane();
      return d.nast != null && d.poprz != null;
   }

   public static boolean aktywna() {
      return aktywna;
   }

   public static void przelacz() {
      aktywna = !aktywna;
      krok = 0;
      ostrzezenie = "";
      if (aktywna) {
         Klikacz.stop();
         Snajper.stop();
         Skanowanie.stop();
      }
   }

   public static void resetuj() {
      dane = new Dane();
      cacheTytul = null;
      zapisz();
   }

   /** Tekst podpowiedzi rysowany na ekranie menu podczas kalibracji. */
   public static String podpowiedz() {
      if (aktywna) {
         if (!ostrzezenie.isEmpty()) {
            return "§c" + ostrzezenie;
         }

         return krok == 0
            ? "§eKalibracja 1/2: kliknij §fNASTĘPNA STRONA"
            : "§eKalibracja 2/2: kliknij §fPOPRZEDNIA STRONA §7(jesteś na 2. stronie)";
      }

      return System.currentTimeMillis() < okDo ? "§a✔ Zapisano przyciski zmiany stron" : "";
   }

   static String norm(String s) {
      String t = class_124.method_539(s == null ? "" : s).toLowerCase(Locale.ROOT);
      t = t.replaceAll("\\(\\s*\\d+\\s*/\\s*\\d+\\s*\\)", " ").replaceAll("[\\d\\[\\]()/#:]", " ");
      return t.replaceAll("\\s+", " ").trim();
   }

   private static String idItemu(class_1799 s) {
      return class_7923.field_41178.method_10221(s.method_7909()).toString();
   }

   /** Czy to okno to rynek/aukcje (znany tytuł albo wykalibrowany). */
   public static boolean pasuje(class_437 s) {
      if (!(s instanceof class_465)) {
         return false;
      }

      String tytul = s.method_25440().getString();
      if (tytul.equals(cacheTytul)) {
         return cacheWynik;
      }

      String n = norm(tytul);
      boolean ok = !dane().tytul.isEmpty() && dane().tytul.equals(n);
      if (!ok) {
         for (String d : DOMYSLNE) {
            if (n.contains(d)) {
               ok = true;
               break;
            }
         }
      }

      cacheTytul = tytul;
      cacheWynik = ok;
      return ok;
   }

   private static boolean pasujeDo(Przycisk p, class_1799 s) {
      if (p == null || s.method_7960()) {
         return false;
      }

      return idItemu(s).equals(p.item) && norm(s.method_7964().getString()).equals(norm(p.nazwa));
   }

   private static int szukaj(class_1703 h, Przycisk p) {
      if (p == null) {
         return -1;
      }

      int gora = h.field_7761.size() - 36;
      if (p.slot >= 0 && p.slot < gora && pasujeDo(p, h.method_7611(p.slot).method_7677())) {
         return p.slot;
      }

      for (int i = 0; i < gora; i++) {
         if (pasujeDo(p, h.method_7611(i).method_7677())) {
            return i;
         }
      }

      return -1;
   }

   /** Slot przycisku "następna strona" w tym oknie albo -1, gdy go nie ma (ostatnia strona). */
   public static int slotNast(class_1703 h) {
      return szukaj(h, dane().nast);
   }

   /** Slot przycisku "poprzednia strona" albo -1, gdy go nie ma (pierwsza strona). */
   public static int slotPoprz(class_1703 h) {
      return szukaj(h, dane().poprz);
   }

   /** Obsługa kliknięcia użytkownika w trakcie kalibracji. */
   public static void klik(class_310 mc, class_437 screen, double mx, double my, int przycisk) {
      if (!aktywna || przycisk != 0 || mc.field_1724 == null || !(screen instanceof class_465)) {
         return;
      }

      class_1703 h = mc.field_1724.field_7512;
      if (h.field_7763 == 0) {
         return;
      }

      pl.rynekklikacz.mixin.HandledScreenAccessor a = (pl.rynekklikacz.mixin.HandledScreenAccessor)screen;
      int gora = h.field_7761.size() - 36;
      class_1735 trafiony = null;

      for (int i = 0; i < gora; i++) {
         class_1735 sl = h.method_7611(i);
         int sx = a.rynekklikacz$getX() + sl.field_7873;
         int sy = a.rynekklikacz$getY() + sl.field_7872;
         if (mx >= sx && mx < sx + 16 && my >= sy && my < sy + 16) {
            trafiony = sl;
            break;
         }
      }

      if (trafiony == null || trafiony.method_7677().method_7960()) {
         return;
      }

      class_1799 st = trafiony.method_7677();
      Przycisk p = new Przycisk();
      p.slot = trafiony.field_7874;
      p.nazwa = class_124.method_539(st.method_7964().getString());
      p.item = idItemu(st);
      ostrzezenie = "";
      if (krok == 0) {
         dane().nast = p;
         dane().poprz = null;
         dane().tytul = norm(screen.method_25440().getString());
         cacheTytul = null;
         krok = 1;
      } else {
         Przycisk n = dane().nast;
         if (n != null && n.slot == p.slot && norm(n.nazwa).equals(norm(p.nazwa)) && n.item.equals(p.item)) {
            ostrzezenie = "To ten sam przycisk — kliknij POPRZEDNIA STRONA";
            return;
         }

         dane().poprz = p;
         aktywna = false;
         krok = 0;
         okDo = System.currentTimeMillis() + 4000L;
         cacheTytul = null;
         zapisz();
      }
   }
}
