package pl.rynekklikacz;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1109;
import net.minecraft.class_124;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2813;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_634;
import pl.rynekklikacz.gui.BazaScreen;
import pl.rynekklikacz.gui.ZakupToast;

public final class Snajper {
   public static final int SLOT_POTWIERDZ = 11;
   private static final long PRZERWA = 55L;
   private static final long LIMIT = 6000L;
   private static final int MAX_PROB = 30;
   private static final int ZIELONY = -10755185;
   private static final int CZERWONY = -1090725;
   private static Set<String> cele;
   private static int celeWersja;
   private static final Map<String, Analiza> analizy = new HashMap<>();
   private static long analizyCzas;
   private static int analizyBaza = -1;
   private static int analizyProfit = -1;
   private static int analizyCele = -1;
   private static final Map<String, Long> pominiete = new HashMap<>();
   private static volatile Snajper.Stan stan = Snajper.Stan.NIC;
   private static int slot;
   private static class_1799 cel = class_1799.field_8037;
   private static Wpis wpis;
   private static Analiza celAnaliza;
   private static int syncListy;
   private static int syncPotw;
   private static boolean kliknieto;
   private static long start;
   private static long ostatniKlik;
   private static long kiedyPusto;
   private static int proby;
   private static int przed;
   private static boolean czatOk;
   private static int kupione;
   public static String status = "";
   private static long przeglad;

   private Snajper() {
   }

   private static Path plik() {
      return FabricLoader.getInstance().getGameDir().resolve("rynekklikacz").resolve("cele.json");
   }

   public static Set<String> cele() {
      if (cele == null) {
         cele = new LinkedHashSet<>();

         try {
            if (Files.exists(plik())) {
               List<String> l = (List<String>)new Gson()
                  .fromJson(Files.readString(plik(), StandardCharsets.UTF_8), (new TypeToken<List<String>>() {}).getType());
               if (l != null) {
                  cele.addAll(l);
               }
            }
         } catch (Exception var1) {
         }
      }

      return cele;
   }

   public static boolean chce(String grupa) {
      return cele().contains(grupa);
   }

   public static void przelacz(String grupa) {
      if (!cele().remove(grupa)) {
         cele.add(grupa);
      }

      celeWersja++;

      try {
         Files.createDirectories(plik().getParent());
         Files.writeString(plik(), new Gson().toJson(new ArrayList<>(cele)), StandardCharsets.UTF_8);
      } catch (Exception var2) {
      }
   }

   public static int celeWersja() {
      return celeWersja;
   }

   public static boolean zajety() {
      return stan != Snajper.Stan.NIC;
   }

   public static int kupione() {
      return kupione;
   }

   public static Analiza analiza(String grupa) {
      long teraz = System.currentTimeMillis();
      int profit = Ustawienia.profit();
      boolean stare = teraz - analizyCzas > 3000L && analizyBaza != Skaner.wersja();
      if (stare || analizyProfit != profit || analizyCele != celeWersja) {
         analizy.clear();
         Map<String, List<Wpis>> grupy = new HashMap<>();

         for (Wpis w : Skaner.wszystkie()) {
            String g = w.grupa();
            if (cele().contains(g)) {
               grupy.computeIfAbsent(g, k -> new ArrayList<>()).add(w);
            }
         }

         for (Entry<String, List<Wpis>> e : grupy.entrySet()) {
            analizy.put(e.getKey(), new Analiza(e.getValue(), profit));
         }

         analizyCzas = teraz;
         analizyBaza = Skaner.wersja();
         analizyProfit = profit;
         analizyCele = celeWersja;
      }

      return analizy.get(grupa);
   }

   public static Analiza analizaDowolna(String grupa) {
      List<Wpis> l = new ArrayList<>();

      for (Wpis w : Skaner.wszystkie()) {
         if (w.grupa().equals(grupa)) {
            l.add(w);
         }
      }

      return l.isEmpty() ? null : new Analiza(l, Ustawienia.profit());
   }

