package pl.rynekklikacz;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_124;
import net.minecraft.class_1703;
import net.minecraft.class_1799;
import net.minecraft.class_1887;
import net.minecraft.class_1890;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
import net.minecraft.class_7225.class_7874;

public final class Skaner {
   public static final int[] SLOTY = new int[]{2, 3, 4, 5, 6, 7, 8, 11, 12, 13, 14, 15, 16, 17, 20, 21, 22, 23, 24, 25, 26, 29, 30, 31, 32, 33, 34, 35};
   private static final Gson GSON = new Gson();
   private static final Pattern CZAS = Pattern.compile("\\d+\\s*(d|h|m|s|dni|godz|min|sek)\\b", 2);
   private static final Pattern SLOWO = Pattern.compile("(?<!\\p{L})(koszt|cena|ceny|price)(?!\\p{L})", 66);
   private static final Pattern JEDNOSTKA = Pattern.compile("(\\d+)\\s*([dhms])\\b", 2);
   private static final Pattern NAWIAS = Pattern.compile("\\(\\s*\\$?\\s*(\\d[\\d,. ]*?)\\s*\\$?\\s*\\)");
   private static final Pattern LICZBA = Pattern.compile("(\\d[\\d .,]*)\\s*(?:(mld|mln|tys|k|m|b)\\b)?", 2);
   private static final ExecutorService ZAPIS = Executors.newSingleThreadExecutor(r -> {
      Thread t = new Thread(r, "RynekKlikacz-zapis");
      t.setDaemon(true);
      return t;
   });
   private static final class_1799[] ostatnie = new class_1799[SLOTY.length];
   private static final String[] klucze = new String[SLOTY.length];
   private static final Wpis[] wpisy = new Wpis[SLOTY.length];
   private static class_1703 handler;
   private static Map<String, Wpis> baza;
   private static boolean brudna;
   private static long zapisano;
   private static int wersja;
   private static int nowe;

   private Skaner() {
   }

   private static Path plik() {
      return FabricLoader.getInstance().getGameDir().resolve("rynekklikacz").resolve("baza.json");
   }

   private static Map<String, Wpis> baza() {
      if (baza == null) {
         baza = new LinkedHashMap<>();

         try {
            if (Files.exists(plik())) {
               List<Wpis> lista = (List<Wpis>)GSON.fromJson(Files.readString(plik(), StandardCharsets.UTF_8), (new TypeToken<List<Wpis>>() {}).getType());
               if (lista != null) {
                  for (Wpis w : lista) {
                     w.klucz = klucz(w);
                     w.cena = cena(w.lore);
                     w.wygasa = wygasa(w.lore, w.ostatni);
                     Wpis jest = baza.get(w.klucz);
                     if (jest != null) {
                        Wpis nowszy = w.ostatni >= jest.ostatni ? w : jest;
                        nowszy.pierwszy = Math.min(w.pierwszy, jest.pierwszy);
                        nowszy.razy = w.razy + jest.razy;
                        w = nowszy;
                        brudna = true;
                     }

                     baza.put(w.klucz, w);
                  }
               }
            }
         } catch (Exception var5) {
         }
      }

      return baza;
   }

   public static List<Wpis> wszystkie() {
      return new ArrayList<>(baza().values());
   }

   public static int ile() {
      return baza().size();
   }

   public static int wersja() {
      return wersja;
   }

   public static int nowe() {
      return nowe;
   }

   public static void wyczysc() {
      baza().clear();
      Arrays.fill(ostatnie, null);
      Arrays.fill(klucze, null);
      nowe = 0;
      wersja++;
      brudna = true;
      zapisz();
   }

   public static void skanuj(class_310 mc) {
      skanuj(mc, false);
   }

   public static void skanuj(class_310 mc, boolean wymus) {
      if (!Klikacz.dziala() || wymus) {
         if (mc.field_1724 != null && mc.field_1687 != null && Klikacz.rynek(mc.field_1755)) {
            class_1703 h = mc.field_1724.field_7512;
            if (h != handler) {
               handler = h;
               Arrays.fill(ostatnie, null);
               Arrays.fill(klucze, null);
               Arrays.fill(wpisy, null);
            }

            class_7874 reg = mc.field_1687.method_30349();

            for (int i = 0; i < SLOTY.length; i++) {
               if (SLOTY[i] < h.field_7761.size()) {
                  class_1799 s = h.method_7611(SLOTY[i]).method_7677();
                  if (ostatnie[i] == null || !class_1799.method_7973(ostatnie[i], s)) {
                     ostatnie[i] = s.method_7972();
                     if (s.method_7960()) {
                        klucze[i] = null;
                        wpisy[i] = null;
                     } else {
                        try {
                           wpisy[i] = dodaj(i, ostatnie[i], reg);
                        } catch (Exception var7) {
                        }
                     }
                  }
               }
            }
         } else {
            handler = null;
         }
      }
   }

   public static String grupa(class_1799 s) {
      Wpis w = new Wpis();
      w.id = class_7923.field_41178.method_10221(s.method_7909()).toString();
      w.nazwa = class_124.method_539(s.method_7964().getString());
      w.kolorowa = kolorowa(s);

      for (Entry<class_6880<class_1887>> e : class_1890.method_57532(s).method_57539()) {
         w.enchanty.add(class_124.method_539(class_1887.method_8179((class_6880)e.getKey(), e.getIntValue()).getString()));
      }

      return w.grupa();
   }

   static void wygas(String klucz) {
      Wpis w = baza().get(klucz);
      if (w != null) {
         w.wygasa = System.currentTimeMillis() - 1L;
         wersja++;
         brudna = true;
      }
   }

   static Wpis wpis(int i) {
      return wpisy[i];
   }

   static class_1799 widziany(int i) {
      return ostatnie[i];
   }

   static boolean kolorowa(class_1799 s) {
      class_2561 n = (class_2561)s.method_57824(class_9334.field_49631);
      if (n == null) {
         n = (class_2561)s.method_57824(class_9334.field_50239);
      }

      if (n == null) {
         return false;
      }

      if (n.getString().contains("§")) {
         return true;
      }

      boolean[] kolor = new boolean[]{false};
      n.method_27658((styl, tekst) -> {
         if (styl.method_10973() != null && !tekst.isBlank()) {
            kolor[0] = true;
         }

         return Optional.empty();
      }, class_2583.field_24360);
      return kolor[0];
   }

   private static String klucz(Wpis w) {
      return w.id + "#" + w.ilosc + "#" + w.nazwa + "#" + String.join("|", w.enchanty) + "#" + normalizuj(w.lore);
   }

   private static Wpis dodaj(int i, class_1799 s, class_7874 reg) {
      Wpis w = new Wpis();
      w.id = class_7923.field_41178.method_10221(s.method_7909()).toString();
      w.nazwa = class_124.method_539(s.method_7964().getString());
      w.ilosc = s.method_7947();
      w.kolorowa = kolorowa(s);

      for (Entry<class_6880<class_1887>> e : class_1890.method_57532(s).method_57539()) {
         w.enchanty.add(class_124.method_539(class_1887.method_8179((class_6880)e.getKey(), e.getIntValue()).getString()));
      }

      class_9290 lore = (class_9290)s.method_57824(class_9334.field_49632);
      if (lore != null) {
         for (class_2561 t : lore.comp_2400()) {
            w.lore.add(class_124.method_539(t.getString()));
         }
      }

      String k = klucz(w);
      w.klucz = k;
      w.cena = cena(w.lore);
      if (k.equals(klucze[i])) {
         return w;
      }

      klucze[i] = k;
      long teraz = System.currentTimeMillis();
      Wpis stary = baza().remove(k);
      if (stary != null) {
         w.pierwszy = stary.pierwszy;
         w.razy = stary.razy + 1;
      } else {
         w.pierwszy = teraz;
         w.razy = 1;
         nowe++;
      }

      w.ostatni = teraz;
      w.cena = cena(w.lore);
      w.wygasa = wygasa(w.lore, teraz);
      w.klucz = k;
      w.ustaw(s, s.method_57358(reg).toString());
      baza.put(k, w);
      Historia.dodaj(w, teraz);
      wersja++;
      brudna = true;
      return w;
   }

   private static String normalizuj(List<String> lore) {
      StringBuilder sb = new StringBuilder();

      for (String l : lore) {
         boolean cenowa = l.contains("$") || SLOWO.matcher(l).find();
         if (cenowa || !CZAS.matcher(l).find()) {
            sb.append(l.trim()).append('|');
         }
      }

      return sb.toString();
   }