   public static void widzi(class_310 mc, int s, class_1799 stack, Wpis w) {
      if (w != null && stan == Snajper.Stan.NIC && Klikacz.dziala() && !cele().isEmpty()) {
         if (cele().contains(w.grupa())) {
            long teraz = System.currentTimeMillis();
            Long pom = pominiete.get(w.klucz);
            if (pom == null || teraz >= pom) {
               Analiza an = analiza(w.grupa());
               double c = w.zaSztukeD();
               if (an == null || !an.jestLow()) {
                  status = "Widzę " + w.nazwa + " za " + BazaScreen.cenaD(w.cena) + " · za mało danych o cenie";
               } else if (!(c <= 0.0) && !(c > an.progLow)) {
                  class_1703 h = mc.field_1724.field_7512;
                  slot = s;
                  cel = stack.method_7972();
                  wpis = w;
                  celAnaliza = an;
                  syncListy = h.field_7763;
                  syncPotw = -1;
                  kliknieto = false;
                  start = teraz;
                  ostatniKlik = 0L;
                  kiedyPusto = 0L;
                  proby = 0;
                  czatOk = false;
                  przed = ile(mc, cel);
                  status = "Kupuję " + w.nazwa + " za " + BazaScreen.cenaD(w.cena);
                  stan = Snajper.Stan.OFERTA;
                  tick(mc);
               } else {
                  status = "Widzę " + w.nazwa + " za " + BazaScreen.cenaD(c) + (w.ilosc > 1 ? "/szt." : "") + " · Low ≤ " + BazaScreen.cenaD(an.progLow);
               }
            }
         }
      }
   }

   public static void czat(class_2561 t) {
      if (stan != Snajper.Stan.NIC) {
         String s = class_124.method_539(t.getString()).toLowerCase(Locale.ROOT);
         if (!s.contains("nie ") && !s.contains("brak")) {
            if (s.contains("zakupi") || s.contains("kupił") || s.contains("kupiono") || s.contains("kupiles")) {
               czatOk = true;
            }
         }
      }
   }

   private static int ile(class_310 mc, class_1799 wzor) {
      if (mc.field_1724 == null) {
         return 0;
      }

      String nazwa = wzor.method_7964().getString();
      int n = 0;

      for (class_1799 s : mc.field_1724.method_31548().field_7547) {
         if (!s.method_7960() && s.method_7909() == wzor.method_7909() && s.method_7964().getString().equals(nazwa)) {
            n += s.method_7947();
         }
      }

      return n;
   }

   private static void klik(class_310 mc, int s) {
      class_634 net = mc.method_1562();
      class_1703 h = mc.field_1724.field_7512;
      if (net != null && h.field_7763 != 0) {
         net.method_52787(new class_2813(h.field_7763, h.method_37421(), s, 0, class_1713.field_7790, class_1799.field_8037, new Int2ObjectOpenHashMap()));
         ostatniKlik = System.currentTimeMillis();
      }
   }

   public static void przeglad(class_310 mc) {
      przeglad = System.currentTimeMillis();
      if (stan == Snajper.Stan.NIC && mc.field_1724 != null && Klikacz.dziala() && !cele().isEmpty() && Klikacz.rynek(mc.field_1755)) {
         class_1703 h = mc.field_1724.field_7512;

         for (int i = 0; i < Skaner.SLOTY.length && stan == Snajper.Stan.NIC; i++) {
            Wpis w = Skaner.wpis(i);
            class_1799 s = Skaner.widziany(i);
            int sl = Skaner.SLOTY[i];
            if (w != null && s != null && sl < h.field_7761.size() && class_1799.method_7973(h.method_7611(sl).method_7677(), s)) {
               widzi(mc, sl, s, w);
            }
         }
      }
   }