   private static Long dokladna(String s) {
      s = s.replace(" ", "");
      if (s.isEmpty()) {
         return null;
      }

      boolean przecinek = s.contains(",");
      boolean kropka = s.contains(".");
      if (przecinek && kropka) {
         if (s.lastIndexOf(44) > s.lastIndexOf(46)) {
            s = s.replace(".", "").replace(',', '.');
         } else {
            s = s.replace(",", "");
         }
      } else if (przecinek) {
         s = s.matches("\\d{1,3}(,\\d{3})+") ? s.replace(",", "") : s.replace(',', '.');
      } else if (kropka) {
         s = s.matches("\\d{1,3}(\\.\\d{3})+") ? s.replace(".", "") : s;
      }

      try {
         return Math.round(Double.parseDouble(s));
      } catch (NumberFormatException e) {
         return null;
      }
   }

   static long cena(List<String> lore) {
      for (String l : lore) {
         if (SLOWO.matcher(l).find()) {
            Long v = cenaZLinii(l);
            if (v != null) {
               return v;
            }
         }
      }

      for (String l : lore) {
         if (l.contains("$")) {
            Long v = cenaZLinii(l);
            if (v != null) {
               return v;
            }
         }
      }

      return -1L;
   }

   static long wygasa(List<String> lore, long widziano) {
      for (String l : lore) {
         String low = l.toLowerCase(Locale.ROOT);
         if (low.contains("wyga") || low.contains("expire")) {
            Matcher m = JEDNOSTKA.matcher(l);
            long ms = 0L;

            boolean jest;
            for (jest = false; m.find(); jest = true) {
               long v = Long.parseLong(m.group(1));

               ms += v * switch (m.group(2).toLowerCase(Locale.ROOT)) {
                  case "d" -> 86400000L;
                  case "h" -> 3600000L;
                  case "m" -> 60000L;
                  default -> 1000L;
               };
            }

            if (jest) {
               return widziano + ms;
            }
         }
      }

      return 0L;
   }

   private static Long cenaZLinii(String l) {
      Matcher nw = NAWIAS.matcher(l);

      while (nw.find()) {
         Long v = dokladna(nw.group(1));
         if (v != null) {
            return v;
         }
      }

      Matcher m = LICZBA.matcher(l);
      int dolar = l.indexOf(36);
      if (dolar >= 0) {
         m.region(dolar, l.length());
      }

      while (m.find()) {
         String n = m.group(1).trim().replace(" ", "");
         String suf = m.group(2) == null ? "" : m.group(2).toLowerCase(Locale.ROOT);
         if (!n.isEmpty()) {
            if (!suf.isEmpty()) {
               try {
                  double var10000 = Double.parseDouble(n.replace(',', '.'));

                  return Math.round(var10000 * switch (suf) {
                     case "k", "tys" -> 1000;
                     case "m", "mln" -> 1000000;
                     case "b", "mld" -> 1000000000;
                     default -> 1;
                  });
               } catch (NumberFormatException var8) {
               }
            } else {
               Long v = dokladna(n);
               if (v != null) {
                  return v;
               }
            }
         }
      }

      return null;
   }

   public static void tick() {
      Historia.tick();
      if (brudna && System.currentTimeMillis() - zapisano > 3000L) {
         zapisz();
      }
   }

   public static void zapisz() {
      Runnable r = zrzut();
      if (r != null) {
         ZAPIS.execute(r);
      }
   }

   public static void zapiszTeraz() {
      Runnable r = zrzut();
      if (r != null) {
         r.run();
      }
   }

   private static Runnable zrzut() {
      if (brudna && baza != null) {
         brudna = false;
         zapisano = System.currentTimeMillis();
         List<Wpis> kopia = new ArrayList<>(baza.size());

         for (Wpis w : baza.values()) {
            kopia.add(w.kopia());
         }

         return () -> {
            synchronized (ZAPIS) {
               try {
                  Path p = plik();
                  Files.createDirectories(p.getParent());
                  Path tmp = p.resolveSibling("baza.json.tmp");
                  Files.writeString(tmp, GSON.toJson(kopia), StandardCharsets.UTF_8);
                  Files.move(tmp, p, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
               } catch (Exception var5) {
               }
            }
         };
      } else {
         return null;
      }
   }
}