   public static void tick(class_310 mc) {
      if (mc.field_1724 != null) {
         if (stan != Snajper.Stan.NIC) {
            long teraz = System.currentTimeMillis();
            if (czatOk || ile(mc, cel) > przed) {
               sukces(mc);
            } else if (teraz - start > 6000L || proby > 30) {
               porazka(mc, proby > 0 ? "Nie weszło po " + proby + " próbach" : "Oferta zniknęła");
            } else if (teraz - ostatniKlik >= 55L) {
               class_1703 h = mc.field_1724.field_7512;
               if (h.field_7763 == 0) {
                  if (kiedyPusto == 0L) {
                     kiedyPusto = teraz;
                  }

                  if (teraz - kiedyPusto > 1500L) {
                     porazka(mc, "Okno się zamknęło");
                  }
               } else {
                  kiedyPusto = 0L;
                  if (stan == Snajper.Stan.OFERTA) {
                     class_1799 s = slot < h.field_7761.size() ? h.method_7611(slot).method_7677() : class_1799.field_8037;
                     boolean jest = class_1799.method_31577(s, cel);
                     if (h.field_7763 != syncListy) {
                        if (!kliknieto && (jest || Klikacz.rynek(mc.field_1755))) {
                           syncListy = h.field_7763;
                        } else {
                           stan = Snajper.Stan.POTWIERDZ;
                           syncPotw = h.field_7763;
                        }
                     }

                     if (stan == Snajper.Stan.OFERTA) {
                        if (jest) {
                           klik(mc, slot);
                           kliknieto = true;
                        } else if (!kliknieto && teraz - start > 1200L) {
                           porazka(mc, "Ktoś był szybszy");
                        }

                        return;
                     }
                  }

                  if (stan == Snajper.Stan.POTWIERDZ) {
                     if (h.field_7763 == syncPotw) {
                        if (11 < h.field_7761.size()) {
                           klik(mc, 11);
                           proby++;
                           status = "Potwierdzam zakup · próba " + proby;
                        }
                     } else {
                        stan = Snajper.Stan.OFERTA;
                        syncListy = h.field_7763;
                        kliknieto = false;
                     }
                  }
               }
            }
         }
      }
   }

   private static void sukces(class_310 mc) {
      Wpis w = wpis;
      Analiza an = celAnaliza;
      stan = Snajper.Stan.NIC;
      kupione++;
      Statystyki.kupiono(w.ilosc);
      pominiete.put(w.klucz, System.currentTimeMillis() + 120000L);
      Skaner.wygas(w.klucz);
      Zakupy.Zakup z = new Zakupy.Zakup();
      z.czas = System.currentTimeMillis();
      z.nazwa = w.nazwa;
      z.id = w.id;
      z.grupa = w.grupa();
      z.ilosc = w.ilosc;
      z.cena = w.cena;
      z.rynkowa = an.rynkowa;
      z.zysk = an.zysk(w.zaSztukeD()) * w.ilosc;
      z.proby = Math.max(1, proby);
      z.item = w.item;
      z.auto = Ustawienia.autoWystaw();
      Zakupy.dodaj(z);
      if (z.auto && mc.field_1755 != null) {
         mc.field_1724.method_7346();
      }

      status = "Kupiono " + w.nazwa + " za " + BazaScreen.cenaD(w.cena);
      mc.method_1566()
         .method_1999(
            new ZakupToast(
               cel,
               "Kupiono!  LOW",
               w.nazwa + " · " + BazaScreen.cenaD(w.cena) + (w.ilosc > 1 ? " (" + w.ilosc + " szt.)" : ""),
               "zysk po 3%: +" + BazaScreen.cenaD(z.zysk) + " · rynek " + BazaScreen.krotkoD(an.rynkowa),
               -10755185,
               -10755185
            )
         );
      mc.method_1483().method_4873(class_1109.method_4758(class_3417.field_14709, 1.6F));
      mc.method_1483().method_4873(class_1109.method_4758((class_3414)class_3417.field_14725.comp_349(), 1.8F));
   }

   private static void porazka(class_310 mc, String powod) {
      Wpis w = wpis;
      stan = Snajper.Stan.NIC;
      pominiete.put(w.klucz, System.currentTimeMillis() + (proby > 0 ? 60000 : 5000));
      status = "Nie kupiono: " + powod;
      mc.method_1566().method_1999(new ZakupToast(cel, "Nie udało się", w.nazwa + " · " + BazaScreen.cenaD(w.cena), powod, -1090725, -7629657));
      mc.method_1483().method_4873(class_1109.method_4758((class_3414)class_3417.field_14624.comp_349(), 0.8F));
   }

   public static void stop() {
      stan = Snajper.Stan.NIC;
   }

   private enum Stan {
      NIC,
      OFERTA,
      POTWIERDZ;
   }
}
